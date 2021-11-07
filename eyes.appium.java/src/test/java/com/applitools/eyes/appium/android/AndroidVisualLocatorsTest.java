package com.applitools.eyes.appium.android;

import com.applitools.eyes.Location;
import com.applitools.eyes.Region;
import com.applitools.eyes.locators.VisualLocator;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AndroidVisualLocatorsTest extends AndroidTestSetup {

    @Test
    public void testAndroidVisualLocators() throws Exception {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        eyes.setForceFullPageScreenshot(false);
        eyes.setMatchTimeout(1000);
        eyes.setSaveDebugScreenshots(false);

        eyes.open(driver, getApplicationName(), "Test Visual Locators");

        eyes.checkWindow("Launch screen test");

        Map<String, List<Region>> locators = eyes.locate(VisualLocator.name("list_view_locator").name("scroll_view_locator"));
        System.out.println("Received locators" + locators);

        List<String> names = new ArrayList<>();
        names.add("list_view_locator");
        names.add("scroll_view_locator");
        locators = eyes.locate(VisualLocator.names(names));
        System.out.println("Received locators" + locators);


        locators = eyes.locate(VisualLocator.name("list_view_locator"));
        List<Region> listViewLocatorRegions = locators.get("list_view_locator");

        if (listViewLocatorRegions != null && !listViewLocatorRegions.isEmpty()) {
            Region listViewLocator = listViewLocatorRegions.get(0);
            Location clickLocation = new Location(listViewLocator.getLeft() + listViewLocator.getWidth() / 2,
                    listViewLocator.getTop() + listViewLocator.getHeight() / 2);

            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence actionPress = new Sequence(finger, 1);
            actionPress.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), clickLocation.getX(), clickLocation.getY()));
            actionPress.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            actionPress.addAction(finger.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), clickLocation.getX(), clickLocation.getY()));
            actionPress.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(Collections.singletonList(actionPress));

            Thread.sleep(3000);

            eyes.checkWindow("ListView screen");
        } else {
            throw new Exception("Locator \"list_view_locator\" was not located...");
        }

        locators = eyes.locate(VisualLocator.name("header_locator"));
        System.out.println("Received locators" + locators);

        eyes.close();
    }
}
