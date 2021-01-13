package com.applitools.eyes.appium;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.eyes.selenium.positioning.ImageRotation;
import com.applitools.utils.GeneralUtils;
import com.applitools.utils.ImageUtils;
import io.appium.java_client.AppiumDriver;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebElement;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EyesAppiumDriver extends EyesWebDriver {

    private final AppiumDriver driver;
    private Map<String, Object> sessionDetails;
    private final Map<String, WebElement> elementsIds = new HashMap<>();
    private ImageRotation rotation;
    private RectangleSize defaultContentViewportSize = null;
    private Map<String, Integer> systemBarsHeights = null;
    private Integer deviceHeight = null;

    public EyesAppiumDriver(Logger logger, Eyes eyes, AppiumDriver driver) {
        super(logger, eyes);
        this.driver = driver;
    }

    @Override
    public AppiumDriver getRemoteWebDriver () { return this.driver; }

    /**
     *
     * @return The image rotation model.
     */
    public ImageRotation getRotation() {
        return rotation;
    }

    /**
     * @param rotation The image rotation model.
     */
    public void setRotation(ImageRotation rotation) {
        this.rotation = rotation;
    }

    private Map<String, Object> getCachedSessionDetails () {
        if(sessionDetails == null) {
            sessionDetails = getRemoteWebDriver().getSessionDetails();
        }
        return sessionDetails;
    }

    public HashMap<String, Integer> getViewportRect() {
        Map<String, Long> rectMap = (Map<String, Long>) getCachedSessionDetails().get("viewportRect");
        int width = rectMap.get("width").intValue();
        int height = ensureViewportHeight(rectMap.get("height").intValue());

        HashMap<String, Integer> intRectMap = new HashMap<>();
        intRectMap.put("width", width);
        intRectMap.put("height", height);

        return intRectMap;
    }

    public int getViewportHeight() {
        Map<String, Long> rectMap = (Map<String, Long>) getCachedSessionDetails().get("viewportRect");
        return rectMap.get("height").intValue();
    }

    private int ensureViewportHeight(int viewportHeight) {
        if (EyesDriverUtils.isAndroid(driver)) {
            try {
                int height = getDeviceHeight();
                Map<String, Integer> systemBarsHeights = getSystemBarsHeights();
                for (Integer barHeight : systemBarsHeights.values()) {
                    if (barHeight != null && barHeight < height) {
                        height -= barHeight;
                    }
                }
                return height;
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, e);
            }
        }

        return viewportHeight;
    }

    public int getDeviceHeight() {
        if (deviceHeight == null) {
            String deviceScreenSize = (String) getCachedSessionDetails().get("deviceScreenSize");
            Pattern p = Pattern.compile("x(\\d+)");
            Matcher m = p.matcher(deviceScreenSize);
            m.find();
            deviceHeight = Integer.parseInt(m.group(1));
        }

        return deviceHeight;
    }

    public Map<String, Integer> getSystemBarsHeights() {
        if (systemBarsHeights == null) {
            systemBarsHeights = EyesAppiumUtils.getSystemBarsHeights(this);
        }

        return systemBarsHeights;
    }

    public int getStatusBarHeight() {
        Object statusBarHeight = getCachedSessionDetails().get("statBarHeight");
        if (statusBarHeight instanceof Double) {
            return ((Double) statusBarHeight).intValue();
        } else {
            return ((Long) statusBarHeight).intValue();
        }
    }

    @Override
    protected double getDevicePixelRatioInner() {
        Object pixelRatio = getCachedSessionDetails().get("pixelRatio");
        if (pixelRatio instanceof Double) {
            return (Double) pixelRatio;
        } else {
            return ((Long) pixelRatio).doubleValue();
        }
    }

    @Override
    public List<WebElement> findElements(By by) {
        List<WebElement> foundWebElementsList = driver.findElements(by);

        // This list will contain the found elements wrapped with our class.
        List<WebElement> resultElementsList = new ArrayList<>(foundWebElementsList.size());
        for (WebElement currentElement : foundWebElementsList) {
            if (!(currentElement instanceof RemoteWebElement)) {
                throw new EyesException(String.format("findElements: element is not a RemoteWebElement: %s", by));
            }
            resultElementsList.add(new EyesAppiumElement(this, currentElement, 1/getDevicePixelRatio()));

            // For Remote web elements, we can keep the IDs
            elementsIds.put(((RemoteWebElement) currentElement).getId(), currentElement);
        }

        return resultElementsList;
    }

    @Override
    public EyesAppiumElement findElement(By by) {
        WebElement webElement = driver.findElement(by);
        if (!(webElement instanceof RemoteWebElement)) {
            throw new EyesException("findElement: Element is not a RemoteWebElement: " + by);
        }

        EyesAppiumElement appiumElement = new EyesAppiumElement(this, webElement, 1/ getDevicePixelRatio());

        // For Remote web elements, we can keep the IDs,
        // for Id based lookup (mainly used for Javascript related
        // activities).
        elementsIds.put(((RemoteWebElement) webElement).getId(), webElement);
        return appiumElement;
    }

    public WebElement findElementByClassName(String className) {
        return findElement(By.className(className));
    }

    public List<WebElement> findElementsByClassName(String className) {
        return findElements(By.className(className));
    }

    public WebElement findElementByCssSelector(String cssSelector) {
        return findElement(By.cssSelector(cssSelector));
    }

    public List<WebElement> findElementsByCssSelector(String cssSelector) {
        return findElements(By.cssSelector(cssSelector));
    }

    public WebElement findElementById(String id) {
        return findElement(By.id(id));
    }

    public List<WebElement> findElementsById(String id) {
        return findElements(By.id(id));
    }

    public WebElement findElementByLinkText(String linkText) {
        return findElement(By.linkText(linkText));
    }

    public List<WebElement> findElementsByLinkText(String linkText) {
        return findElements(By.linkText(linkText));
    }

    public WebElement findElementByPartialLinkText(String partialLinkText) {
        return findElement(By.partialLinkText(partialLinkText));
    }

    public List<WebElement> findElementsByPartialLinkText(String partialLinkText) {
        return findElements(By.partialLinkText(partialLinkText));
    }

    public WebElement findElementByName(String name) {
        return findElement(By.name(name));
    }

    public List<WebElement> findElementsByName(String name) {
        return findElements(By.name(name));
    }

    public WebElement findElementByTagName(String tagName) {
        return findElement(By.tagName(tagName));
    }

    public List<WebElement> findElementsByTagName(String tagName) {
        return findElements(By.tagName(tagName));
    }

    public WebElement findElementByXPath(String path) {
        return findElement(By.xpath(path));
    }

    public List<WebElement> findElementsByXPath(String path) {
        return findElements(By.xpath(path));
    }

    public Capabilities getCapabilities() {
        return driver.getCapabilities();
    }

    /**
     * @param forceQuery If true, we will perform the query even if we have a cached viewport size.
     * @return The viewport size of the default content (outer most frame).
     */
    public RectangleSize getDefaultContentViewportSize(boolean forceQuery) {
        if (defaultContentViewportSize != null && !forceQuery) {
            return defaultContentViewportSize;
        }

        HashMap<String, Integer> rect = getViewportRect();
        double dpr = getDevicePixelRatio();
        defaultContentViewportSize = (new RectangleSize(rect.get("width"), rect.get("height"))).scale(1/dpr);
        logger.log(TraceLevel.Info, null, Stage.GENERAL, Pair.of("defaultContentViewportSize", defaultContentViewportSize));
        return defaultContentViewportSize;
    }

    /**
     * See {@link #getDefaultContentViewportSize(boolean)}.
     * {@code forceQuery} defaults to {@code false}.
     */
    public RectangleSize getDefaultContentViewportSize() {
        return getDefaultContentViewportSize(true);
    }

    public <X> X getScreenshotAs(OutputType<X> xOutputType)
            throws WebDriverException {
        // Get the image as base64.
        String screenshot64 = driver.getScreenshotAs(OutputType.BASE64);
        BufferedImage screenshot = ImageUtils.imageFromBase64(screenshot64);
        screenshot = EyesAppiumUtils.normalizeRotation(logger, driver, screenshot, rotation);

        // Return the image in the requested format.
        screenshot64 = ImageUtils.base64FromImage(screenshot);
        return xOutputType.convertFromBase64Png(screenshot64);
    }

    @Override
    public String getPageSource() {
        return driver.getPageSource();
    }

    @Override
    public void close() {
        driver.close();
    }

    @Override
    public void quit() {
        driver.quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return driver.switchTo();
    }

    @Override
    public Navigation navigate() {
        return driver.navigate();
    }

    @Override
    public Options manage() {
        return driver.manage();
    }

    @Override
    public void get(String url) {
        driver.get(url);
    }

    @Override
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return driver.getTitle();
    }

    public Map<String, WebElement> getElementIds() {
        return elementsIds;
    }

    @Override
    public Object executeScript(String script, Object... args) {
        return driver.executeScript(script, args);
    }

    @Override
    public Object executeAsyncScript(String script, Object... args) {
        return driver.executeAsyncScript(script, args);
    }
}
