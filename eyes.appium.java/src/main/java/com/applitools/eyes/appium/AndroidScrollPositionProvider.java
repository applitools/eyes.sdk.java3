package com.applitools.eyes.appium;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.Region;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.selenium.positioning.ScrollPositionMemento;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebElement;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class AndroidScrollPositionProvider extends AppiumScrollPositionProvider {

    private Location curScrollPos;
    private Location scrollableViewLoc;
    private RectangleSize entireSize = null;
    private String scrollRootElement = null;

    public AndroidScrollPositionProvider(Logger logger, EyesAppiumDriver driver) {
        super(logger, driver);
    }

    @Override
    public Location getScrollableViewLocation() {
        logger.verbose("Getting the location of the scrollable view..");
        if (scrollableViewLoc == null) {
            WebElement activeScroll;
            try {
                activeScroll = getFirstScrollableView();
            } catch (NoSuchElementException e) {
                logger.verbose("WARNING: could not find a scrollable view, using (0,0)");
                return new Location(0, 0);
            }
            Point scrollLoc = activeScroll.getLocation();
            scrollableViewLoc = new Location(scrollLoc.x, scrollLoc.y);
        }
        logger.verbose("The location of the scrollable view is " + scrollableViewLoc);
        return scrollableViewLoc;
    }

    private void checkCurrentScrollPosition() {
        if (curScrollPos == null) {
            logger.verbose("There was no current scroll position registered, so setting it for the first time");
            ContentSize contentSize = getCachedContentSize();
            LastScrollData scrollData = EyesAppiumUtils.getLastScrollData(driver);
            logger.verbose("Last scroll data from the server was: " + scrollData);
            curScrollPos = getScrollPosFromScrollData(contentSize, scrollData, 0, false);
        }
    }

    @Override
    public Location getCurrentPosition(boolean absolute) {
        logger.verbose("AndroidScrollPositionProvider - getCurrentPosition()");
        Location loc = getScrollableViewLocation();
        checkCurrentScrollPosition();
        Location pos;
        if (absolute) {
            pos = new Location(loc.getX() + curScrollPos.getX(), loc.getY() + curScrollPos.getY());
        } else {
            pos = new Location(curScrollPos.getX(), curScrollPos.getY());
        }
        logger.verbose("The current scroll position is " + pos);
        return pos;
    }

    @Override
    public Location getCurrentPositionWithoutStatusBar(boolean absolute) {
        logger.verbose("AndroidScrollPositionProvider - getCurrentPositionWithoutStatusBar()");
        Location loc = getScrollableViewLocation();
        checkCurrentScrollPosition();
        Location pos;
        if (absolute) {
            pos = new Location(loc.getX() + curScrollPos.getX(), loc.getY() - getStatusBarHeight() + curScrollPos.getY());
        } else {
            pos = new Location(curScrollPos.getX(), curScrollPos.getY() - getStatusBarHeight());
        }
        logger.verbose("The current scroll position is " + pos);
        return pos;
    }

    @Override
    public Location setPosition(Location location) {
        if (location.getY() != 0 && location.getX() != 0) {
            logger.log("Warning: tried to set position on an Android scroll view, which is not possible");
        }

        if (location.getY() == curScrollPos.getY() && location.getX() == curScrollPos.getX()) {
            logger.log("Already at the desired position, doing nothing");
            return curScrollPos;
        }

        logger.verbose(
            "Setting position to 0, 0 by scrolling all the way back to the top");
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
        logger.log("Warning: can only scroll back to elements that have already been seen");
        try {
            WebElement activeScroll = getFirstScrollableView();
            EyesAppiumUtils.scrollBackToElement((AndroidDriver) driver, (RemoteWebElement) activeScroll,
                (RemoteWebElement) element);

            LastScrollData lastScrollData = EyesAppiumUtils.getLastScrollData(driver);
            logger.verbose("After scrolling back to first child, lastScrollData was: " + lastScrollData);
            curScrollPos = new Location(lastScrollData.scrollX, lastScrollData.scrollY);
        } catch (NoSuchElementException e) {
            logger.verbose("Could not set position because there was no scrollable view; doing nothing");
        }
    }

    public void restoreState(PositionMemento state) {
        setPosition(new Location(((ScrollPositionMemento) state).getX(), ((ScrollPositionMemento) state).getY()));
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
        logger.verbose("After scroll lastScrollData was: " + lastScrollData);
        curScrollPos = getScrollPosFromScrollData(contentSize, lastScrollData, supposedScrollAmt, isDown);
    }

    public Location scrollDown(boolean returnAbsoluteLocation) {
        scroll(true);
        return getCurrentPositionWithoutStatusBar(returnAbsoluteLocation);
    }

    @Override
    public void scrollTo(int startX, int startY, int endX, int endY, boolean shouldCancel) {
        logger.verbose("Trying to scroll from startX: " + startX + " | startY: " + startY + " | endX: " + endX + " | endY: " + endY);
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
            logger.verbose("Trying to scroll with helper library...");
            MobileElement hiddenElement = ((AndroidDriver<AndroidElement>) driver).findElement(MobileBy.AndroidUIAutomator("new UiSelector().description(\"EyesAppiumHelperEDT\")"));
            if (hiddenElement != null) {
                hiddenElement.setValue("scroll;"+elementId+";"+offset+";"+step+";"+totalSteps);
                hiddenElement.click();
                scrolled = true;
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
        } catch (NoSuchElementException | StaleElementReferenceException ignored) {
            logger.verbose("Could not get EyesAppiumHelperEDT element for scrolling.");
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
        } catch (NoSuchElementException | StaleElementReferenceException ignored) {
            logger.verbose("Could not get EyesAppiumHelperEDT element for scrolling.");
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
            logger.verbose("Element is instance of " + element.getAttribute("className"));
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
                    MobileElement hiddenElement = ((AndroidDriver<AndroidElement>) driver).findElement(MobileBy.AndroidUIAutomator("new UiSelector().description(\"EyesAppiumHelper\")"));
                    if (hiddenElement != null) {
                        hiddenElement.click();

                        String scrollableContentSize = hiddenElement.getText();
                        try {
                            int scrollableHeight = Integer.valueOf(scrollableContentSize);
                            logger.verbose("Scrollable height received from EyesAppiumHelper = " + scrollableContentSize);
                            region = new Region((int) (element.getLocation().getX() * devicePixelRatio),
                                    (int) (element.getLocation().getY() * devicePixelRatio),
                                    (int) (element.getSize().getWidth() * devicePixelRatio),
                                    scrollableHeight);
                        } catch (NumberFormatException nfe) {
                            logger.verbose("Could not parse scrollable content height");
                        }
                    }
                } catch (NoSuchElementException | StaleElementReferenceException ignored) {
                    logger.verbose("Could not get EyesAppiumHelper element.");
                }
            }
        }
        return region;
    }

    private Location getScrollPosFromScrollData(ContentSize contentSize, LastScrollData scrollData, int supposedScrollAmt, boolean isDown) {
        logger.verbose("Getting scroll position from last scroll data (" + scrollData + ") and " +
            "contentSize (" + contentSize + ")");

        // if we didn't get last scroll data, it should be because we were already at the end of
        // the scroll view. This means, unfortunately, we don't have any data about how much
        // we had to scroll to reach the end. So let's make it up based on the contentSize
        if (scrollData == null) {
            logger.verbose("Did not get last scroll data; assume there was no more scroll");
            if (isDown) {
                logger.verbose(
                    "Since we're scrolling down, setting Y location to the last page of the scrollableOffset");
                return new Location(curScrollPos.getX(),
                    contentSize.scrollableOffset);
            }

            logger.verbose("Since we're scrolling up, just say we reached 0, 0");
            return new Location(curScrollPos == null ? 0 : curScrollPos.getX(), 0);
        }

        // if we got scrolldata from a ScrollView (not List or Grid), actively set the scroll
        // position with correct x/y values
        if (scrollData.scrollX != -1 && scrollData.scrollY != -1) {
            logger.verbose("Setting scroll position based on pixel values from scroll data");
            return new Location(scrollData.scrollX, scrollData.scrollY);
        }

        if (contentSize == null) {
            logger.verbose("Retrieved content size is null... Initialized temporary with default values");
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
        logger.verbose("By assumption we are now at " + yPosByAssumption + " pixels, and by item " +
            "index we are now at " + yPosByIndex + " pixels");
        if (((double) Math.abs(yPosByAssumption - yPosByIndex) / contentSize.height) > 0.1) {
            // if the difference is more than 10% of the view height, go with index-based
            newYPos = yPosByIndex;
            logger.verbose("Estimating that current scroll Y position is " + newYPos + ", based on item count of " +
                scrollData.itemCount + ", avg item height of " + avgItemHeight + ", and scrolled-to " +
                "fromIndex of " + scrollData.fromIndex);
        } else {
            newYPos = yPosByAssumption;
            logger.verbose("Assuming we scrolled down exactly " + supposedScrollAmt + " pixels");
        }

        return new Location(curScrollPos == null ? 0 : curScrollPos.getX(), newYPos);
    }

    @Override
    public RectangleSize getEntireSize() {
        if (curScrollPos != null && curScrollPos.getY() != 0 && entireSize != null) {
            return entireSize;
        }
        int windowHeight = driver.manage().window().getSize().getHeight() - getStatusBarHeight();
        logger.verbose("window height: " + windowHeight);

        ContentSize contentSize = getCachedContentSize();
        if (contentSize == null) {
            return eyesDriver.getDefaultContentViewportSize();
        }

        int scrollableHeight = 0;

        try {
            WebElement activeScroll;
            if (scrollRootElement != null) {
                activeScroll = driver.findElement(MobileBy.id(scrollRootElement));
            } else {
                activeScroll = getFirstScrollableView();
            }
            logger.verbose("Scrollable element is instance of " + activeScroll.getAttribute("className"));
            String className = activeScroll.getAttribute("className");

            if (className.equals("android.support.v7.widget.RecyclerView") ||
                    className.equals("androidx.recyclerview.widget.RecyclerView") ||
                    className.equals("androidx.viewpager2.widget.ViewPager2") ||
                    className.equals("android.widget.ListView") ||
                    className.equals("android.widget.GridView")) {
                try {
                    MobileElement hiddenElement = ((AndroidDriver<AndroidElement>) driver).findElement(MobileBy.AndroidUIAutomator("new UiSelector().description(\"EyesAppiumHelper\")"));
                    if (hiddenElement != null) {
                        hiddenElement.click();

                        String scrollableContentSize = hiddenElement.getText();
                        try {
                            scrollableHeight = Integer.valueOf(scrollableContentSize);
                            logger.verbose("Scrollable height received from EyesAppiumHelper = " + scrollableContentSize);
                        } catch (NumberFormatException nfe) {
                            logger.verbose("Could not parse scrollable content height");
                        }
                    }
                } catch (NoSuchElementException | StaleElementReferenceException ignored) {
                    if (contentSize.scrollableOffset > 0) {
                        scrollableHeight = contentSize.scrollableOffset;
                    } else {
                        scrollableHeight = contentSize.height;
                    }
                    logger.verbose("Could not get EyesAppiumHelper element. Return scrollable offset from cached content size (" + scrollableHeight + ")");
                }
            }
        } catch (NoSuchElementException ex) {
            logger.log("WARNING: No active scroll element, so no scrollable height to retrieve");
        }

        this.contentSize.scrollableOffset = scrollableHeight == 0 ? contentSize.scrollableOffset : scrollableHeight - contentSize.height;
        int scrollContentHeight = this.contentSize.getScrollContentHeight();
        int outsideScrollviewHeight = windowHeight - contentSize.height;
        entireSize = new RectangleSize(contentSize.width,
                scrollContentHeight + outsideScrollviewHeight + verticalScrollGap);
        logger.verbose("AppiumScrollPositionProvider - Entire size: " + entireSize + " (Accounting for " +
                "a vertical scroll gap of " + verticalScrollGap + ", with a scroll content height of " +
                scrollContentHeight + ")");
        return entireSize;
    }

    public void setScrollRootElement(String scrollRootElement) {
        this.scrollRootElement = scrollRootElement;
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
}
