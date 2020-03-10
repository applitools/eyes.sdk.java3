package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.BrowserNames;
import com.applitools.eyes.IEyesJsExecutor;
import com.applitools.eyes.Logger;
import com.applitools.eyes.UserAgent;
import org.openqa.selenium.WebElement;

public class ScrollPositionProviderFactory {

    public static ScrollPositionProvider getScrollPositionProvider(String uaString,
                                                                   Logger logger,
                                                                   IEyesJsExecutor executor,
                                                                   WebElement scrollRootElement) {
        UserAgent userAgent = UserAgent.parseUserAgentString(uaString, true);
        return getScrollPositionProvider(userAgent, logger, executor, scrollRootElement);
    }

    public static ScrollPositionProvider getScrollPositionProvider(UserAgent userAgent,
                                                                   Logger logger,
                                                                   IEyesJsExecutor executor,
                                                                   WebElement scrollRootElement) {
        if (userAgent != null) {
            if (userAgent.getBrowser().equals(BrowserNames.EDGE)) {
                return new EdgeBrowserScrollPositionProvider(logger, executor, scrollRootElement);
            }
        }
        return new ScrollPositionProvider(logger, executor, scrollRootElement);
    }
}
