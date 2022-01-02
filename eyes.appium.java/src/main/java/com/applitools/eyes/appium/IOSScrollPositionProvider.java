package com.applitools.eyes.appium;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.Region;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.utils.GeneralUtils;
import com.applitools.utils.ImageUtils;
import io.appium.java_client.MobileBy;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.Collections;
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
     *
     * @param location The position to scroll to.
     */
    public Location setPosition(Location location) {
        Location curPos = getCurrentPosition();
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
            args.put("direction", directionY);
            while ((directionY.equals(SCROLL_DIRECTION_DOWN) && curPos.getY() < location.getY()) ||
                    (directionY.equals(SCROLL_DIRECTION_UP) && curPos.getY() > location.getY())) {
                driver.executeScript("mobile: scroll", args);
                lastPos = curPos;
                curPos = getCurrentPosition();
                if (curPos.getY() == lastPos.getY()) {
                    break;
                }
            }
        }

        // then handle any horizontal scrolling
        if (!directionX.isEmpty()) {
            args.put("direction", directionX);
            while ((directionX.equals(SCROLL_DIRECTION_RIGHT) && curPos.getX() < location.getX()) ||
                    (directionX.equals(SCROLL_DIRECTION_LEFT) && curPos.getX() > location.getX())) {
                driver.executeScript("mobile: scroll", args);
                lastPos = curPos;
                curPos = getCurrentPosition();
                if (curPos.getX() == lastPos.getX()) {
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
    }

    @Override
    public Location getCurrentPositionWithoutStatusBar(boolean absolute) {
        WebElement activeScroll = getFirstScrollableView();
        if (activeScroll.getAttribute("type").equals("XCUIElementTypeCollectionView")) {
            triggerHelperButton();
            try {
                WebElement offsetLabel = driver.findElement(MobileBy.name("applitools_content_offset_label"));
                int contentOffset = (int) Double.parseDouble(offsetLabel.getText().split(",")[1].trim().replace("}", ""));

                Region region = getScrollableViewRegion();
                return new Location(region.getLeft(), region.getTop() + contentOffset - getStatusBarHeight());
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
                logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK, "Can not parse applitools_content_offset_label value");
                return super.getCurrentPositionWithoutStatusBar(absolute);
            } catch (NoSuchElementException ignored) {
                logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK, "Can not find helper library elements");
                return super.getCurrentPositionWithoutStatusBar(absolute);
            }
        } else {
            return super.getCurrentPositionWithoutStatusBar(absolute);
        }
    }

    @Override
    public Location getCurrentPosition(boolean absolute) {
        try {
            WebElement activeScroll = getFirstScrollableView();
            if (activeScroll.getAttribute("type").equals("XCUIElementTypeCollectionView")) {
                triggerHelperButton();
                try {
                    WebElement offsetLabel = driver.findElement(MobileBy.name("applitools_content_offset_label"));
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignored) {
                    }
                    int contentOffset = (int) Double.parseDouble(offsetLabel.getText().split(",")[1].trim().replace("}", ""));
                    return new Location(0, contentOffset);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
                    logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK, "Can not parse applitools_content_offset_label value");
                    return super.getCurrentPosition(absolute);
                } catch (NoSuchElementException ignored) {
                    logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK, "Can not find helper library elements");
                    return super.getCurrentPosition(absolute);
                }
            } else {
                return super.getCurrentPosition(absolute);
            }
        } catch (NoSuchElementException ignored) {
            return super.getCurrentPosition(absolute);
        }
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
        }

        return distanceRatio;
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
            scrollToLegacy(startX, startY, endX, endY, 0, 5000, false);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public Region getElementRegion(WebElement element, boolean shouldStitchContent, Boolean statusBarExists) {
        double devicePixelRatio = eyesDriver.getDevicePixelRatio();
        Region region = new Region((int) (element.getLocation().getX() * devicePixelRatio),
                (int) (element.getLocation().getY() * devicePixelRatio),
                (int) (element.getSize().getWidth() * devicePixelRatio),
                (int) (element.getSize().getHeight() * devicePixelRatio));
        logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK,
                Pair.of("elementClass", element.getAttribute("type")));
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
                        WebElement lastElement = list.get(list.size() - 1);
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
        if (contentSize != null) {
            return contentSize;
        }

        try {
            WebElement activeScroll = getFirstScrollableView();
            contentSize = EyesAppiumUtils.getContentSize(driver, activeScroll);
            contentSize.scrollableOffset = getEntireScrollableHeight(activeScroll, contentSize);
        } catch (NoSuchElementException e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
            contentSize = null;

            /*
             * To get more information about view hierarchy we printed page source to the logs
             * and saving debug screenshot for current screen
             */
            String base64 = driver.getScreenshotAs(OutputType.BASE64);
            BufferedImage image = ImageUtils.imageFromBase64(base64);

            logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK, Pair.of("pageSource", driver.getPageSource()));
        }
        return contentSize;
    }

    @Override
    protected WebElement getCachedFirstVisibleChild() {
        WebElement activeScroll = getFirstScrollableView();
        if (firstVisibleChild == null) {
            firstVisibleChild = getFirstChild(activeScroll);
        } else {
            Rectangle firstVisibleChildRect = firstVisibleChild.getRect();
            if (firstVisibleChildRect.getWidth() == 0 && firstVisibleChildRect.getHeight() == 0) {
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
            GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, e);
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
            case "iphone 12":
            case "iphone 12 pro":
                statusBarHeight = 132;
                density = 3;
                break;
            case "iphone xr":
            case "iphone 11":
                statusBarHeight = 88;
                density = 2;
                break;
            case "iphone 12 pro max":
                statusBarHeight = 141;
                density = 3;
                break;
            case "iphone 12 mini":
                statusBarHeight = 150;
                density = 3;
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
                    WebElement lastElement = list.get(list.size() - 1);
                    scrollableOffset = lastElement.getLocation().getY() + lastElement.getSize().getHeight();
                }
                break;
            case "XCUIElementTypeScrollView":
                list = element.findElements(MobileBy.xpath("//XCUIElementTypeScrollView[1]/*"));
                if (!list.isEmpty()) {
                    WebElement firstElement = list.get(0);
                    scrollableOffset = firstElement.getLocation().getY() + firstElement.getSize().getHeight();
                }
                break;
            case "XCUIElementTypeCollectionView":
                try {
                    triggerHelperButton();

                    WebElement contentInfo = driver.findElement(MobileBy.name("applitools_content_size_label"));
                    try {
                        scrollableOffset = Integer.parseInt(contentInfo.getText().split(",")[1].trim().replace("}", ""));
                        logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK, "Scrollable offset from Helper library: " + scrollableOffset);
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
                        logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK, "Can not parse applitools_content_size_label value");
                    }
                } catch (NoSuchElementException ignored) {
                    logger.log(TraceLevel.Debug, eyesDriver.getTestId(), Stage.CHECK, "Helper library is not provided");
                }
                break;
        }
        return scrollableOffset;
    }

    public void triggerHelperButton() {
        WebElement trigger = driver.findElement(MobileBy.name("applitools_grab_scrollable_data_button"));

        try {
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence scrollAction = new Sequence(finger, 0);
            scrollAction.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), trigger.getLocation().x, trigger.getLocation().y));
            scrollAction.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            scrollAction.addAction(finger.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(), trigger.getLocation().x, trigger.getLocation().y));
            scrollAction.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(Collections.singleton(scrollAction));

        } catch (UnsupportedCommandException e) {
            logger.log(TraceLevel.Warn, eyesDriver.getTestId(), Stage.CHECK, "Using legacy scroll actions");
            TouchAction triggerAction = new TouchAction((PerformsTouchActions) driver);
            triggerAction.tap(new PointOption().withCoordinates(trigger.getLocation().x, trigger.getLocation().y)).waitAction(new WaitOptions().withDuration(Duration.ofMillis(1000)));
            triggerAction.release();
            ((PerformsTouchActions) driver).performTouchAction(triggerAction);
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void cleanupCachedData() {
        super.cleanupCachedData();
        this.firstVisibleChild = null;
    }

    public WebElement getFirstVisibleChild() {
        return this.firstVisibleChild;
    }

    @Override
    public void setScrollRootElement(WebElement scrollRootElement) {
        super.setScrollRootElement(scrollRootElement);
        this.cachedScrollableView = scrollRootElement;
    }
}
