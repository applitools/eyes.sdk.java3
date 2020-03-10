package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.BrowserNames;
import com.applitools.eyes.Logger;
import com.applitools.eyes.OSNames;
import com.applitools.eyes.UserAgent;
import com.applitools.eyes.selenium.SeleniumEyes;

public class RegionPositionCompensationFactory {

    public static RegionPositionCompensation getRegionPositionCompensation(UserAgent userAgent, SeleniumEyes eyes, Logger logger) {
        if (userAgent != null) {
            if (userAgent.getBrowser().equals(BrowserNames.FIREFOX)) {
                try {
                    if (Integer.parseInt(userAgent.getBrowserMajorVersion()) >= 48) {
                        return new FirefoxRegionPositionCompensation(eyes, userAgent, logger);
                    }
                } catch (NumberFormatException e) {
                    return new NullRegionPositionCompensation();
                }
            } else if (userAgent.getBrowser().equals(BrowserNames.SAFARI) && userAgent.getOS().equals(OSNames.MAC_OS_X)) {
                return new SafariRegionPositionCompensation();
            }
            else if (userAgent.getBrowser().equals(BrowserNames.IE))
            {
                return new InternetExplorerRegionPositionCompensation();
            }
        }
        return new NullRegionPositionCompensation();
    }
}
