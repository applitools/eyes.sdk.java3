package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.selenium.exceptions.EyesDriverOperationException;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Coordinates;

import java.io.IOException;
import java.lang.reflect.*;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EyesDriverUtils {
    private static final String NATIVE_APP = "NATIVE_APP";
    private static final String PLATFORM_VERSION = "platformVersion";
    private static final String DEVICE_NAME = "deviceName";
    // See Applitools WiKi for explanation.
    private static final String JS_GET_VIEWPORT_SIZE =
            "var height = undefined;"
                    + "var width = undefined;"
                    + "  if (window.innerHeight) {height = window.innerHeight;}"
                    + "  else if (document.documentElement "
                    + "&& document.documentElement.clientHeight) "
                    + "{height = document.documentElement.clientHeight;}"
                    + "  else { var b = document.getElementsByTagName('body')[0]; "
                    + "if (b.clientHeight) {height = b.clientHeight;}"
                    + "};"
                    + " if (window.innerWidth) {width = window.innerWidth;}"
                    + " else if (document.documentElement "
                    + "&& document.documentElement.clientWidth) "
                    + "{width = document.documentElement.clientWidth;}"
                    + " else { var b = document.getElementsByTagName('body')[0]; "
                    + "if (b.clientWidth) {"
                    + "width = b.clientWidth;}"
                    + "};"
                    + "return width+';'+height;";
    private static final String JS_GET_CURRENT_SCROLL_POSITION =
            "var doc = document.documentElement; " +
                    "var x = window.scrollX || " +
                    "((window.pageXOffset || doc.scrollLeft) - (doc.clientLeft || 0));"
                    + " var y = window.scrollY || " +
                    "((window.pageYOffset || doc.scrollTop) - (doc.clientTop || 0));" +
                    "return x+';'+y;";
    // IMPORTANT: Notice there's a major difference between scrollWidth
    // and scrollHeight. While scrollWidth is the maximum between an
    // element's width and its content width, scrollHeight might be
    // smaller (!) than the clientHeight, which is why we take the
    // maximum between them.
    private static final String JS_GET_CONTENT_ENTIRE_SIZE =
            "var scrollWidth = document.documentElement.scrollWidth; " +
                    "var bodyScrollWidth = document.body.scrollWidth; " +
                    "var totalWidth = Math.max(scrollWidth, bodyScrollWidth); " +
                    "var clientHeight = document.documentElement.clientHeight; " +
                    "var bodyClientHeight = document.body.clientHeight; " +
                    "var scrollHeight = document.documentElement.scrollHeight; " +
                    "var bodyScrollHeight = document.body.scrollHeight; " +
                    "var maxDocElementHeight = Math.max(clientHeight, scrollHeight); " +
                    "var maxBodyHeight = Math.max(bodyClientHeight, bodyScrollHeight); " +
                    "var totalHeight = Math.max(maxDocElementHeight, maxBodyHeight); " +
                    "return totalWidth+';'+totalHeight;";
    private static final String[] JS_TRANSFORM_KEYS = {"transform",
            "-webkit-transform"
    };
    private static final String JS_GET_ENTIRE_PAGE_SIZE =
            "var width = Math.max(arguments[0].clientWidth, arguments[0].scrollWidth);" +
                    "var height = Math.max(arguments[0].clientHeight, arguments[0].scrollHeight);" +
                    "return width+';'+height;";

    private static String JS_GET_VISIBLE_ELEMENT_RECT;

    static {
        try {
            JS_GET_VISIBLE_ELEMENT_RECT = GeneralUtils.readInputStreamAsString(EyesDriverUtils.class.getResourceAsStream("/getVisibleRect.js"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extracts the location relative to the entire page from the coordinates
     * (e.g. as opposed to viewport)
     * @param coordinates The coordinates from which location is extracted.
     * @return The location relative to the entire page
     */
    public static Location getPageLocation(Coordinates coordinates) {
        if (coordinates == null) {
            return null;
        }

        Point p = coordinates.onPage();
        return new Location(p.getX(), p.getY());
    }

    /**
     * Extracts the location relative to the <b>viewport</b> from the
     * coordinates (e.g. as opposed to the entire page).
     * @param coordinates The coordinates from which location is extracted.
     * @return The location relative to the viewport.
     */
    public static Location getViewportLocation(Coordinates coordinates) {
        if (coordinates == null) {
            return null;
        }

        Point p = coordinates.inViewPort();
        return new Location(p.getX(), p.getY());
    }

    /**
     * For EyesWebDriver instances, returns the underlying WebDriver. For all other types - return the driver received
     * as parameter.
     * @param driver The driver instance for which to get the underlying WebDriver.
     * @return The underlying WebDriver
     */
    public static WebDriver getUnderlyingDriver(WebDriver driver) {
        if (driver instanceof EyesWebDriver) {
            driver = ((EyesWebDriver) driver).getRemoteWebDriver();
        }

        return driver;
    }

    /**
     * @param driver The driver for which to check if it represents a mobile
     *               device.
     * @return {@code true} if the platform running the test is a mobile
     * platform. {@code false} otherwise.
     */
    public static boolean isMobileDevice(WebDriver driver) {
        driver = getUnderlyingDriver(driver);
        try {
            if (reflectionInstanceof(driver, "AppiumDriver")) {
                Method isBrowser;
                try {
                    isBrowser = driver.getClass().getDeclaredMethod("isBrowser");
                    isBrowser.setAccessible(true);
                } catch (NoSuchMethodException ignored) {
                    isBrowser = driver.getClass().getMethod("isBrowser");
                }
                return isBrowser.invoke(driver).equals(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Is landscape orientation boolean.
     * @param logger the logger
     * @param driver The driver for which to check the orientation.
     * @return {@code true} if this is a mobile device and is in landscape orientation. {@code false} otherwise.
     */
    public static boolean isLandscapeOrientation(Logger logger, WebDriver driver) {
        // We can only find orientation for mobile devices.
        if (isMobileDevice(driver)) {
            Object appiumDriver = getUnderlyingDriver(driver);

            String originalContext;
            try {
                // We must be in native context in order to ask for orientation,
                // because of an Appium bug.
                originalContext = appiumDriver.getClass().getMethod("getContext").invoke(appiumDriver).toString();
                Set<String> contextHandles = (Set<String>) appiumDriver.getClass().getMethod("getContextHandles").invoke(appiumDriver);
                if (contextHandles.size() > 1 && !originalContext.equalsIgnoreCase(NATIVE_APP)) {
                    appiumDriver.getClass().getMethod("context", String.class).invoke(appiumDriver, NATIVE_APP);
                } else {
                    originalContext = null;
                }
            } catch (WebDriverException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                originalContext = null;
            }
            try {
                Object orientation = appiumDriver.getClass().getMethod("getOrientation").invoke(appiumDriver);
                return orientation == ScreenOrientation.LANDSCAPE;
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, e);
                return false;
            } finally {
                if (originalContext != null) {
                    try {
                        appiumDriver.getClass().getMethod("context", String.class).invoke(appiumDriver, originalContext);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return false;
    }

    /**
     * Select root element string.
     * @param executor the executor
     * @return the string
     */
    @SuppressWarnings("unused")
    public static String selectRootElement(JavascriptExecutor executor) {
        // FIXME: 16/06/2018 HOTFIX for returning using documentElement as default for "hideScrollbars"
        //  (selection logic does not work).

//        String script =
//                "var docElemScrollHeightBefore = document.documentElement.scrollHeight; " +
//                "var originalBodyOverflow = document.body.style.overflow; " +
//                "document.body.style.overflow = 'hidden'; " +
//                "var docElemScrollHeightAfter = document.documentElement.scrollHeight; " +
//                "if (docElemScrollHeightBefore != docElemScrollHeightAfter) " +
//                "var retVal = 'documentElement'; " +
//                "else " +
//                "var retVal = 'body'; " +
//                "document.body.style.overflow = originalBodyOverflow; " +
//                "return retVal;";
//
//        return (String)executor.executeScript(script);
        return "documentElement";
    }

    /**
     * Sets the overflow of the current context's body.
     * @param executor    The executor to use for setting the overflow.
     * @param value       The overflow value to set.
     * @param rootElement the root element
     * @return The previous overflow value (could be {@code null} if undefined).
     */
    public static String setOverflow(JavascriptExecutor executor,
                                     String value,
                                     WebElement rootElement) {
        ArgumentGuard.notNull(executor, "executor");
        ArgumentGuard.notNull(rootElement, "rootElement");

        String script = String.format("var origOF = arguments[0].style.overflow;" +
                "arguments[0].style.overflow = '%s';" +
                "if ('%s'.toUpperCase() === 'HIDDEN' && origOF.toUpperCase() !== 'HIDDEN') arguments[0].setAttribute('data-applitools-original-overflow',origOF);" +
                "return origOF;", value, value);

        try {
            String result = (String) executor.executeScript(script, rootElement);
            GeneralUtils.sleep(200);
            return result;
        } catch (WebDriverException e) {
            throw new EyesDriverOperationException("Failed to set overflow", e);
        }
    }

    /**
     * Gets current scroll position.
     * @param executor The executor to use.
     * @return The current scroll position of the current frame.
     */
    public static Location getCurrentScrollPosition(
            IEyesJsExecutor executor) {
        //noinspection unchecked
        return parseLocationString(executor.executeScript(JS_GET_CURRENT_SCROLL_POSITION));
    }

    public static Location parseLocationString(Object position) {
        String[] xy = position.toString().split(";");
        if (xy.length != 2)
        {
            throw new EyesException("Could not get scroll position!");
        }
        float x = Float.parseFloat(xy[0]);
        float y = Float.parseFloat(xy[1]);
        return new Location((int)Math.ceil(x), (int)Math.ceil(y));
    }

    /**
     * Sets the scroll position of the current frame.
     * @param executor The executor to use.
     * @param location The position to be set.
     */
    public static void setCurrentScrollPosition(IEyesJsExecutor executor,
                                                Location location) {
        executor.executeScript(String.format("window.scrollTo(%d,%d)",
                location.getX(), location.getY()));
    }

    /**
     * Gets current frame content entire size.
     * @param executor The executor to use.
     * @return The size of the entire content.
     */
    public static RectangleSize getCurrentFrameContentEntireSize(IEyesJsExecutor executor) {
        RectangleSize result;
        try {
            //noinspection unchecked
            Object retVal = executor.executeScript(JS_GET_CONTENT_ENTIRE_SIZE);
            String[] wh = ((String) retVal).split(";");

            if (wh.length != 2) {
                throw new EyesException("Could not get entire size!");
            }
            float w = Float.parseFloat(wh[0]);
            float h = Float.parseFloat(wh[1]);
            result = new RectangleSize(Math.round(w), Math.round(h));
        } catch (WebDriverException e) {
            throw new EyesDriverOperationException("Failed to extract entire size!");
        }
        return result;
    }

    /**
     * Gets entire element size.
     * @param logger   the logger
     * @param executor the executor
     * @param element  the element
     * @return the entire element size
     */
    public static RectangleSize getEntireElementSize(Logger logger, IEyesJsExecutor executor, WebElement element) {
        RectangleSize result;
        try {
            Object retVal = executor.executeScript(JS_GET_ENTIRE_PAGE_SIZE, element);
            String[] wh = ((String) retVal).split(";");

            if (wh.length != 2) {
                throw new EyesException("Could not get entire element size!");
            }
            float w = Float.parseFloat(wh[0]);
            float h = Float.parseFloat(wh[1]);
            result = new RectangleSize(Math.round(w), Math.round(h));
        } catch (WebDriverException e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, e);
            throw new EyesDriverOperationException("Failed to extract entire element size!");
        }
        return result;

    }

    /**
     * Gets viewport size.
     * @param executor The executor to use.
     * @return The viewport size.
     */
    public static RectangleSize getViewportSize(JavascriptExecutor executor) {
        String viewportSizeAsString = (String) executor.executeScript(JS_GET_VIEWPORT_SIZE);
        String[] wh = viewportSizeAsString.split(";");

        if (wh.length != 2) {
            throw new EyesException("Could not get viewport size!");
        }
        float w = Float.parseFloat(wh[0]);
        float h = Float.parseFloat(wh[1]);
        return new RectangleSize(Math.round(w), Math.round(h));
    }

    /**
     * Gets viewport size or display size.
     * @param logger The logger to use.
     * @param driver The web driver to use.
     * @return The viewport size of the current context, or the display size
     * if the viewport size cannot be retrieved.
     */
    public static RectangleSize getViewportSizeOrDisplaySize(Logger logger, WebDriver driver) {
        if (!isMobileDevice(driver)) {
            try {
                return getViewportSize((JavascriptExecutor) driver);
            } catch (Exception ex) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, ex);
            }
        }
        // If we failed to extract the viewport size using JS, will use the
        // window size instead.
        Dimension windowSize = driver.manage().window().getSize();
        int width = windowSize.getWidth();
        int height = windowSize.getHeight();
        try {
            if (isLandscapeOrientation(logger, driver) &&
                    height > width) {
                //noinspection SuspiciousNameCombination
                int height2 = width;
                //noinspection SuspiciousNameCombination
                width = height;
                height = height2;
            }
        } catch (WebDriverException e) {
            // Not every WebDriver supports querying for orientation.
        }
        return new RectangleSize(width, height);
    }

    /**
     * Sets browser size.
     * @param driver       the driver
     * @param requiredSize the required size
     * @return the browser size
     */
    public static boolean setBrowserSize(WebDriver driver, RectangleSize requiredSize) {
        final int SLEEP = 1000;
        int retriesLeft = 3;
        Dimension dRequiredSize = new Dimension(requiredSize.getWidth(), requiredSize.getHeight());
        Dimension dCurrentSize;
        RectangleSize currentSize;
        do {
            driver.manage().window().setSize(dRequiredSize);
            GeneralUtils.sleep(SLEEP);
            dCurrentSize = driver.manage().window().getSize();
            currentSize = new RectangleSize(dCurrentSize.getWidth(),
                    dCurrentSize.getHeight());
        } while (--retriesLeft > 0 && !currentSize.equals(requiredSize));

        return currentSize == requiredSize;
    }

    /**
     * Sets browser size by viewport size.
     * @param driver               the driver
     * @param actualViewportSize   the actual viewport size
     * @param requiredViewportSize the required viewport size
     * @return the browser size by viewport size
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean setBrowserSizeByViewportSize(WebDriver driver,
                                                       RectangleSize actualViewportSize,
                                                       RectangleSize requiredViewportSize) {
        Dimension browserSize = driver.manage().window().getSize();
        RectangleSize requiredBrowserSize = new RectangleSize(
                browserSize.width +
                        (requiredViewportSize.getWidth() - actualViewportSize.getWidth()),
                browserSize.height +
                        (requiredViewportSize.getHeight() - actualViewportSize.getHeight()));

        return setBrowserSize(driver, requiredBrowserSize);
    }

    /**
     * Sets viewport size.
     * @param logger The logger to use.
     * @param driver The web driver to use.
     * @param size   The size to set as the viewport size.
     */
    public static void setViewportSize(Logger logger, WebDriver driver, RectangleSize size) {
        ArgumentGuard.notNull(size, "size");
        if (size.isEmpty()) {
            return;
        }

        RectangleSize requiredSize = new RectangleSize(size.getWidth(), size.getHeight());
        RectangleSize actualViewportSize;
        try {
            actualViewportSize = getViewportSize((JavascriptExecutor) driver);
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, e);
            throw e;
        }

        // If the viewport size is already the required size
        if (actualViewportSize.equals(requiredSize)) {
            return;
        }

        // We move the window to (0,0) to have the best chance to be able to
        // set the viewport size as requested.
        try {
            driver.manage().window().setPosition(new Point(0, 0));
        } catch (WebDriverException e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, e);
        }

        setBrowserSizeByViewportSize(driver, actualViewportSize, requiredSize);

        actualViewportSize = getViewportSize((JavascriptExecutor) driver);

        if (actualViewportSize.equals(requiredSize)) {
            return;
        }

        // Additional attempt. This Solves the "maximized browser" bug
        // (border size for maximized browser sometimes different than
        // non-maximized, so the original browser size calculation is
        // wrong).
        setBrowserSizeByViewportSize(driver, actualViewportSize, requiredSize);

        actualViewportSize = getViewportSize((JavascriptExecutor) driver);
        if (actualViewportSize.equals(requiredSize)) {
            return;
        }

        final int MAX_DIFF = 3;
        int widthDiff = actualViewportSize.getWidth() - requiredSize.getWidth();
        int widthStep = widthDiff > 0 ? -1 : 1; // -1 for smaller size, 1 for larger
        int heightDiff = actualViewportSize.getHeight() - requiredSize.getHeight();
        int heightStep = heightDiff > 0 ? -1 : 1;

        Dimension dBrowserSize = driver.manage().window().getSize();
        RectangleSize browserSize = new RectangleSize(dBrowserSize.getWidth(),
                dBrowserSize.getHeight());

        int currWidthChange = 0;
        int currHeightChange = 0;
        // We try the zoom workaround only if size difference is reasonable.
        if (Math.abs(widthDiff) <= MAX_DIFF && Math.abs(heightDiff) <= MAX_DIFF) {
            int retriesLeft = Math.abs((widthDiff == 0 ? 1 : widthDiff) * (heightDiff == 0 ? 1 : heightDiff)) * 2;
            RectangleSize lastRequiredBrowserSize = null;
            do {
                // We specifically use "<=" (and not "<"), so to give an extra resize attempt
                // in addition to reaching the diff, due to floating point issues.
                if (Math.abs(currWidthChange) <= Math.abs(widthDiff) &&
                        actualViewportSize.getWidth() != requiredSize.getWidth()) {
                    currWidthChange += widthStep;
                }
                if (Math.abs(currHeightChange) <= Math.abs(heightDiff) &&
                        actualViewportSize.getHeight() != requiredSize.getHeight()) {
                    currHeightChange += heightStep;
                }

                RectangleSize requiredBrowserSize = new RectangleSize(browserSize.getWidth() + currWidthChange,
                        browserSize.getHeight() + currHeightChange);
                if (requiredBrowserSize.equals(lastRequiredBrowserSize)) {
                    break;
                }

                setBrowserSize(driver, requiredBrowserSize);
                lastRequiredBrowserSize = requiredBrowserSize;

                actualViewportSize = getViewportSize((JavascriptExecutor) driver);

                if (actualViewportSize.equals(requiredSize)) {
                    return;
                }
            } while ((Math.abs(currWidthChange) <= Math.abs(widthDiff) ||
                    Math.abs(currHeightChange) <= Math.abs(heightDiff))
                    && (--retriesLeft > 0));
        }

        throw new EyesException("Failed to set viewport size!");
    }

    /**
     * Is android boolean.
     * @param driver The driver to test.
     * @return {@code true} if the driver is an Android driver. {@code false} otherwise.
     */
    public static boolean isAndroid(WebDriver driver) {
        driver = getUnderlyingDriver(driver);
        return reflectionInstanceof(driver, "AndroidDriver");
    }

    /**
     * Is ios boolean.
     * @param driver The driver to test.
     * @return {@code true} if the driver is an iOS driver. {@code false} otherwise.
     */
    public static boolean isIOS(WebDriver driver) {
        driver = getUnderlyingDriver(driver);
        return reflectionInstanceof(driver, "IOSDriver");
    }

    /**
     * @param driver The driver to get the platform version from.
     * @return The platform version or {@code null} if it is undefined.
     */
    public static String getPlatformVersion(HasCapabilities driver) {
        Capabilities capabilities = driver.getCapabilities();
        Object platformVersionObj = capabilities.getCapability("os_version");
        if (platformVersionObj == null) {
            platformVersionObj = capabilities.getCapability(PLATFORM_VERSION);
        }

        return platformVersionObj == null ? null : String.valueOf(platformVersionObj);
    }

    /**
     * @param driver The driver to get the platform version from.
     * @return The device name or 'Unknown' if it is undefined.
     */
    public static String getMobileDeviceName(HasCapabilities driver) {
        Capabilities capabilities = driver.getCapabilities();
        Object desiredCaps = capabilities.getCapability("desired");
        if (desiredCaps != null) {
            Map<String, String> caps = (Map<String, String>) desiredCaps;
            Object deviceNameCapability = caps.get(DEVICE_NAME);
            return deviceNameCapability != null ? deviceNameCapability.toString() : "Unknown";
        }

        Object deviceNameCapability = capabilities.getCapability(DEVICE_NAME);
        String deviceName = deviceNameCapability != null ? deviceNameCapability.toString() : "Unknown";

        Object deviceCapability = capabilities.getCapability("device");

        if (deviceCapability != null && !deviceName.toLowerCase().contains(deviceCapability.toString())) {
            deviceName = deviceCapability.toString();
        }

        return deviceName;
    }

    /**
     * Gets current transform.
     * @param executor The executor to use.
     * @return The current documentElement transform values, according to {@link #JS_TRANSFORM_KEYS}.
     */
    public static Map<String, String> getCurrentTransform(IEyesJsExecutor executor) {

        StringBuilder script = new StringBuilder("return { ");

        for (String key : JS_TRANSFORM_KEYS) {
            script.append("'").append(key).append("'").append(": document.documentElement.style['").append(key).append("'],");
        }

        // Ending the list
        script.append(" }");

        //noinspection unchecked
        return (Map<String, String>) executor.executeScript(script.toString());

    }

    /**
     * Sets transforms for document.documentElement according to the given
     * map of style keys and values.
     * @param executor   The executor to use.
     * @param transforms The transforms to set. Keys are used as style keys,                   and values are the values for those styles.
     */
    public static void setTransforms(IEyesJsExecutor executor,
                                     Map<String, String> transforms) {

        StringBuilder script = new StringBuilder();

        for (Map.Entry<String, String> entry : transforms.entrySet()) {
            script.append("document.documentElement.style['").append(entry.getKey()).append("'] = '").append(entry.getValue()).append("';");
        }

        executor.executeScript(script.toString());
    }

    /**
     * Set the given transform to document.documentElement for all style keys
     * defined in {@link #JS_TRANSFORM_KEYS} .
     * @param executor  The executor to use.
     * @param transform The transform value to set.
     */
    public static void setTransform(IEyesJsExecutor executor,
                                    String transform) {
        Map<String, String> transforms = new HashMap<>(JS_TRANSFORM_KEYS.length);

        for (String key : JS_TRANSFORM_KEYS) {
            transforms.put(key, transform);
        }

        setTransforms(executor, transforms);
    }

    /**
     * Returns given element visible portion size.\
     * @param element The element for which to return the size.
     * @return The given element's visible portion size.
     */
    public static RectangleSize getElementVisibleSize(Logger logger, WebElement element) {
        Point location = element.getLocation();
        Dimension size = element.getSize();
        Region region = new Region(location.getX(), location.getY(), size.getWidth(), size.getHeight());
        WebElement parent;

        try {
            parent = element.findElement(By.xpath(".."));
        } catch (Exception e) {
            parent = null;
        }

        try {
            while (parent != null && !region.isSizeEmpty()) {
                Point parentLocation = parent.getLocation();
                Dimension parentSize = parent.getSize();
                Region parentRegion = new Region(parentLocation.getX(), parentLocation.getY(),
                        parentSize.getWidth(), parentSize.getHeight());

                region.intersect(parentRegion);
                try {
                    parent = parent.findElement(By.xpath(".."));
                } catch (Exception e) {
                    parent = null;
                }
            }
        } catch (Exception ex) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, ex);
        }

        return region.getSize();
    }

    /**
     * Translates the current documentElement to the given position.
     * @param executor The executor to use.
     * @param position The position to translate to.
     */
    public static void translateTo(IEyesJsExecutor executor,
                                   Location position) {
        setTransform(executor, String.format("translate(-%spx, -%spx)",
                position.getX(), position.getY()));
    }

    /**
     * If the web element was created by {@link org.openqa.selenium.support.FindBy}, then it's a {@link java.lang.reflect.Proxy} object.
     * This method gets the real web element from the proxy object.
     */
    public static WebElement getWrappedWebElement(WebElement webElement) {
        if (!(webElement instanceof java.lang.reflect.Proxy)) {
            return webElement;
        }

        java.lang.reflect.Proxy proxy = (java.lang.reflect.Proxy) webElement;
        Field[] fields =  Proxy.class.getDeclaredFields();
        for (Field field : fields) {
            if(field.getType().equals(InvocationHandler.class)) {
                field.setAccessible(true);
                try {
                    InvocationHandler handler = (InvocationHandler) field.get(proxy);
                    return  (WebElement) handler.invoke(null, WrapsElement.class.getMethod("getWrappedElement"), null);
                } catch (Throwable throwable) {
                    throw new EyesException("Failed getting web element from page object", throwable);
                }
            }
        }

        throw new IllegalStateException("InvocationHandler field wasn't found in proxy class");
    }

    private static boolean reflectionInstanceof(Object object, String className) {
        Class<?> objectClass = object.getClass();
        while (objectClass != null) {
            if (objectClass.getSimpleName().equals(className)) {
                return true;
            }
            objectClass = objectClass.getSuperclass();
        }
        return false;
    }

    public static Rectangle getVisibleElementRect(WebElement webElement, EyesWebDriver driver) {
        if (isMobileDevice(driver)) {
            return new Rectangle(webElement.getLocation(), webElement.getSize());
        }

        String result = (String) driver.executeScript(JS_GET_VISIBLE_ELEMENT_RECT, webElement);
        String[] data = result.split(";");
        return new Rectangle(
                Math.round(Float.parseFloat(data[0])),
                Math.round(Float.parseFloat(data[1])),
                Math.round(Float.parseFloat(data[3])),
                Math.round(Float.parseFloat(data[2])));
    }
}
