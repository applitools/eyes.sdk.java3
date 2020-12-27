package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.Logger;
import com.applitools.eyes.OSNames;
import com.applitools.eyes.Region;
import com.applitools.eyes.UserAgent;
import com.applitools.eyes.selenium.SeleniumEyes;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;

public class FirefoxRegionPositionCompensation implements RegionPositionCompensation {

    private final SeleniumEyes eyes;
    private final Logger logger;
    private final UserAgent userAgent;

    public FirefoxRegionPositionCompensation(SeleniumEyes eyes, UserAgent userAgent, Logger logger) {
        this.eyes = eyes;
        this.logger = logger;
        this.userAgent = userAgent;
    }

    @Override
    public Region compensateRegionPosition(Region region, double pixelRatio) {
        if (userAgent.getOS().equalsIgnoreCase(OSNames.WINDOWS) &&
                Integer.parseInt(userAgent.getOSMajorVersion()) <= 7) {
            return region.offset(0, (int) pixelRatio);
        }

        if (pixelRatio == 1.0) {
            return region;
        }

        EyesSeleniumDriver eyesSeleniumDriver = (EyesSeleniumDriver) eyes.getDriver();
        FrameChain frameChain = eyesSeleniumDriver.getFrameChain();
        if (frameChain.size() > 0) {
            return region;
        }

        region = region.offset(0, -(int) Math.ceil(pixelRatio / 2));

        if (region.getWidth() <= 0 || region.getHeight() <= 0) {
            return Region.EMPTY;
        }

        return region;
    }
}
