package com.applitools.eyes.appium;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.Region;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.selenium.positioning.ScrollPositionMemento;
import com.applitools.eyes.selenium.positioning.ScrollPositionProvider;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import io.appium.java_client.AppiumDriver;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.*;

public abstract class AppiumScrollPositionProvider implements ScrollPositionProvider {

    protected final Logger logger;
    protected final AppiumDriver driver;
    protected final EyesAppiumDriver eyesDriver;
    protected double distanceRatio;
    protected int verticalScrollGap;
    protected WebElement cutElement = null;
    protected WebElement scrollRootElement = null;

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
            firstVisibleChild = EyesAppiumUtils.getFirstVisibleChild(activeScroll);
        } else {
            Rectangle firstVisibleChildRect = firstVisibleChild.getRect();
            if (firstVisibleChildRect.getWidth() == 0 && firstVisibleChildRect.getHeight() == 0) {
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
            logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK, Pair.of("contentSize", contentSize));
        } catch (NoSuchElementException e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
        }

        return contentSize;
    }

    public Location getScrollableViewLocation() {
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
        if (!isVerticalScrollGapSet) {
            verticalScrollGap = firstVisChildLoc.y - scrollLoc.y;
            isVerticalScrollGapSet = true;
        }
        Location loc = new Location(scrollLoc.x, scrollLoc.y + verticalScrollGap);
        logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK,
                Pair.of("location", loc),
                Pair.of("verticalScrollGap", verticalScrollGap));
        return loc;
    }

    public Region getScrollableViewRegion() {
        WebElement activeScroll;
        Region reg;
        try {
            activeScroll = getFirstScrollableView();
            Location scrollLoc = getScrollableViewLocation();
            Dimension scrollDim = activeScroll.getSize();
            reg = new Region(scrollLoc.getX(), scrollLoc.getY(), scrollDim.width, scrollDim.height - verticalScrollGap);
        } catch (NoSuchElementException e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
            reg = new Region(0, 0, 0, 0);
        }

        logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK,
                Pair.of("region", reg),
                Pair.of("verticalScrollGap", verticalScrollGap));
        return reg;
    }

    public Location getFirstVisibleChildLocation() {
        WebElement childElement;
        try {
             childElement = getCachedFirstVisibleChild();
        } catch (NoSuchElementException e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, e);
            return new Location(0, 0);
        }

        Point childLoc = childElement.getLocation();
        return new Location(childLoc.getX(), childLoc.getY());
    }

    /**
     * @return The scroll position of the current frame.
     */
    public Location getCurrentPosition(boolean absolute) {
        Location loc = getScrollableViewLocation();
        Location childLoc = getFirstVisibleChildLocation();
        Location pos;
        if (absolute) {
            pos = new Location(loc.getX() * 2 - childLoc.getX(), loc.getY() * 2 - childLoc.getY());
        } else {
            // the position of the scrollview is basically the offset of the first visible child
            pos = new Location(loc.getX() - childLoc.getX(), loc.getY() - childLoc.getY());
        }
        logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK, Pair.of("currentPosition", pos));
        return pos;
    }

    public Location getCurrentPositionWithoutStatusBar(boolean absolute) {
        Location loc = getScrollableViewLocation();
        Location childLoc = getFirstVisibleChildLocation();
        Location pos;
        if (absolute) {
            pos = new Location(loc.getX() * 2 - childLoc.getX(), loc.getY() * 2 - getStatusBarHeight() - childLoc.getY());
        } else {
            // the position of the scrollview is basically the offset of the first visible child
            pos = new Location(loc.getX() - childLoc.getX(), (loc.getY() - getStatusBarHeight()) - childLoc.getY());
        }
        logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK, Pair.of("currentPosition", pos));
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
        ContentSize contentSize = getCachedContentSize();
        if (contentSize == null) {
            return eyesDriver.getDefaultContentViewportSize();
        }

        int scrollContentHeight = contentSize.getScrollContentHeight();
        int outsideScrollViewHeight = windowHeight - contentSize.height;
        return new RectangleSize(contentSize.width,
            scrollContentHeight + outsideScrollViewHeight + verticalScrollGap);
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
        if (scrollRootElement != null) {
            return scrollRootElement;
        }
        return EyesAppiumUtils.getFirstScrollableView(driver);
    }

    public void cleanupCachedData() {
        this.contentSize = null;
        this.firstVisibleChild = null;
    }

    public void setScrollRootElement(WebElement scrollRootElement) {
        this.scrollRootElement = scrollRootElement;
    }
}
