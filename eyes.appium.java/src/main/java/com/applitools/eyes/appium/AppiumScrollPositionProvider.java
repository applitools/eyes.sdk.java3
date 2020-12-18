package com.applitools.eyes.appium;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.Region;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.selenium.positioning.ScrollPositionMemento;
import com.applitools.eyes.selenium.positioning.ScrollPositionProvider;
import com.applitools.utils.ArgumentGuard;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.*;

import java.io.IOException;

public abstract class AppiumScrollPositionProvider implements ScrollPositionProvider {

    protected final Logger logger;
    protected final AppiumDriver driver;
    protected final EyesAppiumDriver eyesDriver;
    protected double distanceRatio;
    protected int verticalScrollGap;
    protected WebElement cutElement = null;

    protected ContentSize contentSize;

    private WebElement firstVisibleChild;
    private boolean isVerticalScrollGapSet;

    public AppiumScrollPositionProvider(Logger logger, EyesAppiumDriver driver) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(driver, "driver");

        this.logger = logger;
        this.driver = driver.getRemoteWebDriver();
        this.eyesDriver = driver;
        distanceRatio = 0.0;
        verticalScrollGap = 0;
        isVerticalScrollGapSet = false;
    }

    public void setCutElement(WebElement cutElement) {
        this.cutElement = cutElement;
    }

    protected WebElement getCachedFirstVisibleChild () {
        WebElement activeScroll = getFirstScrollableView();
        if (firstVisibleChild == null) {
            logger.verbose("Could not find first visible child in cache, getting (this could take a while)");
            firstVisibleChild = EyesAppiumUtils.getFirstVisibleChild(activeScroll);
        } else {
            Rectangle firstVisibleChildRect = firstVisibleChild.getRect();
            if (firstVisibleChildRect.getWidth() == 0 && firstVisibleChildRect.getHeight() == 0) {
                logger.verbose("Cached visible child is invalid for some reason(e.g. user opened another screen and current firstVisibleChild is useless" +
                        " and it is not already in the view hierarchy). It should be updated. Getting (this could take a while)");
                firstVisibleChild = EyesAppiumUtils.getFirstVisibleChild(activeScroll);
            }
        }
        return firstVisibleChild;
    }

    protected ContentSize getCachedContentSize () {
        if (contentSize != null) {
            return contentSize;
        }

        try {
            WebElement activeScroll = getFirstScrollableView();
            contentSize = EyesAppiumUtils.getContentSize(driver, activeScroll);
            logger.verbose("Retrieved contentSize, it is: " + contentSize);
        } catch (NoSuchElementException e) {
            logger.log("WARNING: No active scroll element, so no content size to retrieve");
        }

        return contentSize;
    }

    public Location getScrollableViewLocation() {
        logger.verbose("Getting the location of the scrollable view");
        WebElement activeScroll, firstVisChild;
        Point scrollLoc, firstVisChildLoc;
        try {
            activeScroll = getFirstScrollableView();
            firstVisChild = getCachedFirstVisibleChild();
        } catch (NoSuchElementException e) {
            return new Location(0, 0);
        }
        scrollLoc = activeScroll.getLocation();
        firstVisChildLoc = firstVisChild.getLocation();
        logger.verbose("The location of the first visible child is " + firstVisChildLoc);
        if (!isVerticalScrollGapSet) {
            verticalScrollGap = firstVisChildLoc.y - scrollLoc.y;
            isVerticalScrollGapSet = true;
        }
        Location loc = new Location(scrollLoc.x, scrollLoc.y + verticalScrollGap);
        logger.verbose("The location of the scrollable view is " + loc + ", accounting for a " +
            "vertical scroll gap of " + verticalScrollGap);
        return loc;
    }

    public Region getScrollableViewRegion() {
        logger.verbose("Getting the region of the scrollable view");
        WebElement activeScroll;
        Region reg;
        try {
            activeScroll = getFirstScrollableView();
            Location scrollLoc = getScrollableViewLocation();
            Dimension scrollDim = activeScroll.getSize();
            reg = new Region(scrollLoc.getX(), scrollLoc.getY(), scrollDim.width, scrollDim.height - verticalScrollGap);
        } catch (NoSuchElementException e) {
            logger.verbose("WARNING: couldn't find scrollview, returning empty Region");
            reg =new Region(0, 0, 0, 0);
        }
        logger.verbose("The region of the scrollable view is " + reg + ", accounting for a vertical " +
            "scroll gap of " + verticalScrollGap);
        return reg;
    }

    public Location getFirstVisibleChildLocation() {
        logger.verbose("Started..");
        WebElement childElement;
        try {
             childElement = getCachedFirstVisibleChild();
        } catch (NoSuchElementException e) {
            logger.verbose("No child element found. Using (0,0).");
            return new Location(0, 0);
        }

        Point childLoc = childElement.getLocation();
        logger.verbose("The first visible child is at " + childLoc);
        return new Location(childLoc.getX(), childLoc.getY());
    }

    /**
     * @return The scroll position of the current frame.
     */
    public Location getCurrentPosition(boolean absolute) {
        logger.verbose("AppiumScrollPositionProvider - getCurrentPosition()");
        Location loc = getScrollableViewLocation();
        Location childLoc = getFirstVisibleChildLocation();
        Location pos;
        if (absolute) {
            pos = new Location(loc.getX() * 2 - childLoc.getX(), loc.getY() * 2 - childLoc.getY());
        } else {
            // the position of the scrollview is basically the offset of the first visible child
            pos = new Location(loc.getX() - childLoc.getX(), loc.getY() - childLoc.getY());
        }
        logger.verbose("The current scroll position is " + pos);
        return pos;
    }

    public Location getCurrentPositionWithoutStatusBar(boolean absolute) {
        logger.verbose("AppiumScrollPositionProvider - getCurrentPositionWithoutStatusBar()");
        Location loc = getScrollableViewLocation();
        Location childLoc = getFirstVisibleChildLocation();
        Location pos;
        if (absolute) {
            pos = new Location(loc.getX() * 2 - childLoc.getX(), loc.getY() * 2 - getStatusBarHeight() - childLoc.getY());
        } else {
            // the position of the scrollview is basically the offset of the first visible child
            pos = new Location(loc.getX() - childLoc.getX(), (loc.getY() - getStatusBarHeight()) - childLoc.getY());
        }
        logger.verbose("The current scroll position is " + pos);
        return pos;
    }

    public Location getCurrentPosition() {
        return getCurrentPosition(false);
    }

    /**
     *
     * @return The entire size of the container which the position is relative
     * to.
     */
    public RectangleSize getEntireSize() {
        int windowHeight = driver.manage().window().getSize().getHeight() - getStatusBarHeight();
        logger.verbose("window height: " + windowHeight);
        ContentSize contentSize = getCachedContentSize();
        if (contentSize == null) {
            return eyesDriver.getDefaultContentViewportSize();
        }

        int scrollContentHeight = contentSize.getScrollContentHeight();
        int outsideScrollviewHeight = windowHeight - contentSize.height;
        RectangleSize result = new RectangleSize(contentSize.width,
            scrollContentHeight + outsideScrollviewHeight + verticalScrollGap);
        logger.verbose("AppiumScrollPositionProvider - Entire size: " + result + " (Accounting for " +
            "a vertical scroll gap of " + verticalScrollGap + ", with a scroll content height of " +
            scrollContentHeight + ")");
        return result;
    }

    public PositionMemento getState() {
        return new ScrollPositionMemento(getCurrentPosition());
    }

    public abstract void restoreState(PositionMemento state);

    public void scrollToBottomRight() {
        setPosition(new Location(9999999, 9999999));
    }

    public abstract Location scrollDown(boolean returnAbsoluteLocation);

    public abstract void scrollTo(int startX, int startY, int endX, int endY, boolean shouldCancel);

    int getStatusBarHeight() {
        return eyesDriver.getStatusBarHeight();
    }

    public abstract Region getElementRegion(WebElement element, boolean shouldStitchContent, Boolean statusBarExists);

    protected WebElement getFirstScrollableView() {
        return EyesAppiumUtils.getFirstScrollableView(driver);
    }
}
