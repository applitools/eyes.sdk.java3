package com.applitools.eyes.selenium.wrappers;

import com.applitools.eyes.*;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.selenium.*;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.positioning.ImageRotation;
import com.applitools.eyes.selenium.triggers.EyesKeyboard;
import com.applitools.eyes.selenium.triggers.EyesMouse;
import com.applitools.eyes.triggers.MouseTrigger;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.internal.*;
import org.openqa.selenium.remote.*;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * An SeleniumEyes implementation of the interfaces implemented by
 * {@link org.openqa.selenium.remote.RemoteWebDriver}.
 * Used so we'll be able to return the users an object with the same
 * functionality as {@link org.openqa.selenium.remote.RemoteWebDriver}.
 */
public class EyesSeleniumDriver extends EyesWebDriver implements HasInputDevices, FindsByClassName,
        FindsByCssSelector, FindsById, FindsByLinkText, FindsByName, FindsByTagName,
        FindsByXPath, HasTouchScreen, IEyesJsExecutor {

    private final SeleniumEyes eyes;
    private final RemoteWebDriver driver;
    private final TouchScreen touch;
    private final Map<String, WebElement> elementsIds;
    private final FrameChain frameChain;

    private ImageRotation rotation;
    private RectangleSize defaultContentViewportSize;

    /**
     * Rotates the image as necessary. The rotation is either manually forced
     * by passing a non-null ImageRotation, or automatically inferred.
     * @param driver   The underlying driver which produced the screenshot.
     * @param image    The image to normalize.
     * @param rotation The degrees by which to rotate the image:
     *                 positive values = clockwise rotation,
     *                 negative values = counter-clockwise,
     *                 0 = force no rotation,
     *                 null = rotate automatically as needed.
     * @param logger logger
     * @return A normalized image.
     */
    public static BufferedImage normalizeRotation(Logger logger,
                                                  WebDriver driver,
                                                  BufferedImage image,
                                                  ImageRotation rotation) {
        ArgumentGuard.notNull(driver, "driver");
        ArgumentGuard.notNull(image, "image");
        BufferedImage normalizedImage = image;
        if (rotation != null) {
            if (rotation.getRotation() != 0) {
                normalizedImage = ImageUtils.rotateImage(image,
                        rotation.getRotation());
            }
        } else { // Do automatic rotation if necessary
            try {
                if (EyesDriverUtils.isMobileDevice(driver) &&
                        EyesDriverUtils.isLandscapeOrientation(logger, driver)
                        && image.getHeight() > image.getWidth()) {
                    // For Android, we need to rotate images to the right, and
                    // for iOS to the left.
                    int degrees =
                            EyesDriverUtils.isAndroid(driver) ? 90 : -90;
                    normalizedImage = ImageUtils.rotateImage(image, degrees);
                }
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, e);
            }
        }

        return normalizedImage;
    }

    public EyesSeleniumDriver(Logger logger, SeleniumEyes eyes, RemoteWebDriver driver) throws EyesException {
        super(logger, eyes);
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(driver, "driver");

        this.eyes = eyes;
        this.driver = driver;

        this.elementsIds = new HashMap<>();
        this.frameChain = new FrameChain(logger);
        this.defaultContentViewportSize = null;

        // initializing "touch" if possible
        ExecuteMethod executeMethod = null;
        try {
            executeMethod = new RemoteExecuteMethod(driver);
        } catch (Exception e) {
            // If an exception occurred, we simply won't instantiate "touch".
        }
        if (null != executeMethod) {
            touch = new EyesTouchScreen(this,
                    new RemoteTouchScreen(executeMethod));
        } else {
            touch = null;
        }
    }

    public SeleniumEyes getEyes() {
        return eyes;
    }

    @Override
    public RemoteWebDriver getRemoteWebDriver() {
        return driver;
    }

    public TouchScreen getTouch() {
        return touch;
    }

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

    public void get(String s) {
        frameChain.clear();
        driver.get(s);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public List<WebElement> findElements(By by) {
        List<WebElement> foundWebElementsList = driver.findElements(by);

        // This list will contain the found elements wrapped with our class.
        List<WebElement> resultElementsList =
                new ArrayList<WebElement>(foundWebElementsList.size());

        for (WebElement currentElement : foundWebElementsList) {
            if (currentElement instanceof RemoteWebElement) {
                resultElementsList.add(new EyesRemoteWebElement(logger, this,
                        (RemoteWebElement) currentElement));

                // For Remote web elements, we can keep the IDs
                elementsIds.put(((RemoteWebElement) currentElement).getId(),
                        currentElement);

            } else {
                throw new EyesException(String.format(
                        "findElements: element is not a RemoteWebElement: %s",
                        by));
            }
        }

        return resultElementsList;
    }

    @Override
    protected double getDevicePixelRatioInner() {
        if (EyesDriverUtils.isMobileDevice(this)) {
            return SeleniumEyes.DEFAULT_DEVICE_PIXEL_RATIO;
        }
        return Float.parseFloat(executeScript("return window.devicePixelRatio").toString());
    }

    public WebElement findElement(By by) {
        WebElement webElement = driver.findElement(by);
        if ((webElement instanceof RemoteWebElement) && !(webElement instanceof EyesRemoteWebElement)) {
            webElement = new EyesRemoteWebElement(logger, this, webElement);

            // For Remote web elements, we can keep the IDs,
            // for Id based lookup (mainly used for Javascript related
            // activities).
            elementsIds.put(((RemoteWebElement) webElement).getId(), webElement);
        } else {
            throw new EyesException("findElement: Element is not a RemoteWebElement: " + by);
        }

        return webElement;
    }

    /**
     * Found elements are sometimes accessed by their IDs (e.g. tapping an
     * element in Appium).
     * @return Maps of IDs for found elements.
     */
    @SuppressWarnings("UnusedDeclaration")
    public Map<String, WebElement> getElementIds() {
        return elementsIds;
    }

    public String getPageSource() {
        return driver.getPageSource();
    }

    public void close() {
        driver.close();
    }

    public void quit() {
        driver.quit();
    }

    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    public TargetLocator switchTo() {
        return new EyesTargetLocator(this, logger, driver.switchTo());
    }

    public Navigation navigate() {
        return driver.navigate();
    }

    public Options manage() {
        return driver.manage();
    }

    public Mouse getMouse() {
        return new EyesMouse(this, driver.getMouse());
    }

    public Keyboard getKeyboard() {
        return new EyesKeyboard(logger, this, driver.getKeyboard());
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

    public Object executeScript(String script, Object... args) {

        // Appium commands are sometimes sent as Javascript
        if (AppiumJsCommandExtractor.isAppiumJsCommand(script)) {
            Trigger trigger =
                    AppiumJsCommandExtractor.extractTrigger(elementsIds,
                            driver.manage().window().getSize(), script, args);

            if (trigger != null) {
                // TODO - Daniel, additional types of triggers
                if (trigger instanceof MouseTrigger) {
                    MouseTrigger mt = (MouseTrigger) trigger;
                    eyes.addMouseTrigger(mt.getMouseAction(), mt.getControl(), mt.getLocation());
                }
            }
        }

        @SuppressWarnings("UnnecessaryLocalVariable")
        Object result = driver.executeScript(script, args);
        return result;
    }

    public Object executeAsyncScript(String script, Object... args) {

        // Appium commands are sometimes sent as Javascript
        if (AppiumJsCommandExtractor.isAppiumJsCommand(script)) {
            Trigger trigger =
                    AppiumJsCommandExtractor.extractTrigger(elementsIds,
                            driver.manage().window().getSize(), script, args);

            if (trigger != null) {
                // TODO - Daniel, additional type of triggers
                if (trigger instanceof MouseTrigger) {
                    MouseTrigger mt = (MouseTrigger) trigger;
                    eyes.addMouseTrigger(mt.getMouseAction(),
                            mt.getControl(), mt.getLocation());
                }
            }
        }

        return driver.executeAsyncScript(script, args);
    }

    /**
     * @param forceQuery If true, we will perform the query even if we have a cached viewport size.
     * @return The viewport size of the default content (outer most frame).
     */
    public RectangleSize getDefaultContentViewportSize(boolean forceQuery) {
        if (defaultContentViewportSize != null && !forceQuery) {
            return defaultContentViewportSize;
        }

        EyesTargetLocator switchTo = null;
        //In Appium driver there's no switchTo()
        if (!EyesDriverUtils.isMobileDevice(this.driver)) {
            switchTo = (EyesTargetLocator)switchTo();
        }

        FrameChain currentFrames = getFrameChain().clone();

        // Optimization
        if (currentFrames.size() > 0) {
            switchTo.defaultContent();
        }

        defaultContentViewportSize = EyesDriverUtils.getViewportSizeOrDisplaySize(logger, this);
        if (currentFrames.size() > 0) {
            switchTo.frames(currentFrames);
        }
        return defaultContentViewportSize;
    }

    /**
     * See {@link #getDefaultContentViewportSize(boolean)}.
     * {@code forceQuery} defaults to {@code false}.
     * @return the DefaultContentViewportSize
     */
    public RectangleSize getDefaultContentViewportSize() {
        return getDefaultContentViewportSize(true);
    }

    /**
     * @return The current frame chain.
     */
    public FrameChain getFrameChain() {
        return frameChain;
    }

    public <X> X getScreenshotAs(OutputType<X> xOutputType)
            throws WebDriverException {
        // Get the image as base64.
        String screenshot64 = driver.getScreenshotAs(OutputType.BASE64);
        BufferedImage screenshot = ImageUtils.imageFromBase64(screenshot64);
        screenshot = normalizeRotation(logger, driver, screenshot, rotation);

        // Return the image in the requested format.
        screenshot64 = ImageUtils.base64FromImage(screenshot);
        return xOutputType.convertFromBase64Png(screenshot64);
    }

    public String getUserAgent() {
        String userAgent = null;
        try {
            if (!EyesDriverUtils.isMobileDevice(driver)) {
                userAgent = (String) this.driver.executeScript("return navigator.userAgent");
            }
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, e);
            userAgent = null;
        }

        return userAgent;
    }
}
