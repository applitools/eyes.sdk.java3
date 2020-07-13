package com.applitools.eyes.appium;

import com.applitools.eyes.EyesException;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

public class EyesAppiumElement extends RemoteWebElement {

    private final EyesAppiumDriver driver;
    private final RemoteWebElement webElement;
    private Dimension size;
    private final double pixelRatio;

    public EyesAppiumElement(EyesAppiumDriver driver, WebElement webElement, double pixelRatio) {
        if (webElement instanceof RemoteWebElement) {
            this.webElement = (RemoteWebElement) webElement;
        } else {
            throw new EyesException("The input web element is not a RemoteWebElement.");
        }
        this.pixelRatio = pixelRatio;
        this.driver = driver;
    }

    protected Dimension getCachedSize() {
        if (size == null) {
            size = webElement.getSize();
        }
        return size;
    }

    @Override
    public Dimension getSize() {
        Dimension size = super.getSize();
        if (pixelRatio == 1.0) {
            return size;
        }
        int unscaledWidth;
        int unscaledHeight;
        if (driver.getRemoteWebDriver() instanceof IOSDriver) {
            unscaledWidth = webElement.getSize().getWidth();
            unscaledHeight = webElement.getSize().getHeight();
        } else {
            unscaledWidth = (int) Math.ceil(webElement.getSize().getWidth()*pixelRatio);
            unscaledHeight = (int) Math.ceil(webElement.getSize().getHeight()*pixelRatio);
        }
        return new Dimension(unscaledWidth, unscaledHeight);
    }

    public int getClientWidth () {
        return getCachedSize().width;
    }

    public int getClientHeight () {
        return getCachedSize().height;
    }

    public int getComputedStyleInteger (String propStyle) {
        return 0;
    }

    @Override
    public Point getLocation() {
        Point location = super.getLocation();
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
}
