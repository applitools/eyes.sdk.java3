package com.applitools.eyes.selenium;

import com.applitools.eyes.IEyesJsExecutor;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Factory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class TestClassicApi extends TestSetup {

    @Factory(dataProvider = "dp", dataProviderClass = TestDataProvider.class)
    public TestClassicApi(Capabilities caps, String mode) {
        super("Eyes Selenium SDK - Classic API", caps, mode);
        testedPageUrl = "https://applitools.github.io/demo/TestPages/FramesTestPage/";
    }

    @Test
    public void TestCheckWindow() {
        getEyes().checkWindow("Window");
    }

    @Test
    public void TestCheckWindowFully() {
        getEyes().checkWindow("Full Window", true);
    }

    @Test
    public void TestCheckWindowViewport() {
        getEyes().checkWindow("Viewport Window", false);
    }

    @Test
    public void TestCheckRegion() {
        getEyes().checkRegion(By.id("overflowing-div"), "Region", false);
    }

    @Test
    public void TestCheckRegion2() {
        getEyes().checkRegion(By.id("overflowing-div-image"), "minions", false);
    }

    @Test
    public void TestCheckFrame() {
        getEyes().checkFrame("frame1", "frame1");
    }

    @Test
    public void TestCheckRegionInFrame() {
        getEyes().checkRegionInFrame("frame1", By.id("inner-frame-div"), "Inner frame div", true);
    }

    @Test
    public void TestCheckInnerFrame() {
        getEyes().setHideScrollbars(false);
        ((JavascriptExecutor) getDriver()).executeScript("document.documentElement.scrollTo(0,350);");
        getDriver().switchTo().defaultContent();
        getDriver().switchTo().frame(getDriver().findElement(By.name("frame1")));
        getEyes().checkFrame("frame1-1", "inner-frame");
        getEyes().checkWindow("window after check frame");
        WebElement innerFrameBody = getDriver().findElement(By.tagName("body"));
        ((IEyesJsExecutor) getDriver()).executeScript("arguments[0].style.background='red';", innerFrameBody);
        getEyes().checkWindow("window after change background color of inner frame");
    }

    @Test
    public void TestCheckWindowAfterScroll() {
        ((JavascriptExecutor) getDriver()).executeScript("document.documentElement.scrollTo(0,350);");
        getEyes().checkWindow("viewport after scroll", false);
    }

    @Test
    public void TestDoubleCheckWindow() {
        getEyes().checkWindow("first");
        getEyes().checkWindow("second");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
