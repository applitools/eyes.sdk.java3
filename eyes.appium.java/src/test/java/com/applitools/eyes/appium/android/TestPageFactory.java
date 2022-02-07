package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import com.applitools.eyes.selenium.EyesDriverUtils;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestPageFactory extends AndroidTestSetup {

    public static class MainActivity {
        // The element is now looked up using the name attribute
        @FindBy(id = "btn_recycler_view")
        private WebElement recyclerViewButton;
    }

    @Test
    public void testPageFactory() {
        MainActivity page = PageFactory.initElements(driver, MainActivity.class);
        Assert.assertFalse(page.recyclerViewButton instanceof RemoteWebElement);
        WebElement wrappedWebElement = EyesDriverUtils.getWrappedWebElement(page.recyclerViewButton);
        Assert.assertTrue(wrappedWebElement instanceof RemoteWebElement);

        eyes.open(driver, getApplicationName(), "Check element page factory");
        eyes.check(Target.region(page.recyclerViewButton));
        eyes.check(Target.window().ignore(page.recyclerViewButton));
        eyes.close();
    }
}
