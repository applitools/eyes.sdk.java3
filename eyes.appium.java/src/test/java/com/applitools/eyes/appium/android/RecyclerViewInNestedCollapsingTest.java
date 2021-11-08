package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.PointerCancelInteraction;
import com.applitools.eyes.appium.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Collections;

public class RecyclerViewInNestedCollapsingTest extends AndroidTestSetup {

    @Test
    public void testScrollRootElement() throws InterruptedException {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence scrollAction = new Sequence(finger, 1);
        scrollAction.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), 5, 1300));
        scrollAction.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        scrollAction.addAction(finger.createPointerMove(Duration.ofMillis(1500), PointerInput.Origin.viewport(), 5, 200));
        scrollAction.addAction(new PointerCancelInteraction(finger));
        driver.perform(Collections.singletonList(scrollAction));

        eyes.open(driver, getApplicationName(), "Check RecyclerView inside NestedScrollView and Collapsing layout");

        driver.findElement(By.id("btn_recycler_view_nested_collapsing")).click();

        Thread.sleep(1000);

        eyes.check(Target.window().scrollRootElement(By.id("recyclerView")).fully().timeout(0));

        eyes.close();
    }

    @Override
    protected void setAppCapability() {
        capabilities.setCapability("app", "app_androidx");
    }

    @Override
    protected String getApplicationName() {
        return "Java Appium - AndroidX";
    }
}
