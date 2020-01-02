package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.*;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.SeleniumEyes;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.positioning.ScrollPositionProviderFactory;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class SafariScreenshotImageProvider extends MobileScreenshotImageProvider {

    private static Map<RectangleSize, Rectangle[]> devicesRegions = null;

    public SafariScreenshotImageProvider(SeleniumEyes eyes, Logger logger, TakesScreenshot tsInstance, UserAgent userAgent) {
        super(eyes, logger, tsInstance, userAgent);
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

            RectangleSize imageSize = new RectangleSize(imageWidth, imageHeight);

            if (devicesRegions.containsKey(imageSize)) {

                Rectangle[] resolutions = devicesRegions.get(imageSize);
                float widthRatio = image.getWidth() / (float) originalViewportSize.getWidth();
                float height = widthRatio * originalViewportSize.getHeight();
                if (Math.abs(height - image.getHeight()) > 1.5) {
                    Rectangle bestMatchingRect = resolutions[0];
                    float bestHeightDiff = Math.abs(bestMatchingRect.height - height);
                    for (int i = 1; i < resolutions.length; ++i) {
                        Rectangle rect = resolutions[i];
                        float heightDiff = Math.abs(rect.height - height);
                        if (heightDiff < bestHeightDiff) {
                            bestHeightDiff = heightDiff;
                            bestMatchingRect = rect;
                        }
                    }
                    logger.verbose("closest crop rect found: " + bestMatchingRect);
                    image = ImageUtils.cropImage(logger, image, new Region(bestMatchingRect.x, bestMatchingRect.y, bestMatchingRect.width, bestMatchingRect.height));
                } else {
                    logger.verbose("no crop needed. must be using chrome emulator.");
                }
            }
        } else {
            Boolean forceFullPageScreenshot = eyes.getConfigGetter().getForceFullPageScreenshot();
            if (forceFullPageScreenshot != null && !forceFullPageScreenshot) {

                Location loc;
                FrameChain currentFrameChain = ((EyesWebDriver) eyes.getDriver()).getFrameChain();

                if (currentFrameChain.size() == 0) {
                    WebElement scrollRootElement = eyes.getCurrentFrameScrollRootElement();
                    PositionProvider positionProvider = ScrollPositionProviderFactory.getScrollPositionProvider(userAgent, logger, jsExecutor, scrollRootElement);
                    loc = positionProvider.getCurrentPosition();
                } else {
                    loc = currentFrameChain.getDefaultContentScrollPosition();
                }

                loc = loc.scale(scaleRatio);

                image = ImageUtils.cropImage(logger, image, new Region(loc, viewportSize));
            }
        }

        return image;
    }

    private void initDeviceRegionsTable() {

        devicesRegions = new HashMap<>();
        devicesRegions.put(new RectangleSize(1536, 2048), new Rectangle[]{new Rectangle(0, 140, 1536, 1908), new Rectangle(0, 205, 1536, 1843), new Rectangle(0, 128, 1536, 1920), new Rectangle(0, 194, 1536, 1854)});
        devicesRegions.put(new RectangleSize(2048, 1536), new Rectangle[]{new Rectangle(0, 140, 2048, 1396), new Rectangle(0, 205, 2048, 1331), new Rectangle(0, 128, 2048, 1408), new Rectangle(0, 194, 2048, 1342)});
        devicesRegions.put(new RectangleSize(828, 1792), new Rectangle[]{new Rectangle(0, 188, 828, 1438)});
        devicesRegions.put(new RectangleSize(1792, 828), new Rectangle[]{new Rectangle(88, 100, 1616, 686), new Rectangle(88, 166, 1616, 620)});
        devicesRegions.put(new RectangleSize(1242, 2688), new Rectangle[]{new Rectangle(0, 282, 1242, 2157)});
        devicesRegions.put(new RectangleSize(2688, 1242), new Rectangle[]{new Rectangle(132, 150, 2424, 1029), new Rectangle(132, 249, 2424, 930)});
        devicesRegions.put(new RectangleSize(1125, 2436), new Rectangle[]{new Rectangle(0, 282, 1125, 1905)});
        devicesRegions.put(new RectangleSize(2436, 1125), new Rectangle[]{new Rectangle(132, 150, 2172, 912), new Rectangle(132, 249, 2172, 813)});
        devicesRegions.put(new RectangleSize(1242, 2208), new Rectangle[]{new Rectangle(0, 210, 1242, 1866), new Rectangle(0, 192, 1242, 1884)});
        devicesRegions.put(new RectangleSize(2208, 1242), new Rectangle[]{new Rectangle(0, 150, 2208, 1092), new Rectangle(0, 230, 2208, 1012)});
        devicesRegions.put(new RectangleSize(750, 1334), new Rectangle[]{new Rectangle(0, 140, 750, 1106), new Rectangle(0, 128, 750, 1118)});
        devicesRegions.put(new RectangleSize(1334, 750), new Rectangle[]{new Rectangle(0, 100, 1334, 650), new Rectangle(0, 88, 1334, 662)});
        devicesRegions.put(new RectangleSize(640, 1136), new Rectangle[]{new Rectangle(0, 128, 640, 920)});
        devicesRegions.put(new RectangleSize(1136, 640), new Rectangle[]{new Rectangle(0, 88, 1136, 464)});
        devicesRegions.put(new RectangleSize(2048, 2732), new Rectangle[]{new Rectangle(0, 140, 2048, 2592)});
        devicesRegions.put(new RectangleSize(2732, 2048), new Rectangle[]{new Rectangle(0, 140, 2732, 1908)});
        devicesRegions.put(new RectangleSize(1668, 2224), new Rectangle[]{new Rectangle(0, 140, 1668, 2084)});
        devicesRegions.put(new RectangleSize(2224, 1668), new Rectangle[]{new Rectangle(0, 140, 2224, 1528)});
    }

}
