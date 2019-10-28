package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.BrowserNames;
import com.applitools.eyes.IEyesJsExecutor;
import com.applitools.eyes.Logger;
import com.applitools.eyes.UserAgent;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.selenium.positioning.CssTranslatePositionProvider;
import com.applitools.eyes.selenium.positioning.EdgeBrowserScrollPositionProvider;
import com.applitools.eyes.selenium.positioning.ScrollPositionProvider;
import com.applitools.utils.ArgumentGuard;
import org.openqa.selenium.WebElement;

public class SeleniumScrollPositionProviderFactory {
    public static PositionProvider getPositionProvider(Logger logger, StitchMode stitchMode, IEyesJsExecutor executor, WebElement scrollRootElement, UserAgent userAgent) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(executor, "executor");

        switch (stitchMode) {
            case CSS:
                return new CssTranslatePositionProvider(logger, executor, scrollRootElement);
            case SCROLL:
                if (userAgent != null && userAgent.getBrowser().equals(BrowserNames.Edge))
                    return new EdgeBrowserScrollPositionProvider(logger, executor, scrollRootElement);
                //else
                return new ScrollPositionProvider(logger, executor, scrollRootElement);
            default:
                return null;
        }
    }

}
