package com.applitools.eyes.selenium;

import com.applitools.eyes.AutProxySettings;
import com.applitools.eyes.EyesException;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.universal.dto.DebugEyesDto;
import com.applitools.eyes.selenium.universal.dto.DebugHistoryDto;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

public class TestProxy extends ReportingTestSuite {

    private WebDriver driver;
    private ProxySettings proxySettings;
    private AutProxySettings autProxySettings;

    @BeforeTest
    public void setup() {
        String chromeDriverPath = System.getenv("CHROME_DRIVER_PATH");
        if(chromeDriverPath == null) throw new EyesException("CHROME_DRIVER_PATH missing");
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        driver = SeleniumUtils.createChromeDriver(new ChromeOptions().setHeadless(true));
        proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        autProxySettings = new AutProxySettings(proxySettings);
    }

    @AfterTest
    public void teardown() {
        if (driver != null)
            driver.quit();
    }

// this test is passing when running it alone.
// if the test runs in the suite - the universal will start without debug mode and
// rest of the tests would fail.
//    @Test
//    public void shouldBeNullWhenDebugIsOff() {
//        VisualGridRunner runner = new VisualGridRunner();
//        runner.getAllTestResults(false);
//        Assert.assertNull(CommandExecutor.getDebugHistory());
//    }

    @Test
    public void testUniversalProxyWithVisualGridRunner() {

        try (MockedStatic<GeneralUtils> utilities = Mockito.mockStatic(GeneralUtils.class, Mockito.CALLS_REAL_METHODS)) {
            utilities.when(() -> GeneralUtils.getEnvString("APPLITOOLS_UNIVERSAL_DEBUG"))
                    .thenReturn("true");

            VisualGridRunner runner = new VisualGridRunner();
            Eyes eyes = new Eyes(runner);

            eyes.setConfiguration(eyes.getConfiguration()
                    .setProxy(proxySettings)
            );

            eyes.open(driver, "ProxyTest", "proxyTestVisualGridRunner");
            driver.get("https://applitools.com");

            eyes.check(Target.window().fully(false));
            eyes.close(false);

            runner.getAllTestResults(false);

            DebugHistoryDto history = CommandExecutor.getDebugHistory();
            DebugEyesDto debugEyes = history.getManagers().get(0).getEyes().get(0);

            Assert.assertNotNull(debugEyes.getConfig());
            Assert.assertEquals(debugEyes.getConfig().getProxy().getUrl(), proxySettings.getUri());
        }
    }

    @Test(dependsOnMethods = {"testUniversalProxyWithVisualGridRunner"})
    public void testUniversalProxyWithClassicRunner() {

        try (MockedStatic<GeneralUtils> utilities = Mockito.mockStatic(GeneralUtils.class, Mockito.CALLS_REAL_METHODS)) {
            utilities.when(() -> GeneralUtils.getEnvString("APPLITOOLS_UNIVERSAL_DEBUG"))
                    .thenReturn("true");

            ClassicRunner runner = new ClassicRunner();
            Eyes eyes = new Eyes(runner);

            eyes.setConfiguration(eyes.getConfiguration()
                    .setProxy(proxySettings)
            );

            eyes.open(driver, "ProxyTest", "proxyTestClassicRunner");
            driver.get("https://applitools.com");

            eyes.check(Target.window().fully(false));
            eyes.close(false);

            runner.getAllTestResults(false);

            DebugHistoryDto history = CommandExecutor.getDebugHistory();
            DebugEyesDto debugEyes = history.getManagers().get(1).getEyes().get(0);

            Assert.assertNotNull(debugEyes.getConfig());
            Assert.assertEquals(debugEyes.getConfig().getProxy().getUrl(), proxySettings.getUri());
        }
    }

    @Test(dependsOnMethods = {"testUniversalProxyWithClassicRunner"})
    public void testUniversalAutProxyWithVisualGridRunner() {

        try (MockedStatic<GeneralUtils> utilities = Mockito.mockStatic(GeneralUtils.class, Mockito.CALLS_REAL_METHODS)) {
            utilities.when(() -> GeneralUtils.getEnvString("APPLITOOLS_UNIVERSAL_DEBUG"))
                    .thenReturn("true");

            VisualGridRunner runner = new VisualGridRunner();
            Eyes eyes = new Eyes(runner);

            eyes.setConfiguration(eyes.getConfiguration()
                    .setAutProxy(autProxySettings)
            );

            eyes.open(driver, "ProxyTest", "autProxyTestVisualGridRunner");
            driver.get("https://applitools.com");

            eyes.check(Target.window().fully(false));
            eyes.close(false);

            runner.getAllTestResults(false);

            DebugHistoryDto history = CommandExecutor.getDebugHistory();
            DebugEyesDto debugEyes = history.getManagers().get(2).getEyes().get(0);

            Assert.assertNotNull(debugEyes.getConfig());
            Assert.assertNull(debugEyes.getConfig().getProxy());
            Assert.assertEquals(debugEyes.getConfig().getAutProxy().getUrl(), autProxySettings.getUri());
        }
    }

    // not in use
    private void startProxyDocker() throws IOException, InterruptedException {
        Process stopDocker = Runtime.getRuntime().exec(new String[]{"bash","-c","docker run -d --name='tinyproxy' -p 8080:8888 dannydirect/tinyproxy:latest ANY"});
        stopDocker.waitFor();
    }

    // not in use
    private void stopAllDockers() throws IOException, InterruptedException {
        Process stopDocker = Runtime.getRuntime().exec(new String[]{"bash","-c","docker stop $(docker ps -a -q)"});
        stopDocker.waitFor();
        Process removeDocker = Runtime.getRuntime().exec(new String[]{"bash","-c","docker rm $(docker ps -a -q)"});
        removeDocker.waitFor();
    }
}

