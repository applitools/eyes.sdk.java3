package com.applitools.eyes.driver;

import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.playwright.universal.driver.SpecDriverPlaywright;
import com.applitools.eyes.playwright.universal.driver.dto.DriverInfoDto;
import com.applitools.eyes.playwright.universal.dto.Context;
import com.applitools.eyes.playwright.universal.dto.Driver;
import com.applitools.eyes.playwright.universal.dto.Element;
import com.applitools.eyes.utils.PlaywrightTestSetup;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Frame;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestSpecDriver extends PlaywrightTestSetup {

    private SpecDriverPlaywright specDriver;
    private Refer refer;
    private Driver driver;


    @BeforeClass
    public void setup() {
        refer = new Refer();
        specDriver = new SpecDriverPlaywright(refer);
        page = initDriver(true);

        driver = new Driver();
        driver.setApplitoolsRefId(refer.ref(page, driver.getRoot()));
    }

    @Test
    public void testSpecDriverMainContext() {
        page.navigate("https://applitools.github.io/demo/TestPages/FramesTestPage/");

        String frame1 = "[name=\"frame1\"]";

        ElementHandle elementHandle = page.locator(frame1).elementHandle();

        Element element = new Element(elementHandle);
        element.setApplitoolsRefId(refer.ref(elementHandle, driver));

        Context childContext = specDriver.childContext(driver, element);
        Context mainContext = specDriver.mainContext(childContext);
        Frame mainFrame = (Frame) refer.deref(mainContext);
        Assert.assertEquals(mainFrame, page.mainFrame());

        mainContext = specDriver.mainContext(driver);
        mainFrame = (Frame) refer.deref(mainContext);
        Assert.assertEquals(mainFrame, page.mainFrame());
    }

    @Test
    public void testSpecDriverChildContext() {
        page.navigate("https://applitools.github.io/demo/TestPages/FramesTestPage/");

        String frame1 = "[name=\"frame1\"]";

        ElementHandle elementHandle = page.locator(frame1).elementHandle();

        Element element = new Element(elementHandle);
        element.setApplitoolsRefId(refer.ref(elementHandle, driver));

        Context childContext = specDriver.childContext(driver, element);
        Frame frame = (Frame) refer.deref(childContext);

        Assert.assertEquals(frame, elementHandle.contentFrame());
    }

    @Test
    public void testSpecDriverParentContext() {
        page.navigate("https://applitools.github.io/demo/TestPages/FramesTestPage/");

        String frame1 = "[name=\"frame1\"]";
        String frame1_1 = "[name=\"frame1-1\"]";

        Frame mainFrame = page.locator(frame1).elementHandle().contentFrame();
        Frame innerFrame = mainFrame.locator(frame1_1).elementHandle().contentFrame();

        Context context = new Context();
        context.setApplitoolsRefId(refer.ref(innerFrame, driver));

        Context parentContext = specDriver.parentContext(context);
        Frame parentFrame = (Frame) refer.deref(parentContext);
        Assert.assertEquals(parentFrame, mainFrame);
    }

    @Test
    public void testSpecDriverGetDriverInfo() {
        DriverInfoDto driverInfoDto = new DriverInfoDto();
        DriverInfoDto driverInfo = specDriver.getDriverInfo(driver);
        Assert.assertEquals(driverInfo, driverInfoDto);
    }

    @Test
    public void testSpecDriverGetTitle() {
        page.navigate("https://demo.applitools.com");
        String title = specDriver.getTitle(driver);

        Assert.assertEquals(title, "ACME demo app");
    }

    @Test
    public void testSpecDriverGetUrl() {
        page.navigate("https://demo.applitools.com");
        String url = specDriver.getUrl(driver);

        Assert.assertEquals(url, page.url());
    }

    @Test
    public void testSpecDriverVisit() {
        page.navigate("https://demo.applitools.com");
        String url = "https://applitools.com/";

        specDriver.visit(driver, url);
        String visited = specDriver.getUrl(driver);

        Assert.assertEquals(visited, url);
    }
}
