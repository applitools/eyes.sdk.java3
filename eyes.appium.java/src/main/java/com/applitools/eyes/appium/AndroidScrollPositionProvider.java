package com.applitools.eyes.appium;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.Region;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.utils.GeneralUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.MobileBy;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebElement;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class AndroidScrollPositionProvider extends AppiumScrollPositionProvider {

    private Location curScrollPos;
    private Location scrollableViewLoc;
    private RectangleSize entireSize = null;
    private String[] helperLibraryVersion = null;

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

    private void checkCurrentScrollPosition() {
        if (curScrollPos == null) {
            ContentSize contentSize = getCachedContentSize();
            if (contentSize.isEmpty()) {
                curScrollPos = new Location(0, 0);
            } else {
                LastScrollData scrollData = EyesAppiumUtils.getLastScrollData(driver);
                curScrollPos = getScrollPosFromScrollData(contentSize, scrollData, 0, false);
            }
//            LastScrollData scrollData = EyesAppiumUtils.getLastScrollData(driver);
//            curScrollPos = getScrollPosFromScrollData(contentSize, scrollData, 0, false);
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

        try {
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence scrollAction = new Sequence(finger, 1);
            scrollAction.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX, startY));
            scrollAction.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            scrollAction.addAction(finger.createPointerMove(Duration.ofMillis(1500), PointerInput.Origin.viewport(), endX, endY));
            scrollAction.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(Collections.singletonList(scrollAction));
        } catch (UnsupportedCommandException e) {
            logger.log(TraceLevel.Warn, eyesDriver.getTestId(), Stage.CHECK, "Using legacy scroll actions");
            TouchAction scrollAction = new TouchAction((PerformsTouchActions) driver);
            scrollAction.press(new PointOption().withCoordinates(startX, startY)).waitAction(new WaitOptions().withDuration(Duration.ofMillis(1500)));
            scrollAction.moveTo(new PointOption().withCoordinates(endX, endY));
            scrollAction.release();
            ((PerformsTouchActions) driver).performTouchAction(scrollAction);
        }

        // because Android scrollbars are visible a bit after touch, we should wait for them to
        // disappear before handing control back to the screenshotter
        try {
            Thread.sleep(750);
        } catch (InterruptedException ignored) {
        }

        curScrollPos = new Location(curScrollPos.getX(), curScrollPos.getY() + supposedScrollAmt);
        if (curScrollPos.getY() <= 0) {
            curScrollPos = new Location(curScrollPos.getX(), 0);
        }
    }

    @Override
    public void scrollTo(int startX, int startY, int endX, int endY, boolean shouldCancel) {
        logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK,
                Pair.of("from", new Location(startX, startY)),
                Pair.of("to", new Location(startX, startY)));

        try {
            scrollToW3c(startX, startY, endX, endY);
        } catch (UnsupportedCommandException e) {
            logger.log(TraceLevel.Warn, eyesDriver.getTestId(), Stage.CHECK, "Using legacy scroll actions");
            scrollToLegacy(startX, startY, endX, endY, contentSize.touchPadding, 1500, shouldCancel);
        }

        curScrollPos = new Location(curScrollPos.getX(), curScrollPos.getY() + startY - endY);

        // because Android scrollbars are visible a bit after touch, we should wait for them to
        // disappear before handing control back to the screenshotter
        try {
            Thread.sleep(750);
        } catch (InterruptedException ignored) {
        }
    }

    public boolean tryScrollWithHelperLibrary(String elementId, int offset, int step, int totalSteps) {
        boolean scrolled = false;
        try {
            WebElement hiddenElement = getHelperEDTElement();
            if (hiddenElement != null) {
                hiddenElement.sendKeys("scroll;" + elementId + ";" + offset + ";" + step + ";" + totalSteps);
                doHelperAction(hiddenElement);
                scrolled = true;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
                hiddenElement.clear();
            }
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
        }
        return scrolled;
    }

    public boolean moveToTop(String elementId) {
        boolean scrolled = false;
        try {
            WebElement hiddenElement = getHelperEDTElement();
            if (hiddenElement != null) {
                hiddenElement.sendKeys("moveToTop;" + elementId + ";0;-1");
                doHelperAction(hiddenElement);
                scrolled = true;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                hiddenElement.clear();
            }
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
        }
        return scrolled;
    }

    public int getBehaviorOffsetWithHelperLibrary() {
        int offset = -1;
        if (scrollRootElement == null) {
            return offset;
        }
        String elementId = scrollRootElement.getAttribute("resourceId").split("/")[1];
        try {
            WebElement hiddenElement = getHelperEDTElement();
            if (hiddenElement != null) {
                hiddenElement.sendKeys("behaviorOffset;" + elementId + ";0;0");
                doHelperAction(hiddenElement);
                offset = Integer.parseInt(hiddenElement.getText());
                hiddenElement.clear();
            }
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
        }
        return offset;
    }

    public boolean tryScrollBehaviorOffsetWithHelperLibrary(String elementId, int offset) {
        boolean scrolled = false;
        try {
            WebElement hiddenElement = getHelperEDTElement();
            if (hiddenElement != null) {
                hiddenElement.sendKeys("behaviorScroll;" + elementId + ";" + offset + ";0");
                doHelperAction(hiddenElement);
                scrolled = true;
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ignored) {}
                hiddenElement.clear();
            }
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
        }
        return scrolled;
    }

    public String tryGetClassWithHelperLibrary() {
        String className = null;
        if (scrollRootElement != null) {
            String elementId = scrollRootElement.getAttribute("resourceId").split("/")[1];
            try {
                WebElement hiddenElement = getHelperEDTElement();
                if (hiddenElement != null) {
                    hiddenElement.sendKeys("className;"+elementId+";0;0");
                    doHelperAction(hiddenElement);
                    className = hiddenElement.getText();
                    if (className.isEmpty()) {
                        className = null;
                    }
                    hiddenElement.clear();
                }
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
            }
        }
        return className;
    }

    public void tryDumpVHSWithHelperLibrary() {
        try {
            WebElement hiddenElement = driver.findElement(MobileBy.AndroidUIAutomator("new UiSelector().description(\"EyesAppiumHelperEDT\")"));
            if (hiddenElement != null) {
                hiddenElement.sendKeys("dumpVHS;0;0;0");
                hiddenElement.click();
                try {
                    Thread.sleep(5_000);
                } catch (InterruptedException ignored) {
                }
                hiddenElement.clear();
            }
        } catch (NoSuchElementException | NumberFormatException | StaleElementReferenceException e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
        }
    }

    @Override
    public Region getElementRegion(WebElement element, boolean shouldStitchContent, Boolean statusBarExists) {
        Region region = new Region(element.getLocation().getX(),
                element.getLocation().getY(),
                element.getSize().getWidth(),
                element.getSize().getHeight());
        if (shouldStitchContent) {
            String className = tryGetClassWithHelperLibrary();
            if (className == null) {
                className = element.getAttribute("className");
            }
            logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK,
                    Pair.of("elementClass", className));
            double devicePixelRatio = eyesDriver.getDevicePixelRatio();
            ContentSize contentSize = EyesAppiumUtils.getContentSize(driver, element);
            region = new Region(contentSize.left,
                    (int) (element.getLocation().y * devicePixelRatio),
                    contentSize.width,
                    contentSize.getScrollContentHeight());
            if (className.equals("android.support.v7.widget.RecyclerView") ||
                    className.equals("androidx.recyclerview.widget.RecyclerView") ||
                    className.equals("androidx.viewpager2.widget.ViewPager2") ||
                    className.equals("android.widget.ListView") ||
                    className.equals("android.widget.GridView") ||
                    (scrollRootElement != null && className.equals("androidx.core.widget.NestedScrollView"))) {
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
//        int windowHeight = driver.manage().window().getSize().getHeight();
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
            String className = tryGetClassWithHelperLibrary();
            if (className == null) {
                className = activeScroll.getAttribute("className");
            }

            if (className.equals("android.support.v7.widget.RecyclerView") ||
                    className.equals("androidx.recyclerview.widget.RecyclerView") ||
                    className.equals("androidx.viewpager2.widget.ViewPager2") ||
                    className.equals("android.widget.ListView") ||
                    className.equals("android.widget.GridView") ||
                    (scrollRootElement != null && className.equals("androidx.core.widget.NestedScrollView"))) {
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
                int position;
                if (activeScroll instanceof EyesAppiumElement) {
                    position = (int) (activeScroll.getLocation().getY() * eyesDriver.getDevicePixelRatio());
                } else {
                    position = activeScroll.getLocation().getY();
                }
                this.contentSize.top = position;
                this.contentSize.height = activeScroll.getRect().getHeight();
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
        return new RectangleSize(entireSize.getWidth(), entireSize.getHeight());
    }

    @Override
    protected WebElement getFirstScrollableView() {
        if (cachedScrollableView == null) {
            WebElement scrollableView;
            if (scrollRootElement != null) {
                scrollableView = driver.findElement(MobileBy.id(scrollRootElement.getAttribute("resourceId")));
            } else {
                scrollableView = EyesAppiumUtils.getFirstScrollableView(driver);
                if (scrollableView.getAttribute("className").equals("android.widget.HorizontalScrollView")) {
                    List<WebElement> list = driver.findElements(By.xpath(EyesAppiumUtils.SCROLLVIEW_XPATH));
                    for (WebElement element : list) {
                        if (element.getAttribute("className").equals("android.widget.HorizontalScrollView")) {
                            continue;
                        }
                        List<WebElement> child = scrollableView.findElements(By.xpath(EyesAppiumUtils.SCROLLVIEW_XPATH));
                        scrollableView = child.isEmpty() ? element : child.get(0);
                    }
                }
            }
            cachedScrollableView = scrollableView;
        }
        return cachedScrollableView;
    }

    private String getScrollableContentSize(String resourceId) {
        String scrollableContentSize = "";
        if (resourceId == null) {
            return scrollableContentSize;
        }
        helperLibraryVersion = EyesAppiumUtils.getHelperLibraryVersion(eyesDriver, logger).split("\\.");
        WebElement hiddenElement;
        if (helperLibraryVersion.length == 3 &&
                Integer.parseInt(helperLibraryVersion[0]) >= 1 &&
                Integer.parseInt(helperLibraryVersion[1]) >= 3) {
            hiddenElement = getHelperEDTElement();
            if (hiddenElement != null) {
                String elementId = resourceId.split("/")[1];
                hiddenElement.sendKeys("offset;" + elementId + ";0;0;0");
                doHelperAction(hiddenElement);
                scrollableContentSize = hiddenElement.getText();
                hiddenElement.clear();
            }
        } else {
            hiddenElement = driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().description(\"EyesAppiumHelper\")"));
            if (hiddenElement != null) {
                hiddenElement.click();
                scrollableContentSize = hiddenElement.getText();
            }
        }
        logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK,
                Pair.of("scrollableHeightFromHelper", scrollableContentSize));
        return scrollableContentSize;
    }

    @Override
    public void cleanupCachedData() {
        super.cleanupCachedData();
        curScrollPos = null;
        entireSize = null;
        scrollableViewLoc = null;
    }

    private WebElement getHelperEDTElement() {
        return searchForHelperElement("EyesAppiumHelperEDT");
    }

    private WebElement getHelperActionElement() {
        return searchForHelperElement("EyesAppiumHelper_Action");
    }

    private WebElement searchForHelperElement(String contentDescription) {
        WebElement element = null;
        try {
            element =  driver.findElement(AppiumBy.androidUIAutomator(String.format("new UiSelector().description(\"%s\")", contentDescription)));
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
            try { // Try to search for element by XPATH. For case when element has displayed=false
                String xpath = String.format("//android.widget.EditText[@content-desc=\"%s\" and @displayed=\"false\"]", contentDescription);
                element = driver.findElement(AppiumBy.xpath(xpath));
            } catch (Exception e1) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
            }
        }
        return element;
    }

    private void doHelperAction(WebElement hiddenElement) {
        if (helperLibraryVersion == null) {
            helperLibraryVersion = EyesAppiumUtils.getHelperLibraryVersion(eyesDriver, logger).split("\\.");
        }
        if (helperLibraryVersion.length == 3 &&
                Integer.parseInt(helperLibraryVersion[0]) >= 1 &&
                Integer.parseInt(helperLibraryVersion[1]) >= 7) {
            WebElement actionElement = getHelperActionElement();
            if (actionElement != null) {
                actionElement.sendKeys("1");
            } else {
                hiddenElement.click();
            }
        } else {
            hiddenElement.click();
        }
    }
}
