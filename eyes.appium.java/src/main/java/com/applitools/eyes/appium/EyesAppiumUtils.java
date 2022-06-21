/*
 * Applitools software.
 */
package com.applitools.eyes.appium;

import com.applitools.eyes.Logger;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.eyes.selenium.positioning.ImageRotation;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.applitools.utils.ImageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.NoSuchContextException;
import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.remote.Response;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EyesAppiumUtils {

    private static final String NATIVE_APP = "NATIVE_APP";
    static String SCROLLVIEW_XPATH = "//*[@scrollable='true']";
    private static String FIRST_VIS_XPATH = "/*[@firstVisible='true']";

    public static final String STATUS_BAR = "statusBar";
    public static final String NAVIGATION_BAR = "navigationBar";

    public static WebElement getFirstScrollableView(WebDriver driver) {
        return driver.findElement(By.xpath(SCROLLVIEW_XPATH));
    }

    public static WebElement getFirstVisibleChild(WebElement element) {
        return element.findElement(By.xpath(FIRST_VIS_XPATH));
    }

    public static void scrollByDirection(AppiumDriver driver, String direction) {
        EyesAppiumUtils.scrollByDirection(driver, direction, 1.0);
    }

    public static void scrollByDirection(AppiumDriver driver, String direction, double distanceRatio) {
        HashMap<String, String> args = new HashMap<>();
        args.put("direction", direction);
        args.put("distance", Double.toString(distanceRatio));
        driver.executeScript("mobile: scroll", args);
    }

    public static void scrollBackToElement(AndroidDriver driver, RemoteWebElement scroller, RemoteWebElement scrollToEl) {
        HashMap<String, String> args = new HashMap<>();
        args.put("elementId", scroller.getId());
        args.put("elementToId", scrollToEl.getId());
        driver.executeScript("mobile: scrollBackTo", args);
    }

    public static ContentSize getContentSize(AppiumDriver driver, WebElement element) {
        ContentSize contentSize;
        try {
            String contentSizeJson = element.getAttribute("contentSize");
            ObjectMapper objectMapper = new ObjectMapper();
            contentSize = objectMapper.readValue(contentSizeJson, ContentSize.class);
            contentSize.setDriver(driver);
        } catch (WebDriverException | IOException e) {
            contentSize = new ContentSize();
            contentSize.height = element.getSize().getHeight();
            contentSize.width = element.getSize().getWidth();
            contentSize.top = element.getLocation().getY();
            contentSize.left = element.getLocation().getX();
            contentSize.scrollableOffset = element.getSize().getHeight();
            contentSize.setDriver(driver);
        }
        return contentSize;
    }

    public static Map<String, Object> getSessionDetails(RemoteWebDriver driver) {
        Map<String, Object> sessionDetails = new HashMap<>();
        sessionDetails.putAll(driver.getCapabilities().asMap());
        if (driver instanceof AppiumDriver) {
            Response response = ((AppiumDriver) driver).execute("getSession");
            Map<String, Object> resultMap = Map.class.cast(response.getValue());
            sessionDetails.putAll(resultMap);
        }

        return sessionDetails;
    }

    public static Object getSessionDetail(RemoteWebDriver driver, String detailName) {
        Map<String, Object> details = getSessionDetails(driver);
        return getSessionDetail(details, detailName);
    }

    public static Object getSessionDetail(Map<String, Object> details, String detailName) {
        Object detailValue = details.get("appium:" + detailName);
        if (detailValue == null) {
            detailValue = details.get(detailName);
        }
        return detailValue;
    }

    @Nullable
    public static LastScrollData getLastScrollData(AppiumDriver driver) {
        Object lastScrollDataObj = getSessionDetail(driver, "lastScrollData");
        if (lastScrollDataObj == null) {
            return null;
        }
        Map<String, Long> scrollData = (Map<String, Long>) lastScrollDataObj;
        return new LastScrollData(scrollData);
    }

    public static WebDriver context(RemoteWebDriver driver, String name) {
        Preconditions.checkNotNull(name, "Must supply a context name");

        try {
            execute(driver, "switchToContext", ImmutableMap.of("name", name));
            return driver;
        } catch (WebDriverException var3) {
            throw new NoSuchContextException(var3.getMessage(), var3);
        }
    }

    public static String getContext(RemoteWebDriver driver) {
        Response response = execute(driver, "getCurrentContextHandle");
        String contextName = String.valueOf(response.getValue());
        return "null".equalsIgnoreCase(contextName) ? null : contextName;
    }

    public static Set<String> getContextHandles(RemoteWebDriver driver) {
        Response response = execute(driver, "getContextHandles");
        Object value = response.getValue();

        try {
            List<String> returnedValues = (List) value;
            return new LinkedHashSet(returnedValues);
        } catch (ClassCastException var4) {
            throw new WebDriverException("Returned value cannot be converted to List<String>: " + value, var4);
        }
    }

    public static ScreenOrientation getOrientation(RemoteWebDriver driver) {
        Response response = execute(driver, "getScreenOrientation");
        String orientation = response.getValue().toString().toLowerCase();
        if (orientation.equals(ScreenOrientation.LANDSCAPE.value())) {
            return ScreenOrientation.LANDSCAPE;
        } else if (orientation.equals(ScreenOrientation.PORTRAIT.value())) {
            return ScreenOrientation.PORTRAIT;
        } else {
            throw new WebDriverException("Unexpected orientation returned: " + orientation);
        }
    }

    private static Response execute(RemoteWebDriver driver, String driverCommand) {
        return execute(driver, driverCommand, ImmutableMap.of());
    }

    private static Response execute(RemoteWebDriver driver, String driverCommand, Map<String, ?> parameters) {
        try {
            Method execute = driver.getClass().getMethod("execute", String.class, Map.class);
            Response response = (Response) execute.invoke(driver, driverCommand, parameters);
            return response;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isLandscapeOrientation(Logger logger, WebDriver driver) {
        // We can only find orientation for mobile devices.
        if (!EyesDriverUtils.isMobileDevice(driver)) {
            return false;
        }

        RemoteWebDriver appiumDriver = (RemoteWebDriver) EyesDriverUtils.getUnderlyingDriver(driver);

        String originalContext = null;
        try {
            // We must be in native context in order to ask for orientation,
            // because of an Appium bug.
            originalContext = getContext(appiumDriver);
            if (getContextHandles(appiumDriver).size() > 1 &&
                    !originalContext.equalsIgnoreCase(NATIVE_APP)) {
                context(appiumDriver, NATIVE_APP);
            } else {
                originalContext = null;
            }
        } catch (WebDriverException e) {
            originalContext = null;
        }
        try {
            ScreenOrientation orientation = getOrientation(appiumDriver);
            return orientation == ScreenOrientation.LANDSCAPE;
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, e);
            return false;
        } finally {
            if (originalContext != null) {
                context(appiumDriver, originalContext);
            }
        }
    }

    public static int tryAutomaticRotation(Logger logger, WebDriver driver, BufferedImage image) {
        ArgumentGuard.notNull(logger, "logger");
        int degrees = 0;
        try {
            if (EyesDriverUtils.isMobileDevice(driver) &&
                    isLandscapeOrientation(logger, driver)
                    && image.getHeight() > image.getWidth()) {
                // For Android, we need to rotate images to the right, and
                // for iOS to the left.
                degrees = EyesDriverUtils.isAndroid(driver) ? 90 : -90;
            }
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, e);
        }
        return degrees;
    }

    /**
     * Rotates the image as necessary. The rotation is either manually forced
     * by passing a non-null ImageRotation, or automatically inferred.
     *
     * @param driver   The underlying driver which produced the screenshot.
     * @param image    The image to normalize.
     * @param rotation The degrees by which to rotate the image:
     *                 positive values = clockwise rotation,
     *                 negative values = counter-clockwise,
     *                 0 = force no rotation,
     *                 null = rotate automatically as needed.
     * @return A normalized image.
     */
    public static BufferedImage normalizeRotation(Logger logger, WebDriver driver,
                                                  BufferedImage image, ImageRotation rotation) {
        ArgumentGuard.notNull(driver, "driver");
        ArgumentGuard.notNull(image, "image");
        int degrees;
        if (rotation != null) {
            degrees = rotation.getRotation();
        } else {
            degrees = EyesAppiumUtils.tryAutomaticRotation(logger, driver, image);
        }

        return ImageUtils.rotateImage(image, degrees);
    }

    public static Map<String, Integer> getSystemBarsHeights(EyesAppiumDriver driver) {
        Map<String, Integer> systemBarHeights = new HashMap();
        systemBarHeights.put(STATUS_BAR, null);
        systemBarHeights.put(NAVIGATION_BAR, null);

        try {
            if (EyesDriverUtils.isAndroid(driver)) {
                fillSystemBarsHeightsMap((AndroidDriver) driver.getRemoteWebDriver(), systemBarHeights);
                if (systemBarHeights.get(STATUS_BAR) == 1 && systemBarHeights.get(NAVIGATION_BAR) == 1) {
                    fillSystemBarsHeightsMap(driver, systemBarHeights);
                }
            } else {
                fillSystemBarsHeightsMap(driver, systemBarHeights);
            }
        } catch (Exception ignored) {
            fillSystemBarsHeightsMap(driver, systemBarHeights);
        }

        return systemBarHeights;
    }

    private static Integer getSystemBar(String systemBarName, Map<String, Map<String, Object>> systemBars) {
        if (systemBars.containsKey(systemBarName)) {
            String value = String.valueOf(systemBars.get(systemBarName));
            if (getSystemBarVisibility(value)) {
                return getSystemBarHeight(value);
            }
        }

        return null;
    }

    private static Boolean getSystemBarVisibility(String systemBarDetails) {
        Pattern p = Pattern.compile("visible=(\\w+)");
        Matcher m = p.matcher(systemBarDetails);
        m.find();
        return Boolean.parseBoolean(m.group(1));
    }

    private static Integer getSystemBarHeight(String systemBarDetails) {
        Pattern p = Pattern.compile("height=(\\d+)");
        Matcher m = p.matcher(systemBarDetails);
        m.find();
        return Integer.parseInt(m.group(1));
    }

    private static void fillSystemBarsHeightsMap(EyesAppiumDriver driver, Map<String, Integer> systemBarHeights) {
        int statusBarHeight = driver.getStatusBarHeight();
        int navigationBarHeight = driver.getDeviceHeight() - driver.getViewportRect().get("height") - statusBarHeight;
        systemBarHeights.put(STATUS_BAR, statusBarHeight);
        systemBarHeights.put(NAVIGATION_BAR, navigationBarHeight);
    }

    private static void fillSystemBarsHeightsMap(AndroidDriver driver, Map<String, Integer> systemBarHeights) {
        Map<String, Map<String, Object>> systemBars = driver.getSystemBars();
        for (String systemBarName : systemBars.keySet()) {
            systemBarHeights.put(systemBarName, getSystemBar(systemBarName, systemBars));
        }
    }

    public static String getHelperLibraryVersion(EyesAppiumDriver driver, Logger logger) {
        String version = "";
        if (EyesDriverUtils.isAndroid(driver)) {
            try {
                WebElement hiddenElement = getHelperElement(driver, logger, "EyesAppiumHelper_Version");
                if (hiddenElement != null) {
                    version = hiddenElement.getText();
                }
            } catch (Exception ignored) {}
            if (version == null) {
                try {
                    WebElement hiddenElement = getHelperElement(driver, logger, "EyesAppiumHelper");
                    if (hiddenElement != null) {
                        version = "1.0.0";
                    }
                } catch (Exception ignored) {}
            }
        }
        logger.log(TraceLevel.Debug, driver.getTestId(), Stage.CHECK, Pair.of("helperLibraryVersion", version));
        return version;
    }

    // Should be used only for getting version of helper library
    private static WebElement getHelperElement(EyesAppiumDriver driver, Logger logger, String contentDescription) {
        WebElement hiddenElement = null;
        try {
            String selector = String.format("new UiSelector().description(\"%s\")", contentDescription);
            hiddenElement =  driver.getRemoteWebDriver().findElement(AppiumBy.androidUIAutomator(selector));
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
            try { // Try to search for element by XPATH. For case when element has displayed=false
                String selector = String.format("//android.widget.TextView[@content-desc=\"%s\" and @displayed=\"false\"]", contentDescription);
                hiddenElement = driver.getRemoteWebDriver().findElement(AppiumBy.xpath(selector));
            } catch (Exception e1) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
            }
        }
        return hiddenElement;
    }
}
