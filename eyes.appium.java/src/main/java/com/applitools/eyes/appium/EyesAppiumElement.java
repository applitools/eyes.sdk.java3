package com.applitools.eyes.appium;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.utils.ArgumentGuard;
import com.google.common.collect.ImmutableMap;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.FileDetector;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.remote.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EyesAppiumElement extends RemoteWebElement {

    private final EyesAppiumDriver driver;
    private final RemoteWebElement webElement;
    private final double pixelRatio;

    public EyesAppiumElement(EyesAppiumDriver driver, WebElement webElement, double pixelRatio) {
        ArgumentGuard.notNull(driver, "driver");
        ArgumentGuard.notNull(webElement, "webElement");

        webElement = EyesDriverUtils.getWrappedWebElement(webElement);
        if (webElement instanceof RemoteWebElement) {
            this.webElement = (RemoteWebElement) webElement;
        } else {
            throw new EyesException("The input web element is not a RemoteWebElement.");
        }
        this.pixelRatio = pixelRatio;
        this.driver = driver;
        setParent(driver.getRemoteWebDriver());
        setId(this.webElement.getId());
    }

    @Override
    public Dimension getSize() {
        Dimension size = webElement.getSize();
        if (pixelRatio == 1.0) {
            return size;
        }
        int unscaledWidth;
        int unscaledHeight;
        if (driver.getRemoteWebDriver() instanceof IOSDriver) {
            unscaledWidth = size.getWidth();
            unscaledHeight = size.getHeight();
        } else {
            unscaledWidth = (int) Math.ceil(size.getWidth() * pixelRatio);
            unscaledHeight = (int) Math.ceil(size.getHeight() * pixelRatio);
        }
        return new Dimension(unscaledWidth, unscaledHeight);
    }

    @Override
    public WebDriver getWrappedDriver() {
        return driver;
    }

    /**
     * For RemoteWebElement object, the function returns an
     * EyesRemoteWebElement object. For all other types of WebElement,
     * the function returns the original object.
     */
    private WebElement wrapElement(WebElement elementToWrap) {
        WebElement resultElement = elementToWrap;
        if (elementToWrap instanceof RemoteWebElement) {
            resultElement = new EyesAppiumElement(driver, elementToWrap, pixelRatio);
        }
        return resultElement;
    }

    /**
     * For RemoteWebElement object, the function returns an
     * EyesRemoteWebElement object. For all other types of WebElement,
     * the function returns the original object.
     */
    private List<WebElement> wrapElements(List<WebElement> elementsToWrap) {
        // This list will contain the found elements wrapped with our class.
        List<WebElement> wrappedElementsList = new ArrayList<>(elementsToWrap.size());
        for (WebElement currentElement : elementsToWrap) {
            if (currentElement instanceof RemoteWebElement) {
                wrappedElementsList.add(new EyesAppiumElement(driver, currentElement, pixelRatio));
            } else {
                wrappedElementsList.add(currentElement);
            }
        }

        return wrappedElementsList;
    }

    @Override
    public List<WebElement> findElements(By by) {
        return wrapElements(webElement.findElements(by));
    }

    @Override
    public WebElement findElement(By by) {
        return wrapElement(webElement.findElement(by));
    }

    @Override
    public WebElement findElementById(String using) {
        return wrapElement(webElement.findElementById(using));
    }

    @Override
    public List<WebElement> findElementsById(String using) {
        return wrapElements(webElement.findElementsById(using));
    }

    @Override
    public WebElement findElementByLinkText(String using) {
        return wrapElement(webElement.findElementByLinkText(using));
    }

    @Override
    public List<WebElement> findElementsByLinkText(String using) {
        return wrapElements(webElement.findElementsByLinkText(using));
    }

    @Override
    public WebElement findElementByName(String using) {
        return wrapElement(webElement.findElementByName(using));
    }

    @Override
    public List<WebElement> findElementsByName(String using) {
        return wrapElements(webElement.findElementsByName(using));
    }

    @Override
    public WebElement findElementByClassName(String using) {
        return wrapElement(webElement.findElementByClassName(using));
    }

    @Override
    public List<WebElement> findElementsByClassName(String using) {
        return wrapElements(webElement.findElementsByClassName(using));
    }

    @Override
    public WebElement findElementByCssSelector(String using) {
        return wrapElement(webElement.findElementByCssSelector(using));
    }

    @Override
    public List<WebElement> findElementsByCssSelector(String using) {
        return wrapElements(webElement.findElementsByCssSelector(using));
    }

    @Override
    public WebElement findElementByXPath(String using) {
        return wrapElement(webElement.findElementByXPath(using));
    }

    @Override
    public List<WebElement> findElementsByXPath(String using) {
        return wrapElements(webElement.findElementsByXPath(using));
    }

    @Override
    public WebElement findElementByPartialLinkText(String using) {
        return wrapElement(webElement.findElementByPartialLinkText(using));
    }

    @Override
    public List<WebElement> findElementsByPartialLinkText(String using) {
        return wrapElements(webElement.findElementsByPartialLinkText(using));
    }

    @Override
    public WebElement findElementByTagName(String using) {
        return wrapElement(webElement.findElementByTagName(using));
    }

    @Override
    public List<WebElement> findElementsByTagName(String using) {
        return wrapElements(webElement.findElementsByTagName(using));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof RemoteWebElement) && webElement.equals(obj);
    }

    @Override
    public int hashCode() {
        return webElement.hashCode();
    }

    @Override
    public void setFileDetector(FileDetector detector) {
        webElement.setFileDetector(detector);
    }

    @Override
    public void submit() {
        webElement.submit();
    }

    @Override
    public void clear() {
        webElement.clear();
    }

    @Override
    public String getTagName() {
        return webElement.getTagName();
    }

    @Override
    public String getAttribute(String name) {
        return webElement.getAttribute(name);
    }

    @Override
    public boolean isSelected() {
        return webElement.isSelected();
    }

    @Override
    public boolean isEnabled() {
        return webElement.isEnabled();
    }

    @Override
    public String getText() {
        return webElement.getText();
    }

    @Override
    public Point getLocation() {
        String elementId = getId();
        Response response = execute(DriverCommand.GET_ELEMENT_LOCATION, ImmutableMap.of("id", elementId));
        Map<String, Object> rawPoint = (Map<String, Object>) response.getValue();
        int x = (int) Math.round(((Number) rawPoint.get("x")).doubleValue());
        int y = (int) Math.round(((Number) rawPoint.get("y")).doubleValue());
        Point location =  new Point(x, y);
        location = new Point(location.getX(), location.getY() - driver.getStatusBarHeight());
        if (pixelRatio == 1.0) {
            return location;
        }
        int unscaledX;
        int unscaledY;
        if (driver.getRemoteWebDriver() instanceof IOSDriver) {
            unscaledX = location.getX();
            unscaledY = location.getY();
        } else {
            unscaledX = (int) Math.ceil(location.getX()*pixelRatio);
            unscaledY = (int) Math.ceil(location.getY()*pixelRatio);
        }
        return new Point(unscaledX, unscaledY);
    }

    @Override
    public Coordinates getCoordinates() {
        return webElement.getCoordinates();
    }

    @Override
    public String toString() {
        return "EyesAppiumElement: " + webElement.getId();
    }
}
