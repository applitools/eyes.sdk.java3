package com.applitools.eyes.selenium.wrappers;

import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestPageFactory extends ReportingTestSuite {

    public TestPageFactory() {
        super.setGroupName("selenium");
    }

    private WebDriver driver;

    public static class DemoPage {
        // The element is now looked up using the name attribute
        @FindBy(id = "centered")
        private WebElement greenRectangleElement;
    }
    @BeforeMethod
    public void beforeEach() {
        driver = SeleniumUtils.createChromeDriver();
    }

    @Test
    public void testPageFactory() {
        driver.get("https://applitools.github.io/demo/TestPages/FramesTestPage/");
        DemoPage page = PageFactory.initElements(driver, DemoPage.class);
        Assert.assertFalse(page.greenRectangleElement instanceof RemoteWebElement);
        WebElement wrappedWebElement = EyesRemoteWebElement.getWrappedWebElement(page.greenRectangleElement);
        Assert.assertTrue(wrappedWebElement instanceof RemoteWebElement);
    }

    @AfterMethod
    public void afterEach() {
        driver.quit();
    }
}
