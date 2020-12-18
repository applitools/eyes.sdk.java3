package com.applitools.eyes.appium;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.Region;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.utils.ImageUtils;
import io.appium.java_client.MobileBy;
import io.appium.java_client.TouchAction;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;

public class IOSScrollPositionProvider extends AppiumScrollPositionProvider {

    private static final String SCROLL_DIRECTION_UP = "up";
    private static final String SCROLL_DIRECTION_DOWN = "down";
    private static final String SCROLL_DIRECTION_LEFT = "left";
    private static final String SCROLL_DIRECTION_RIGHT = "right";
    private WebElement firstVisibleChild;

    public IOSScrollPositionProvider(Logger logger, EyesAppiumDriver driver) {
        super(logger, driver);
    }

    /**
     * Go to the specified location.
     * @param location The position to scroll to.
     */
    public Location setPosition(Location location) {
        logger.log("Warning: Appium cannot reliably scroll based on location; pass an element instead if you can. Doing nothing");
        Location curPos = getCurrentPosition();
        logger.verbose("Wanting to scroll to " + location);
        logger.verbose("Current scroll position is " + getCurrentPosition());
        Location lastPos;


        HashMap<String, String> args = new HashMap<>();
        String directionY = ""; // empty means we don't have to do any scrolling
        String directionX = "";
        if (curPos.getY() < location.getY()) {
            directionY = SCROLL_DIRECTION_DOWN;
        } else if (curPos.getY() > location.getY()) {
            directionY = SCROLL_DIRECTION_UP;
        }
        if (curPos.getX() < location.getX()) {
            directionX = SCROLL_DIRECTION_RIGHT;
        } else if (curPos.getX() > location.getX()) {
            directionX = SCROLL_DIRECTION_LEFT;
        }


        // first handle any vertical scrolling
        if (!directionY.isEmpty()) {
            logger.verbose("Scrolling to Y component");
            args.put("direction", directionY);
            while ((directionY.equals(SCROLL_DIRECTION_DOWN) && curPos.getY() < location.getY()) ||
                (directionY.equals(SCROLL_DIRECTION_UP) && curPos.getY() > location.getY())) {
                logger.verbose("Scrolling " + directionY);
                driver.executeScript("mobile: scroll", args);
                lastPos = curPos;
                curPos = getCurrentPosition();
                logger.verbose("Scrolled to " + curPos);
                if (curPos.getY() == lastPos.getY()) {
                    logger.verbose("Ended up at the same place as last scroll, stopping");
                    break;
                }
            }
        }

        // then handle any horizontal scrolling
        if (!directionX.isEmpty()) {
            logger.verbose("Scrolling to X component");
            args.put("direction", directionX);
            while ((directionX.equals(SCROLL_DIRECTION_RIGHT) && curPos.getX() < location.getX()) ||
                (directionX.equals(SCROLL_DIRECTION_LEFT) && curPos.getX() > location.getX())) {
                logger.verbose("Scrolling " + directionY);
                driver.executeScript("mobile: scroll", args);
                lastPos = curPos;
                curPos = getCurrentPosition();
                if (curPos.getX() == lastPos.getX()) {
                    logger.verbose("Ended up at the same place as last scroll, stopping");
                    break;
                }
            }
        }

        return curPos;
    }

