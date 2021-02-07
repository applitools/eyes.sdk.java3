package com.applitools.eyes.appium;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.Region;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.utils.GeneralUtils;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebElement;

import java.time.Duration;
import java.util.List;

public class AndroidScrollPositionProvider extends AppiumScrollPositionProvider {

    private Location curScrollPos;
    private Location scrollableViewLoc;
    private RectangleSize entireSize = null;

    public AndroidScrollPositionProvider(Logger logger, EyesAppiumDriver driver) {
        super(logger, driver);
    }

    @Override
    public Location getScrollableViewLocation() {
        if (scrollableViewLoc == null) {
            WebElement activeScroll;
            try {
                activeScroll = getFirstScrollableView();
            } catch (NoSuchElementException e) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
                return new Location(0, 0);
            }
            Point scrollLoc = activeScroll.getLocation();
            scrollableViewLoc = new Location(scrollLoc.x, scrollLoc.y);
        }
        logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK, Pair.of("location", scrollableViewLoc));
        return scrollableViewLoc;
    }

    private void checkCurrentScrollPosition() {
        if (curScrollPos == null) {
            ContentSize contentSize = getCachedContentSize();
            LastScrollData scrollData = EyesAppiumUtils.getLastScrollData(driver);
            curScrollPos = getScrollPosFromScrollData(contentSize, scrollData, 0, false);
        }
    }

    @Override
    public Location getCurrentPosition(boolean absolute) {
        Location loc = getScrollableViewLocation();
        checkCurrentScrollPosition();
        Location pos;
        if (absolute) {
            pos = new Location(loc.getX() + curScrollPos.getX(), loc.getY() + curScrollPos.getY());
        } else {
            pos = new Location(curScrollPos.getX(), curScrollPos.getY());
        }
        logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK, Pair.of("currentPosition", pos));
        return pos;
    }

    @Override
    public Location getCurrentPositionWithoutStatusBar(boolean absolute) {
        Location loc = getScrollableViewLocation();
        checkCurrentScrollPosition();
        Location pos;
        if (absolute) {
            pos = new Location(loc.getX() + curScrollPos.getX(), loc.getY() - getStatusBarHeight() + curScrollPos.getY());
        } else {
            pos = new Location(curScrollPos.getX(), curScrollPos.getY() - getStatusBarHeight());
        }

        logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK, Pair.of("currentPosition", pos));
        return pos;
    }

    @Override
    public Location setPosition(Location location) {
        if (location.getY() == curScrollPos.getY() && location.getX() == curScrollPos.getX()) {
            return curScrollPos;
        }

        Location lastScrollPos = curScrollPos;
        while (curScrollPos.getY() > 0) {
            scroll(false);
            if (lastScrollPos.getY() == curScrollPos.getY()) {
                // if we wound up in the same place after a scroll, abort
                break;
            }
            lastScrollPos = curScrollPos;
        }
        scroll(false); // One more scroll to make sure that first child is fully visible
        entireSize = null;
        return lastScrollPos;
    }

    public void setPosition(WebElement element) {
        try {
            WebElement activeScroll = getFirstScrollableView();
            EyesAppiumUtils.scrollBackToElement((AndroidDriver) driver, (RemoteWebElement) activeScroll,
                (RemoteWebElement) element);

            LastScrollData lastScrollData = EyesAppiumUtils.getLastScrollData(driver);
            curScrollPos = new Location(lastScrollData.scrollX, lastScrollData.scrollY);
        } catch (NoSuchElementException e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
        }
    }

    public void restoreState(PositionMemento state) {
        setPosition(new Location(state.getX(), state.getY()));
    }

    private void scroll(boolean isDown) {
        ContentSize contentSize = getCachedContentSize();
        int extraPadding = (int) (contentSize.height * 0.1); // scroll 10% less than the max
        int startX = contentSize.left + (contentSize.width / 2);
        int startY = contentSize.top + contentSize.height - contentSize.touchPadding - extraPadding;
        int endX = startX;
        int endY = contentSize.top + contentSize.touchPadding + extraPadding;

        // if we're scrolling up, just switch the Y vars
        if (!isDown) {
            int temp = endY;
            endY = startY;
            startY = temp;
        }

        int supposedScrollAmt = startY - endY; // how much we will scroll if we don't hit a barrier

        TouchAction scrollAction = new TouchAction(driver);
        scrollAction.press(new PointOption().withCoordinates(startX, startY)).waitAction(new WaitOptions().withDuration(Duration.ofMillis(1500)));
        scrollAction.moveTo(new PointOption().withCoordinates(endX, endY));
        scrollAction.release();
        driver.performTouchAction(scrollAction);

        // because Android scrollbars are visible a bit after touch, we should wait for them to
        // disappear before handing control back to the screenshotter
        try { Thread.sleep(750); } catch (InterruptedException ignored) {}

        LastScrollData lastScrollData = EyesAppiumUtils.getLastScrollData(driver);
        curScrollPos = getScrollPosFromScrollData(contentSize, lastScrollData, supposedScrollAmt, isDown);
    }

    public Location scrollDown(boolean returnAbsoluteLocation) {
        scroll(true);
        return getCurrentPositionWithoutStatusBar(returnAbsoluteLocation);
    }

    @Override
    public void scrollTo(int startX, int startY, int endX, int endY, boolean shouldCancel) {
        logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK,
                Pair.of("from", new Location(startX, startY)),
                Pair.of("to", new Location(startX, startY)));
        TouchAction scrollAction = new TouchAction(driver);
        scrollAction.press(new PointOption().withCoordinates(startX, startY)).waitAction(new WaitOptions().withDuration(Duration.ofMillis(1500)));
        scrollAction.moveTo(new PointOption().withCoordinates(endX, Math.max(endY - contentSize.touchPadding, 0)));
        if (shouldCancel) {
            scrollAction.cancel();
        } else {
            scrollAction.release();
        }
        driver.performTouchAction(scrollAction);

        curScrollPos = new Location(curScrollPos.getX(), curScrollPos.getY() + startX);

        // because Android scrollbars are visible a bit after touch, we should wait for them to
        // disappear before handing control back to the screenshotter
        try { Thread.sleep(750); } catch (InterruptedException ignored) {}
    }

    public boolean tryScrollWithHelperLibrary(String elementId, int offset, int step, int totalSteps) {
        boolean scrolled = false;
        try {
            MobileElement hiddenElement = ((AndroidDriver<AndroidElement>) driver).findElement(MobileBy.AndroidUIAutomator("new UiSelector().description(\"EyesAppiumHelperEDT\")"));
            if (hiddenElement != null) {
                hiddenElement.setValue("scroll;"+elementId+";"+offset+";"+step+";"+totalSteps);
                hiddenElement.click();
                scrolled = true;
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
        }
        return scrolled;
    }

    public boolean moveToTop(String elementId) {
        boolean scrolled = false;
        try {
            MobileElement hiddenElement = ((AndroidDriver<AndroidElement>) driver).findElement(MobileBy.AndroidUIAutomator("new UiSelector().description(\"EyesAppiumHelperEDT\")"));
            if (hiddenElement != null) {
                hiddenElement.setValue("moveToTop;"+elementId+";0;-1");
                hiddenElement.click();
                scrolled = true;
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
        }
        return scrolled;
    }

    @Override
    public Region getElementRegion(WebElement element, boolean shouldStitchContent, Boolean statusBarExists) {
        Region region = new Region(element.getLocation().getX(),
                element.getLocation().getY(),
                element.getSize().getWidth(),
                element.getSize().getHeight());
        if (shouldStitchContent) {
            logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK,
                    Pair.of("elementClass", element.getAttribute("className")));
            double devicePixelRatio = eyesDriver.getDevicePixelRatio();
            ContentSize contentSize = EyesAppiumUtils.getContentSize(driver, element);
            region = new Region(contentSize.left,
                    (int) (element.getLocation().y * devicePixelRatio),
                    contentSize.width,
                    contentSize.getScrollContentHeight());
            if (element.getAttribute("className").equals("android.support.v7.widget.RecyclerView") ||
                    element.getAttribute("className").equals("androidx.recyclerview.widget.RecyclerView") ||
                    element.getAttribute("className").equals("androidx.viewpager2.widget.ViewPager2") ||
                    element.getAttribute("className").equals("android.widget.ListView") ||
                    element.getAttribute("className").equals("android.widget.GridView")) {
                try {
                    String scrollableContentSize = getScrollableContentSize(element.getAttribute("resourceId"));
                    try {
                        int scrollableHeight = Integer.parseInt(scrollableContentSize);
                        region = new Region((int) (element.getLocation().getX() * devicePixelRatio),
                                (int) (element.getLocation().getY() * devicePixelRatio),
                                (int) (element.getSize().getWidth() * devicePixelRatio),
                                scrollableHeight);
                    } catch (NumberFormatException e) {
                        GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
                    }
                } catch (NoSuchElementException | StaleElementReferenceException e) {
                    GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
                }
            }
        }
        return region;
    }

    private Location getScrollPosFromScrollData(ContentSize contentSize, LastScrollData scrollData, int supposedScrollAmt, boolean isDown) {
        logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK,
                Pair.of("scrollData", scrollData),
                Pair.of("contentSize", contentSize));

        // if we didn't get last scroll data, it should be because we were already at the end of
        // the scroll view. This means, unfortunately, we don't have any data about how much
        // we had to scroll to reach the end. So let's make it up based on the contentSize
        if (scrollData == null) {
            if (isDown) {
                return new Location(curScrollPos.getX(),
                    contentSize.scrollableOffset);
            }

            return new Location(curScrollPos == null ? 0 : curScrollPos.getX(), 0);
        }

        // if we got scrolldata from a ScrollView (not List or Grid), actively set the scroll
        // position with correct x/y values
        if (scrollData.scrollX != -1 && scrollData.scrollY != -1) {
            return new Location(scrollData.scrollX, scrollData.scrollY);
        }

        if (contentSize == null) {
            // It can happens when we use scroll (touch) actions for navigation on some screens before
            // And after that we are executing check() command
            contentSize = new ContentSize();
        }

        // otherwise, if we already have a scroll position, just assume we scrolled exactly as much
        // as the touchaction was supposed to. unfortunately it's not really that simple, because we
        // might think we scrolled a full page but we hit a barrier and only scrolled a bit. so take
        // a peek at the fromIndex of the scrolldata; if the position based on the fromIndex is
        // wildly different than what we thought we scrolled, go with the fromIndex-based position

        // we really need the number of items per row to do this math correctly.
        // since we don't have that, just use the average item height, which means we might get
        // part-rows for gridviews that have multiple items per row
        double avgItemHeight = contentSize.getScrollContentHeight() / scrollData.itemCount;
        int curYPos = curScrollPos == null ? 0 : curScrollPos.getY();
        int yPosByIndex = (int) avgItemHeight * scrollData.fromIndex;
        int yPosByAssumption = curYPos + supposedScrollAmt;
        int newYPos;
        if (((double) Math.abs(yPosByAssumption - yPosByIndex) / contentSize.height) > 0.1) {
            // if the difference is more than 10% of the view height, go with index-based
            newYPos = yPosByIndex;
        } else {
            newYPos = yPosByAssumption;
        }

        return new Location(curScrollPos == null ? 0 : curScrollPos.getX(), newYPos);
    }

    @Override
    public RectangleSize getEntireSize() {
        if (curScrollPos != null && curScrollPos.getY() != 0 && entireSize != null) {
            return entireSize;
        }
        int windowHeight = driver.manage().window().getSize().getHeight() - getStatusBarHeight();
        ContentSize contentSize = getCachedContentSize();
        if (contentSize == null) {
            return eyesDriver.getDefaultContentViewportSize();
        }

        int scrollableHeight = 0;

        try {
            WebElement activeScroll = scrollRootElement;
            if (activeScroll == null) {
                activeScroll = getFirstScrollableView();
            }
            logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK,
                    Pair.of("elementClass", activeScroll.getAttribute("className")));
            String className = activeScroll.getAttribute("className");

            if (className.equals("android.support.v7.widget.RecyclerView") ||
                    className.equals("androidx.recyclerview.widget.RecyclerView") ||
                    className.equals("androidx.viewpager2.widget.ViewPager2") ||
                    className.equals("android.widget.ListView") ||
                    className.equals("android.widget.GridView")) {
                try {
                    String scrollableContentSize = getScrollableContentSize(activeScroll.getAttribute("resourceId"));
                    try {
                        scrollableHeight = Integer.parseInt(scrollableContentSize);
                    } catch (NumberFormatException e) {
                        GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
                    }
                } catch (NoSuchElementException | StaleElementReferenceException e) {
                    if (contentSize.scrollableOffset > 0) {
                        scrollableHeight = contentSize.scrollableOffset;
                    } else {
                        scrollableHeight = contentSize.height;
                    }
                    GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
                }
            }
        } catch (NoSuchElementException e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
        }

        this.contentSize.scrollableOffset = scrollableHeight == 0 ? contentSize.scrollableOffset : scrollableHeight - contentSize.height;
        int scrollContentHeight = this.contentSize.getScrollContentHeight();
        int outsideScrollviewHeight = windowHeight - contentSize.height;
        entireSize = new RectangleSize(contentSize.width,
                scrollContentHeight + outsideScrollviewHeight + verticalScrollGap);
        logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK,
                Pair.of("entireSize", entireSize),
                Pair.of("verticalScrollGap", verticalScrollGap),
                Pair.of("scrollContentHeight", scrollContentHeight));
        return entireSize;
    }

    @Override
    protected WebElement getFirstScrollableView() {
        WebElement scrollableView = EyesAppiumUtils.getFirstScrollableView(driver);
        if (scrollableView.getAttribute("className").equals("android.widget.HorizontalScrollView")) {
            List<MobileElement> list = driver.findElements(By.xpath(EyesAppiumUtils.SCROLLVIEW_XPATH));
            for (WebElement element : list) {
                if (element.getAttribute("className").equals("android.widget.HorizontalScrollView")) {
                    continue;
                }
                List<MobileElement> child = scrollableView.findElements(By.xpath(EyesAppiumUtils.SCROLLVIEW_XPATH));
                return child.isEmpty() ? element : child.get(0);
            }
        }
        return scrollableView;
    }

    private String getScrollableContentSize(String resourceId) {
        String scrollableContentSize = "";
        String[] version = EyesAppiumUtils.getHelperLibraryVersion(eyesDriver, logger).split("\\.");
        MobileElement hiddenElement;
        if (version.length == 3 &&
                Integer.parseInt(version[0]) >= 1 &&
                Integer.parseInt(version[1]) >= 3 &&
                Integer.parseInt(version[2]) >= 1) {
            hiddenElement = ((AndroidDriver<AndroidElement>) driver).findElement(MobileBy.AndroidUIAutomator("new UiSelector().description(\"EyesAppiumHelperEDT\")"));
            if (hiddenElement != null) {
                String elementId = resourceId.split("/")[1];
                hiddenElement.setValue("offset;"+elementId+";0;0;0");
                hiddenElement.click();
                scrollableContentSize = hiddenElement.getText();
            }
        } else {
            hiddenElement = ((AndroidDriver<AndroidElement>) driver).findElement(MobileBy.AndroidUIAutomator("new UiSelector().description(\"EyesAppiumHelper\")"));
            if (hiddenElement != null) {
                hiddenElement.click();
                scrollableContentSize = hiddenElement.getText();
            }
        }
        logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK,
                Pair.of("scrollableHeightFromHelper", scrollableContentSize));
        return scrollableContentSize;
    }
}
