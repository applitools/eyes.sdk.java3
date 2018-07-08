package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.Logger;
import com.applitools.eyes.OSNames;
import com.applitools.eyes.Region;
import com.applitools.eyes.UserAgent;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;

public class FirefoxRegionPositionCompensation implements RegionPositionCompensation {

    private final Eyes eyes;
    private final Logger logger;
    private final UserAgent userAgent;

    public FirefoxRegionPositionCompensation(Eyes eyes, UserAgent userAgent, Logger logger) {
        this.eyes = eyes;
        this.logger = logger;
        this.userAgent = userAgent;
    }

    @Override
    public Region compensateRegionPosition(Region region, double pixelRatio) {
        logger.verbose(userAgent.toString());
        logger.verbose("pixel ratio: " + pixelRatio);

        if (userAgent.getOS().equalsIgnoreCase(OSNames.Windows) &&
                Integer.parseInt(userAgent.getOSMajorVersion()) <= 7) {
            logger.verbose("compensating by " + pixelRatio + " pixels");
            return region.offset(0, (int) pixelRatio);
        }

        if (pixelRatio == 1.0) {
            return region;
        }

        EyesWebDriver eyesWebDriver = (EyesWebDriver) eyes.getDriver();
        FrameChain frameChain = eyesWebDriver.getFrameChain();
        logger.verbose("frameChain.size(): " + frameChain.size());
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
