package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.BrowserNames;
import com.applitools.eyes.Logger;
import com.applitools.eyes.OSNames;
import com.applitools.eyes.UserAgent;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.selenium.SeleniumEyes;
import com.applitools.eyes.selenium.SeleniumJavaScriptExecutor;
import org.openqa.selenium.TakesScreenshot;

public class ImageProviderFactory {

    public static ImageProvider getImageProvider(UserAgent ua, SeleniumEyes eyes, Logger logger, TakesScreenshot tsInstance) {
        if (ua != null) {
            if (ua.getBrowser().equals(BrowserNames.FIREFOX)) {
                try {
                    if (Integer.parseInt(ua.getBrowserMajorVersion()) >= 48) {
                        return new FirefoxScreenshotImageProvider(eyes, tsInstance);
                    }
                } catch (NumberFormatException e) {
                    return new TakesScreenshotImageProvider(logger, tsInstance);
                }
            } else if (ua.getBrowser().equals(BrowserNames.SAFARI)) {
                return new SafariScreenshotImageProvider(eyes, logger, tsInstance, ua);
            } else if (ua.getBrowser().equals(BrowserNames.IE)) {
                return new InternetExplorerScreenshotImageProvider(eyes, logger, tsInstance, ua);
            } else if (ua.getOS().equals(OSNames.ANDROID)) {
                return new AndroidScreenshotImageProvider(eyes, logger, tsInstance, ua);
            }
        }
        return new TakesScreenshotImageProvider(logger, tsInstance);
    }

    public static ISizeAdjuster getImageSizeAdjuster(UserAgent ua, SeleniumJavaScriptExecutor jsExecutor) {

        if (ua != null && (ua.getOS().equals(OSNames.ANDROID) || ua.getOS().equals(OSNames.IOS))) {
            return new MobileDeviceSizeAdjuster(jsExecutor);
        }
        return NullSizeAdjuster.getInstance();
    }
}
