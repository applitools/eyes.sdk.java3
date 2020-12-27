package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.*;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.EyesSeleniumUtils;
import com.applitools.eyes.selenium.SeleniumEyes;
import com.applitools.eyes.selenium.SeleniumJavaScriptExecutor;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.positioning.ScrollPositionProviderFactory;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.awt.image.BufferedImage;

public class InternetExplorerScreenshotImageProvider implements ImageProvider {

    private final SeleniumEyes eyes;
    private final Logger logger;
    private final TakesScreenshot tsInstance;
    private final IEyesJsExecutor jsExecutor;
    private final UserAgent userAgent;

    public InternetExplorerScreenshotImageProvider(SeleniumEyes eyes, Logger logger, TakesScreenshot tsInstance, UserAgent userAgent) {
        this.eyes = eyes;
        this.logger = logger;
        this.tsInstance = tsInstance;
        this.jsExecutor = new SeleniumJavaScriptExecutor((EyesSeleniumDriver) eyes.getDriver());
        this.userAgent = userAgent;
    }

    public BufferedImage getImage() {
        Location loc;
        double scaleRatio = eyes.getDevicePixelRatio();

        FrameChain currentFrameChain = ((EyesSeleniumDriver) eyes.getDriver()).getFrameChain();
        PositionProvider positionProvider = null;
        if (currentFrameChain.size() == 0) {
            positionProvider = ScrollPositionProviderFactory.getScrollPositionProvider(userAgent, logger, jsExecutor, EyesSeleniumUtils.getDefaultRootElement(logger, (EyesSeleniumDriver) eyes.getDriver()));
            loc = positionProvider.getCurrentPosition();
        } else {
            loc = currentFrameChain.getDefaultContentScrollPosition();
        }

        Location scaledLoc = loc.scale(scaleRatio);
        String screenshot64 = tsInstance.getScreenshotAs(OutputType.BASE64);
        BufferedImage image = ImageUtils.imageFromBase64(screenshot64);
        RectangleSize originalViewportSize = eyes.getViewportSize();
        RectangleSize viewportSize = originalViewportSize.scale(scaleRatio);

        if (image.getHeight() > viewportSize.getHeight() || image.getWidth() > viewportSize.getWidth()) {
            //Damn IE driver returns full page screenshot even when not asked to!
            eyes.getDebugScreenshotsProvider().save(image, "IE");
            if (!eyes.getIsCutProviderExplicitlySet()) {
                image = ImageUtils.cropImage(image, new Region(scaledLoc, viewportSize));
            }
        }
        if (positionProvider != null) {
            positionProvider.setPosition(loc);
        }
        return image;
    }

}
