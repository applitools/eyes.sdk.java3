package com.applitools.eyes.selenium;

import com.applitools.connectivity.EyesConnectivityUtils;
import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.config.ConfigurationProvider;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.selenium.capture.EyesWebDriverScreenshot;
import com.applitools.eyes.selenium.capture.TakesScreenshotImageProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.universal.server.UniversalSdkNativeLoader;
import com.applitools.eyes.selenium.wrappers.EyesRemoteWebElement;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.model.RenderingInfo;
import com.applitools.utils.GeneralUtils;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TestSeleniumEyes extends ReportingTestSuite {

    private static final String TESTED_PAGE_URL = "https://applitools.github.io/demo/TestPages/FramesTestPage/";

    Configuration configuration = new Configuration();
    ConfigurationProvider configurationProvider = new ConfigurationProvider() {
        @Override
        public Configuration get() {
            return configuration;
        }
    };

    public TestSeleniumEyes() {
        super.setGroupName("selenium");
    }

    @BeforeMethod
    public void beforeEach() {
        configuration = new Configuration();
    }

    @Test
    public void testShouldTakeFullPageScreenshot() {
        SeleniumEyes eyes = new SeleniumEyes(configurationProvider, new ClassicRunner0());

        Assert.assertFalse(eyes.shouldTakeFullPageScreenshot(Target.window()));
        Assert.assertTrue(eyes.shouldTakeFullPageScreenshot(Target.window().fully()));
        Assert.assertTrue(eyes.shouldTakeFullPageScreenshot(Target.window().fully(true)));
        Assert.assertFalse(eyes.shouldTakeFullPageScreenshot(Target.window().fully(false)));

        configuration.setForceFullPageScreenshot(true);
        Assert.assertTrue(eyes.shouldTakeFullPageScreenshot(Target.window()));
        Assert.assertTrue(eyes.shouldTakeFullPageScreenshot(Target.window().fully()));
        Assert.assertTrue(eyes.shouldTakeFullPageScreenshot(Target.window().fully(true)));
        Assert.assertFalse(eyes.shouldTakeFullPageScreenshot(Target.window().fully(false)));

        configuration.setForceFullPageScreenshot(false);
        Assert.assertFalse(eyes.shouldTakeFullPageScreenshot(Target.window()));
        Assert.assertTrue(eyes.shouldTakeFullPageScreenshot(Target.window().fully()));
        Assert.assertTrue(eyes.shouldTakeFullPageScreenshot(Target.window().fully(true)));
        Assert.assertFalse(eyes.shouldTakeFullPageScreenshot(Target.window().fully(false)));
    }

    @Test
    public void testDebugScreenshot() {
        SeleniumEyes eyes = new SeleniumEyes(configurationProvider, new ClassicRunner0());

        final AtomicBoolean wasSavedDebugScreenshot = new AtomicBoolean();
        wasSavedDebugScreenshot.set(false);
        eyes.setDebugScreenshotProvider(new DebugScreenshotsProvider() {
            @Override
            public void save(BufferedImage image, String suffix) {
                wasSavedDebugScreenshot.set(true);
            }
        });

        WebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get(TESTED_PAGE_URL);
        try {
            eyes.open(driver, "Applitools Eyes SDK", "Test Debug Screenshot", new RectangleSize(800, 800));
            eyes.checkWindow();
            Assert.assertTrue(wasSavedDebugScreenshot.get());
            wasSavedDebugScreenshot.set(false);
            eyes.check(Target.window().fully());
            Assert.assertTrue(wasSavedDebugScreenshot.get());
            eyes.close(true);
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
        }
    }

    @Test
    public void testChangeTabs() {
        SeleniumEyes eyes = new SeleniumEyes(configurationProvider, new ClassicRunner0());
        WebDriver driver = SeleniumUtils.createChromeDriver();
        try {
            driver = eyes.open(driver, "Applitools Eyes SDK", "Test Change Tabs", new RectangleSize(800, 800));
            driver.get("https://the-internet.herokuapp.com/windows");
            driver.findElement(By.xpath("//a[contains(@href, 'new')]")).click();
            List tabs = Arrays.asList(driver.getWindowHandles().toArray());
            driver.switchTo().window((String) tabs.get(1));
            driver.close();
            driver.switchTo().window((String) tabs.get(0));
            eyes.checkWindow();
            eyes.close(true);
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
        }
    }

    @Test
    public void testScreenshotTooBig() {
        ClassicRunner0 runner = new ClassicRunner0();
        SeleniumEyes eyes = new SeleniumEyes(configurationProvider, runner);

        final BufferedImage[] screenshots = new BufferedImage[1];
        screenshots[0] = null;
        eyes.setSaveDebugScreenshots(true);
        eyes.setDebugScreenshotProvider(new DebugScreenshotsProvider() {
            @Override
            public void save(BufferedImage image, String suffix) {
                if (suffix.equals("stitched")) {
                    screenshots[0] = image;
                }
            }
        });

        RenderingInfo renderingInfo = eyes.getRenderingInfo();
        WebDriver driver = SeleniumUtils.createChromeDriver();
        try {
            driver = eyes.open(driver, "Applitools Eyes SDK", "Test Screenshot Too Big", new RectangleSize(800, 800));
            driver.get(TESTED_PAGE_URL);
            driver.findElement(By.id("stretched")).click();
            WebElement frame = driver.findElement(By.cssSelector("#modal2 iframe"));
            driver = driver.switchTo().frame(frame);
            WebElement element = driver.findElement(By.tagName("html"));
            eyes.check(Target.region(element).fully());
            eyes.close(false);
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
        }

        Assert.assertEquals(screenshots[0].getHeight(), renderingInfo.getMaxImageHeight());
        TestResultsSummary summary = runner.getAllTestResults(false);
        TestResults results = summary.getAllResults()[0].getTestResults();
        results.delete();
    }

    @Test
    public void testConfiguration() {
        Eyes eyes = new Eyes();
        eyes.setApiKey("apikey");
        Assert.assertEquals(eyes.getConfiguration().getApiKey(), "apikey");

        ProxySettings proxySettings = new ProxySettings("http://localhost:8888");
        eyes.setProxy(proxySettings);
        Assert.assertEquals(eyes.getConfiguration().getProxy(), proxySettings);

        eyes.setBranchName("master");
        Assert.assertEquals(eyes.getConfiguration().getBranchName(), "master");
        eyes.setParentBranchName("develop");
        Assert.assertEquals(eyes.getConfiguration().getParentBranchName(), "develop");

        String url = "http://localhost";
        eyes.setServerUrl(url);
        Assert.assertEquals(eyes.getConfiguration().getServerUrl().toString(), url);

        eyes.setAppName("app");
        Assert.assertEquals(eyes.getConfiguration().getAppName(), "app");
    }

    @Test
    public void testSetLogger() {
        Eyes eyes = new Eyes(new ClassicRunner());
        LogHandler logHandler = new StdoutLogHandler(true);
        eyes.setLogHandler(logHandler);
        ServerConnector serverConnector = eyes.getServerConnector();
        Assert.assertEquals(serverConnector.getLogger().getLogHandler(), logHandler);
        Assert.assertEquals(EyesConnectivityUtils.getClient(serverConnector).getLogger().getLogHandler(), logHandler);
    }

    @Test
    public void testGetViewportSizeBeforeOpen() {
        Eyes eyes = new Eyes(new ClassicRunner());
        Assert.assertNull(eyes.getViewportSize());
    }

    @Test
    public void testGetBoundingClientRect() {
        EyesSeleniumDriver driver = mock(EyesSeleniumDriver.class);
        RemoteWebElement element = mock(RemoteWebElement.class);
        EyesRemoteWebElement remoteWebElement = new EyesRemoteWebElement(new Logger(), driver, element);

        Rectangle resultRect = new Rectangle(0, 0, 500, 400);

        String result = "0;0;400;500;undefined;undefined";
        when(driver.executeScript(ArgumentMatchers.<String>any(), ArgumentMatchers.<Object[]>any())).thenReturn(result);
        Assert.assertEquals(remoteWebElement.getBoundingClientRect(), resultRect);

        result = "0;0;undefined;undefined;400;500";
        when(driver.executeScript(ArgumentMatchers.<String>any(), ArgumentMatchers.<Object[]>any())).thenReturn(result);
        Assert.assertEquals(remoteWebElement.getBoundingClientRect(), resultRect);
    }

    @Test
    public void testGetRootElementNoBody() {
        EyesSeleniumDriver driver = mock(EyesSeleniumDriver.class);
        when(driver.findElement(By.tagName("html"))).thenReturn(mock(RemoteWebElement.class));
        when(driver.findElement(By.tagName("body"))).thenThrow(new NoSuchElementException(""));
        EyesSeleniumUtils.getDefaultRootElement(new Logger(), driver);
    }

    @Test
    public void testSetEmptyViewportSize() {
        ChromeDriver driver = SeleniumUtils.createChromeDriver();
        try {
            EyesDriverUtils.setViewportSize(new Logger(), driver, new RectangleSize(0, 0));
        } finally {
            driver.quit();
        }
    }

    @Test
    public void testGetEmptyFrameContentSize() {
        Logger logger = new Logger();
        ChromeDriver driver = SeleniumUtils.createChromeDriver();
        driver.get(TESTED_PAGE_URL);
        try {
            SeleniumEyes seleniumEyes = new SeleniumEyes(configurationProvider, new ClassicRunner0());
            EyesSeleniumDriver seleniumDriver = (EyesSeleniumDriver) seleniumEyes.open(
                    driver, "Applitools Eyes SDK", "testGetEmptyFrameContentSize", new RectangleSize(800, 800));
            TakesScreenshotImageProvider imageProvider = new TakesScreenshotImageProvider(logger, seleniumDriver);
            EyesSeleniumDriver mockedDriver = spy(seleniumDriver);
            doReturn("0;0").when(mockedDriver).executeScript(eq(EyesRemoteWebElement.JS_GET_CLIENT_SIZE), any());
            EyesWebDriverScreenshot screenshot = new EyesWebDriverScreenshot(logger, mockedDriver, imageProvider.getImage());
            seleniumEyes.close();
            Assert.assertEquals(screenshot.getFrameWindow(), new Region(0, 0, 800, 800));
        } finally {
            driver.quit();
        }
    }

    @Test
    public void testUniversalServerPath() {
        try (MockedStatic<GeneralUtils> utilities = mockStatic(GeneralUtils.class)) {
            utilities.when(() -> GeneralUtils.getEnvString("APPLITOOLS_UNIVERSAL_PATH"))
                    .thenReturn("path");
            utilities.when(() -> GeneralUtils.getPropertyString("os.name")).thenReturn("macos");
            utilities.when(GeneralUtils::getServerUrl).thenReturn(URI.create("http://locahost:8080"));
            Eyes eyes = new Eyes();
            eyes.open(new ChromeDriver(), "app name", "test name");
            assertEquals("21077", UniversalSdkNativeLoader.getPort());
            assertEquals("path", GeneralUtils.getEnvString("APPLITOOLS_UNIVERSAL_PATH"));
        }
    }
}