    public void setPosition(WebElement element) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("toVisible", "true");
        params.put("element", element);
        driver.executeScript("mobile: scroll", params);
    }

    public void restoreState(PositionMemento state) {
        logger.log("Warning: AppiumScrollPositionProvider cannot reliably restore position state; doing nothing");
    }


    private double getScrollDistanceRatio() {
        if (distanceRatio == 0.0) {
            int viewportHeight = eyesDriver.getDefaultContentViewportSize(false).getHeight() + eyesDriver.getStatusBarHeight();
            double pixelRatio = eyesDriver.getDevicePixelRatio();
            // viewport height is in device pixels, whereas element heights are in logical pixels,
            // so need to scale the scrollview height accordingly.
            // FIXME: 29/11/2018 should the scrollviewHeight be indeed UNSCALED and WITHOUT the scrollgap
            //double scrollviewHeight = ((getScrollableViewRegion().getHeight() - verticalScrollGap) * pixelRatio);
            double scrollviewHeight = getScrollableViewRegion().getHeight() - (cutElement != null ? cutElement.getSize().getHeight() : 0);
            distanceRatio = scrollviewHeight / viewportHeight;
            logger.verbose("Distance ratio for scroll down based on viewportHeight of " + viewportHeight +
                " and scrollview height of " + scrollviewHeight + " is " + Double.toString(distanceRatio));
        }

        return distanceRatio;
    }

    public Location scrollDown(boolean returnAbsoluteLocation) {
        EyesAppiumUtils.scrollByDirection(driver, SCROLL_DIRECTION_DOWN, getScrollDistanceRatio());
        return getCurrentPositionWithoutStatusBar(returnAbsoluteLocation);
    }

    @Override
    public void scrollTo(int startX, int startY, int endX, int endY, boolean shouldCancel) {
        logger.verbose("Trying to scroll from startX: " + startX + " | startY: " + startY + " | endX: " + endX + " | endY: " + endY);

        TouchAction scrollAction = new TouchAction(driver);
        scrollAction.press(new PointOption().withCoordinates(startX, startY)).waitAction(new WaitOptions().withDuration(Duration.ofMillis(3000)));;
        scrollAction.moveTo(new PointOption().withCoordinates(startX, endY));
        scrollAction.release();

        driver.performTouchAction(scrollAction);

        try { Thread.sleep(750); } catch (InterruptedException ignored) {}
    }

    @Override
    public Region getElementRegion(WebElement element, boolean shouldStitchContent, Boolean statusBarExists) {
        double devicePixelRatio = eyesDriver.getDevicePixelRatio();
        Region region = new Region((int) (element.getLocation().getX() * devicePixelRatio),
                (int) (element.getLocation().getY() * devicePixelRatio),
                (int) (element.getSize().getWidth() * devicePixelRatio),
                (int) (element.getSize().getHeight() * devicePixelRatio));;
        logger.verbose("Element is instance of " + element.getAttribute("type"));
        if (shouldStitchContent) {
            ContentSize contentSize = EyesAppiumUtils.getContentSize(driver, element);
            /*
            * The result of EyesAppiumUtils.getContentSize() is different in the same conditions for different types of views.
            * E.g. contentSize.top value for type 'XCUIElementTypeTable' INcludes the size of status bar(of cause if
            * it is visible). But contentSize.top for type 'XCUIElementTypeScrollView' returnes value
            * EXcluding size of status bar.
            *
            * It happens so because Appium gives us the result of content size for 'XCUIElementTypeTable'. In case
            * 'XCUIElementTypeScrollView' Appium throws an exception and contentSize is set manually from
            * element.getSize() and element.getLocation() values, where element.getLocation().getY() value
            * does NOT include status bar size.
            *
            * Let's imagine we got iPhone 8, status bar is visible(height = 20 points),
            * navigation bar exists as well(height = 44 points). Under navigation bar locates or table view, or scroll view.
            * Frame of both views will be the same: {(0, 64), (375, 603)}. Height of internal content equals 1000 for both views.
            * So result of EyesAppiumUtils.getContentSize() for views will be like that:
            * - table view: {(0, 64), (375, 1000)}
            * - scroll view: {(0, 44), (375, 1000)}
            *
            * Value element.getRect().getY() always INcludes status bar size. Let's use it in calculations.
            * */
            switch (element.getAttribute("type")) {
                case "XCUIElementTypeTable":
                    List<WebElement> list = element.findElements(MobileBy.xpath("//XCUIElementTypeTable[1]/*"));
                    if (!list.isEmpty()) {
                        WebElement lastElement = list.get(list.size()-1);
                        contentSize.scrollableOffset = lastElement.getLocation().getY() + lastElement.getSize().getHeight()
                                - element.getRect().getY() + eyesDriver.getStatusBarHeight();
                    }
                    break;
                case "XCUIElementTypeScrollView":
                    list = element.findElements(MobileBy.xpath("//XCUIElementTypeScrollView[1]/*"));
                    if (!list.isEmpty()) {
                        WebElement firstElement = list.get(0);
                        contentSize.scrollableOffset = firstElement.getLocation().getY() + firstElement.getSize().getHeight()
                                - element.getRect().getY() + eyesDriver.getStatusBarHeight();
                    }
                    break;
            }

            // Correct Y coordinate by status bar size.
            contentSize.top = positionCorrectionRegardingStatusBar(element.getRect().getY(), statusBarExists);
            if (contentSize.scrollableOffset == 0) {
                contentSize.scrollableOffset = contentSize.height;
            }

            region = new Region((int) (contentSize.left * devicePixelRatio),
                    (int) (contentSize.top * devicePixelRatio),
                    (int) (contentSize.width * devicePixelRatio),
                    (int) (contentSize.getScrollContentHeight() * devicePixelRatio));
        }
        return region;
    }

    @Nullable
    @Override
    protected ContentSize getCachedContentSize() {
        try {
            WebElement activeScroll = getFirstScrollableView();
            logger.verbose("Scrollable element is type of " + activeScroll.getAttribute("type"));
            contentSize = EyesAppiumUtils.getContentSize(driver, activeScroll);
            contentSize.scrollableOffset = getEntireScrollableHeight(activeScroll, contentSize);
        } catch (NoSuchElementException e) {
            logger.log("WARNING: No active scroll element, so no content size to retrieve");
            contentSize = null;

            /*
            * To get more information about view hierarchy we printed page source to the logs
            * and saving debug screenshot for current screen
             */
            String base64 = driver.getScreenshotAs(OutputType.BASE64);
            BufferedImage image = ImageUtils.imageFromBase64(base64);

            logger.verbose(driver.getPageSource());
        }
        logger.verbose("Retrieved contentSize, it is: " + contentSize);
        return contentSize;
    }

    @Override
    protected WebElement getCachedFirstVisibleChild () {
        WebElement activeScroll = getFirstScrollableView();
        if (firstVisibleChild == null) {
            logger.verbose("Could not find first visible child in cache, getting (this could take a while)");
            firstVisibleChild = getFirstChild(activeScroll);
        } else {
            Rectangle firstVisibleChildRect = firstVisibleChild.getRect();
            if (firstVisibleChildRect.getWidth() == 0 && firstVisibleChildRect.getHeight() == 0) {
                logger.verbose("Cached visible child is invalid for some reason(e.g. user opened another screen and current firstVisibleChild is useless" +
                        " and it is not already in the view hierarchy). It should be updated. Getting (this could take a while)");
                firstVisibleChild = getFirstChild(activeScroll);
            }
        }
        return firstVisibleChild;
    }

    private WebElement getFirstChild(WebElement activeScroll) {
        if (activeScroll.getAttribute("type").equals("XCUIElementTypeTable")) {
            List<WebElement> list = activeScroll.findElements(MobileBy.xpath("//XCUIElementTypeTable[1]/*"));
            WebElement firstCell = getFirstCellForXCUIElementTypeTable(list);
            if (firstCell == null) {
                return EyesAppiumUtils.getFirstVisibleChild(activeScroll);
            }
            return firstCell;
        } else {
            return EyesAppiumUtils.getFirstVisibleChild(activeScroll);
        }
    }

    private WebElement getFirstCellForXCUIElementTypeTable(List<WebElement> list) {
        for (WebElement element : list) {
            if (element.getTagName().equals("XCUIElementTypeCell")) {
                return element;
            }
        }
        return null;
    }

    private int positionCorrectionRegardingStatusBar(int position, Boolean statusBarExists) {
        // Position correction regarding the status bar height
        if (statusBarExists != null && !statusBarExists) {
            return position;
        }
        Double platformVersion = null;
        try {
            String platformVersionString = EyesDriverUtils.getPlatformVersion(driver);
            if (platformVersionString != null) {
                platformVersion = Double.valueOf(platformVersionString);
            }
        } catch (NumberFormatException e) {
            logger.verbose("Could not extract platform version from capabilities.");
        }
        // Since iOS 13 we can not get correct status bar height
        if (platformVersion != null && platformVersion.compareTo(Double.valueOf("13.0")) >= 0) {
            return position - getDeviceStatusBarHeight(((String) driver.getCapabilities().getCapability(MobileCapabilityType.DEVICE_NAME)).toLowerCase());
        } else {
            return position - getStatusBarHeight();
        }
    }

    private int getDeviceStatusBarHeight(String deviceName) {
        int statusBarHeight = 0;
        int density = 1;
        switch (deviceName) {
            case "iphone 6":
            case "iphone 6s":
            case "iphone 7":
            case "iphone 8":
                statusBarHeight = 40;
                density = 2;
                break;
            case "iphone 6 plus":
            case "iphone 6s plus":
            case "iphone 7 plus":
            case "iphone 8 plus":
                statusBarHeight = 60;
                density = 3;
                break;
            case "iphone x":
            case "iphone xs":
            case "iphone xs max":
            case "iphone 11 pro":
            case "iphone 11 pro max":
                statusBarHeight = 132;
                density = 3;
                break;
            case "iphone xr":
            case "iphone 11":
                statusBarHeight = 88;
                density = 2;
                break;
        }
        return statusBarHeight / density;
    }

    private int getEntireScrollableHeight(WebElement element, ContentSize contentSize) {
        /*
        * We should calculate entire scrollable height for some element types
        * because Appium returns wrong scrollable height.
         */
        int scrollableOffset = contentSize.scrollableOffset;
        switch (element.getAttribute("type")) {
            case "XCUIElementTypeTable":
                List<WebElement> list = element.findElements(MobileBy.xpath("//XCUIElementTypeTable[1]/*"));
                if (!list.isEmpty()) {
                    WebElement lastElement = list.get(list.size()-1);
                    scrollableOffset = lastElement.getLocation().getY() + lastElement.getSize().getHeight();
                } else {
                    logger.verbose("XCUIElementTypeTable does not have child...");
                }
                break;
            case "XCUIElementTypeScrollView":
                list = element.findElements(MobileBy.xpath("//XCUIElementTypeScrollView[1]/*"));
                if (!list.isEmpty()) {
                    WebElement firstElement = list.get(0);
                    scrollableOffset = firstElement.getLocation().getY() + firstElement.getSize().getHeight();
                } else {
                    logger.verbose("XCUIElementTypeScrollView does not have child...");
                }
                break;
        }
        return scrollableOffset;
    }
}
