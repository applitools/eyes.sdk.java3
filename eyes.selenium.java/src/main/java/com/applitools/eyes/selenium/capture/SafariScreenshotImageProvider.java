package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.*;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.SeleniumJavaScriptExecutor;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.positioning.ScrollPositionProvider;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TakesScreenshot;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class SafariScreenshotImageProvider implements ImageProvider {

    private final Eyes eyes;
    private final Logger logger;
    private final TakesScreenshot tsInstance;
    private final IEyesJsExecutor jsExecutor;
    private final UserAgent userAgent;

    private static Map<DeviceData, Region> devicesRegions = null;

    public SafariScreenshotImageProvider(Eyes eyes, Logger logger, TakesScreenshot tsInstance, UserAgent userAgent) {
        this.eyes = eyes;
        this.logger = logger;
        this.tsInstance = tsInstance;
        this.jsExecutor = new SeleniumJavaScriptExecutor((EyesWebDriver) eyes.getDriver());
        this.userAgent = userAgent;
    }

    @Override
    public BufferedImage getImage() {
        logger.verbose("Getting screenshot as base64...");
        String screenshot64 = tsInstance.getScreenshotAs(OutputType.BASE64);
        logger.verbose("Done getting base64! Creating BufferedImage...");
        BufferedImage image = ImageUtils.imageFromBase64(screenshot64);

        eyes.getDebugScreenshotsProvider().save(image, "SAFARI");

        if (eyes.getIsCutProviderExplicitlySet()) {
            return image;
        }

        double scaleRatio = eyes.getDevicePixelRatio();
        RectangleSize originalViewportSize = eyes.getViewportSize();
        RectangleSize viewportSize = originalViewportSize.scale(scaleRatio);

        logger.verbose("logical viewport size: " + originalViewportSize);

        if (userAgent.getOS().equals(OSNames.IOS)) {
            if (devicesRegions == null) {
                initDeviceRegionsTable();
            }

            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();

            logger.verbose("physical device pixel size: " + imageWidth + " x " + imageHeight);

            DeviceData deviceData = new DeviceData(
                    imageWidth, imageHeight,
                    originalViewportSize.getWidth(), originalViewportSize.getHeight(),
                    Integer.parseInt(userAgent.getBrowserMajorVersion()));

            if (devicesRegions.containsKey(deviceData)) {
                logger.verbose("device data found in hash table");
                Region crop = devicesRegions.get(deviceData);
                image = ImageUtils.cropImage(image, crop);
            } else {
                logger.verbose("device not found in list. returning original image.");
            }
        } else if (!eyes.getForceFullPageScreenshot()) {

            Location loc;
            FrameChain currentFrameChain = ((EyesWebDriver) eyes.getDriver()).getFrameChain();

            if (currentFrameChain.size() == 0) {
                PositionProvider positionProvider = new ScrollPositionProvider(logger, jsExecutor);
                loc = positionProvider.getCurrentPosition();
            } else {
                loc = currentFrameChain.getDefaultContentScrollPosition();
            }

            loc = loc.scale(scaleRatio);

            image = ImageUtils.cropImage(image, new Region(loc, viewportSize));
        }

        return image;
    }

    private void initDeviceRegionsTable() {
        devicesRegions = new HashMap<>();

        devicesRegions.put(new DeviceData(1125, 2436, 375, 635, 11), new Region(0, 283, 1125, 1903));
        devicesRegions.put(new DeviceData(2436, 1125, 724, 325, 11), new Region(132, 151, 2436, 930));

        devicesRegions.put(new DeviceData(1242, 2208, 414, 622, 11), new Region(0, 211, 1242, 1863));
        devicesRegions.put(new DeviceData(2208, 1242, 736, 364, 11), new Region(0, 151, 2208, 1090));

        devicesRegions.put(new DeviceData(1242, 2208, 414, 628, 10), new Region(0, 193, 1242, 1882));
        devicesRegions.put(new DeviceData(2208, 1242, 736, 337, 10), new Region(0, 231, 2208, 1010));

        devicesRegions.put(new DeviceData(750, 1334, 375, 553, 11), new Region(0, 141, 750, 1104));
        devicesRegions.put(new DeviceData(1334, 750, 667, 325, 11), new Region(0, 101, 1334, 648));

        devicesRegions.put(new DeviceData(750, 1334, 375, 559, 10), new Region(0, 129, 750, 1116));
        devicesRegions.put(new DeviceData(1334, 750, 667, 331, 10), new Region(0, 89, 1334, 660));

        devicesRegions.put(new DeviceData(640, 1136, 320, 460, 10), new Region(0, 129, 640, 918));
        devicesRegions.put(new DeviceData(1136, 640, 568, 232, 10), new Region(0, 89, 1136, 462));

        devicesRegions.put(new DeviceData(1536, 2048, 768, 954, 11), new Region(0, 141, 1536, 1907));
        devicesRegions.put(new DeviceData(2048, 1536, 1024, 698, 11), new Region(0, 141, 2048, 1395));

        devicesRegions.put(new DeviceData(1536, 2048, 768, 922, 11), new Region(0, 206, 1536, 1842));
        devicesRegions.put(new DeviceData(2048, 1536, 1024, 666, 11), new Region(0, 206, 2048, 1330));

        devicesRegions.put(new DeviceData(1536, 2048, 768, 960, 10), new Region(0, 129, 1536, 1919));
        devicesRegions.put(new DeviceData(2048, 1536, 1024, 704, 10), new Region(0, 129, 2048, 1407));

        devicesRegions.put(new DeviceData(1536, 2048, 768, 928, 10), new Region(0, 194, 1536, 1854));
        devicesRegions.put(new DeviceData(2048, 1536, 1024, 672, 10), new Region(0, 194, 2048, 1342));

        devicesRegions.put(new DeviceData(2048, 2732, 1024, 1296, 11), new Region(0, 141, 2048, 2591));
        devicesRegions.put(new DeviceData(2732, 2048, 1366, 954, 11), new Region(0, 141, 2732, 1907));

        devicesRegions.put(new DeviceData(1668, 2224, 834, 1042, 11), new Region(0, 141, 1668, 2083));
        devicesRegions.put(new DeviceData(2224, 1668, 1112, 764, 11), new Region(0, 141, 2224, 1527));
    }

    private static class DeviceData {
        private int width;
        private int height;
        private int vpWidth;
        private int vpHeight;
        private int majorVersion;

        public DeviceData(int width, int height, int vpWidth, int vpHeight, int majorVersion) {

            this.width = width;
            this.height = height;
            this.vpWidth = vpWidth;
            this.vpHeight = vpHeight;
            this.majorVersion = majorVersion;
        }

        @Override
        public int hashCode() {
            return width * 100000 + height * 1000 + vpWidth * 100 + vpHeight * 10 + majorVersion;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DeviceData)) return false;
            DeviceData other = (DeviceData) obj;
            return this.width == other.width &&
                    this.height == other.height &&
                    this.vpWidth == other.vpWidth &&
                    this.vpHeight == other.vpHeight &&
                    this.majorVersion == other.majorVersion;
        }
    }
}
