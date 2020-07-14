package com.applitools.eyes.appium.locators;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.Logger;
import com.applitools.eyes.Region;
import com.applitools.eyes.appium.EyesAppiumDriver;
import com.applitools.eyes.debug.DebugScreenshotsProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AndroidVisualLocatorProvider extends MobileVisualLocatorProvider {

    public AndroidVisualLocatorProvider(Logger logger, EyesAppiumDriver driver, ServerConnector serverConnector,
                                 double devicePixelRatio, String appName, DebugScreenshotsProvider debugScreenshotsProvider) {
        super(logger, driver, serverConnector, devicePixelRatio, appName, debugScreenshotsProvider);
    }

    @Override
    protected Map<String, List<Region>> adjustVisualLocators(Map<String, List<Region>> map) {
        logger.verbose("Adjust visual locators size and coordinates according to device pixel ratio = " + devicePixelRatio);
        Map<String, List<Region>> result = new HashMap<>();
        for (String key : map.keySet()) {
            List<Region> regions = new ArrayList<>();
            if (map.get(key) != null) {
                for (Region region : map.get(key)) {
                    region.setLeft((int) (region.getLeft() * devicePixelRatio));
                    region.setTop((int) (region.getTop() * devicePixelRatio));
                    region.setHeight((int) (region.getHeight() * devicePixelRatio));
                    region.setWidth((int) (region.getWidth() * devicePixelRatio));
                    regions.add(region);
                }
                result.put(key, regions);
            }
        }
        return result;
    }
}
