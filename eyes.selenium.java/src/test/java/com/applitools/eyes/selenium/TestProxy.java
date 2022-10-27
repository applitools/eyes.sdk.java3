package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.universal.dto.DebugEyesDto;
import com.applitools.eyes.selenium.universal.dto.DebugHistoryDto;
import com.applitools.eyes.selenium.universal.server.UniversalSdkNativeLoader;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;

public class TestProxy extends ReportingTestSuite {

    private Eyes eyes;
    private WebDriver driver;
    private ProxySettings proxySettings;
    private AutProxySettings autProxySettings;
    private ProxySettings proxySettings1;
    private AutProxySettings autProxySettings1;

    private Method stopServer;
    private Field instance;
    private Field debug;

    @BeforeTest
    public void setup() throws IOException, InterruptedException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        String chromeDriverPath = System.getenv("CHROME_DRIVER_PATH");
        if(chromeDriverPath == null) throw new EyesException("CHROME_DRIVER_PATH missing");
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        driver = SeleniumUtils.createChromeDriver(new ChromeOptions().setHeadless(true));
        proxySettings = new ProxySettings("http://127.0.0.1", 8080, "username", "password");
        autProxySettings = new AutProxySettings(proxySettings);

        proxySettings1 = new ProxySettings("http://127.0.0.1", 8081);
        autProxySettings1 = new AutProxySettings(proxySettings1, new String[]{"http://domain.com"}, AutProxyMode.ALLOW);

        stopServer = UniversalSdkNativeLoader.class.getDeclaredMethod("destroyProcess");
        stopServer.setAccessible(true);

        instance = CommandExecutor.class.getDeclaredField("instance");
        instance.setAccessible(true);

        debug = UniversalSdkNativeLoader.class.getDeclaredField("UNIVERSAL_DEBUG");
        debug.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(debug, debug.getModifiers() & ~Modifier.FINAL);
    }

    @AfterTest
    public void teardown() throws IOException, InterruptedException, IllegalAccessException {
        if (driver != null)
            driver.quit();

        stopAllDockers();

        stopServer.setAccessible(false);
        instance.setAccessible(false);
        debug.set(this, "false");
    }

    @BeforeMethod
    public void beforeEach() {
        try {
            instance.set(this, null);

        } catch (IllegalAccessException e) {
            String errorMessage = GeneralUtils.createErrorMessageFromExceptionWithText(e, "Failed to destroy process / reset CE instance");
            System.out.println(errorMessage);
            throw new EyesException(errorMessage, e);
        }
    }

    @AfterMethod
    public void afterEach() {
        if (eyes != null)
            eyes.abort();

        try {
            stopServer.invoke(UniversalSdkNativeLoader.class);
            instance.set(this, null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            String errorMessage = GeneralUtils.createErrorMessageFromExceptionWithText(e, "Failed to destroy process / reset CE instance");
            System.out.println(errorMessage);
            throw new EyesException(errorMessage, e);
        }
    }

    @Test
    public void debugHistoryShouldBeNullWhenDebugIsOff() {
        new VisualGridRunner();
        Assert.assertNull(CommandExecutor.getDebugHistory());
    }

    @Test
    public void testUniversalProxyWithVisualGridRunner() throws IllegalAccessException, IOException, InterruptedException {
        stopAndStartDockers(8080);
        debug.set(this, "true");

        VisualGridRunner runner = new VisualGridRunner();
        eyes = new Eyes(runner);

        eyes.setConfiguration(eyes.getConfiguration()
                .setProxy(proxySettings)
        );

        eyes.open(driver, "ProxyTest", "proxyTestVisualGridRunner");

        DebugHistoryDto history = CommandExecutor.getDebugHistory();
        DebugEyesDto debugEyes = history.getManagers().get(0).getEyes().get(0);

        Assert.assertNotNull(debugEyes.getConfig());
        Assert.assertEquals(debugEyes.getConfig().getProxy().getUrl(), proxySettings.getUri());
        Assert.assertEquals(debugEyes.getConfig().getProxy().getUsername(), proxySettings.getUsername());
        Assert.assertEquals(debugEyes.getConfig().getProxy().getPassword(), proxySettings.getPassword());
    }

    @Test
    public void testUniversalProxyWithClassicRunner() throws IllegalAccessException, IOException, InterruptedException {
        stopAndStartDockers(8080);
        debug.set(this, "true");

        ClassicRunner runner = new ClassicRunner();
        eyes = new Eyes(runner);

        eyes.setConfiguration(eyes.getConfiguration()
                .setProxy(proxySettings)
        );

        eyes.open(driver, "ProxyTest", "proxyTestClassicRunner");

        DebugHistoryDto history = CommandExecutor.getDebugHistory();
        DebugEyesDto debugEyes = history.getManagers().get(0).getEyes().get(0);

        Assert.assertNotNull(debugEyes.getConfig());
        Assert.assertEquals(debugEyes.getConfig().getProxy().getUrl(), proxySettings.getUri());
        Assert.assertEquals(debugEyes.getConfig().getProxy().getUsername(), proxySettings.getUsername());
        Assert.assertEquals(debugEyes.getConfig().getProxy().getPassword(), proxySettings.getPassword());
    }

    @Test
    public void testUniversalAutProxyWithVisualGridRunner() throws IllegalAccessException {
        debug.set(this, "true");

        VisualGridRunner runner = new VisualGridRunner();
        eyes = new Eyes(runner);

        eyes.setConfiguration(eyes.getConfiguration()
                .setAutProxy(autProxySettings)
        );

        eyes.open(driver, "ProxyTest", "autProxyTestVisualGridRunner");

        DebugHistoryDto history = CommandExecutor.getDebugHistory();
        DebugEyesDto debugEyes = history.getManagers().get(0).getEyes().get(0);

        Assert.assertNotNull(debugEyes.getConfig());
        Assert.assertNull(debugEyes.getConfig().getProxy());
        Assert.assertEquals(debugEyes.getConfig().getAutProxy().getUrl(), autProxySettings.getUri());
        Assert.assertEquals(debugEyes.getConfig().getAutProxy().getUsername(), autProxySettings.getUsername());
        Assert.assertEquals(debugEyes.getConfig().getAutProxy().getPassword(), autProxySettings.getPassword());
    }

    @Test
    public void testUniversalAutProxyWithClassicRunner() throws IllegalAccessException {
        debug.set(this, "true");

        ClassicRunner runner = new ClassicRunner();
        eyes = new Eyes(runner);

        eyes.setConfiguration(eyes.getConfiguration()
                .setAutProxy(autProxySettings1)
        );

        eyes.open(driver, "ProxyTest", "autProxyTestClassicRunner");

        DebugHistoryDto history = CommandExecutor.getDebugHistory();
        DebugEyesDto debugEyes = history.getManagers().get(0).getEyes().get(0);

        Assert.assertNotNull(debugEyes.getConfig());
        Assert.assertNull(debugEyes.getConfig().getProxy());
        Assert.assertEquals(debugEyes.getConfig().getAutProxy().getUrl(), autProxySettings1.getUri());
        Assert.assertEquals(debugEyes.getConfig().getAutProxy().getUsername(), autProxySettings1.getUsername());
        Assert.assertEquals(debugEyes.getConfig().getAutProxy().getPassword(), autProxySettings1.getPassword());
        Assert.assertEquals(debugEyes.getConfig().getAutProxy().getDomains(), autProxySettings1.getDomains());
        Assert.assertEquals(debugEyes.getConfig().getAutProxy().getAutProxyMode(), autProxySettings1.getAutProxyMode().getName());
    }

    @Test
    public void testUniversalProxyAndAutProxyWithVisualGridRunner() throws IllegalAccessException, IOException, InterruptedException {
        stopAndStartDockers(8080);
        debug.set(this, "true");

        VisualGridRunner runner = new VisualGridRunner();
        eyes = new Eyes(runner);

        eyes.setConfiguration(eyes.getConfiguration()
                .setProxy(proxySettings)
                .setAutProxy(autProxySettings)
        );

        eyes.open(driver, "ProxyTest", "proxyAndAutProxyTestVisualGridRunner");

        DebugHistoryDto history = CommandExecutor.getDebugHistory();
        DebugEyesDto debugEyes = history.getManagers().get(0).getEyes().get(0);

        Assert.assertNotNull(debugEyes.getConfig());
        Assert.assertEquals(debugEyes.getConfig().getProxy().getUrl(), proxySettings.getUri());
        Assert.assertEquals(debugEyes.getConfig().getProxy().getUsername(), proxySettings.getUsername());
        Assert.assertEquals(debugEyes.getConfig().getProxy().getPassword(), proxySettings.getPassword());
        Assert.assertEquals(debugEyes.getConfig().getAutProxy().getUrl(), autProxySettings.getUri());
        Assert.assertEquals(debugEyes.getConfig().getAutProxy().getUsername(), autProxySettings.getUsername());
        Assert.assertEquals(debugEyes.getConfig().getAutProxy().getPassword(), autProxySettings.getPassword());
    }

    @Test
    public void testUniversalProxyWithDifferentAutProxyWithClassicRunner() throws IllegalAccessException, IOException, InterruptedException {
        stopAndStartDockers(8081);
        debug.set(this, "true");

        // change aut mode from other tests
        autProxySettings1 = new AutProxySettings(proxySettings1, new String[]{"http://domain.com"}, AutProxyMode.BLOCK);

        VisualGridRunner runner = new VisualGridRunner();
        eyes = new Eyes(runner);

        eyes.setConfiguration(eyes.getConfiguration()
                .setProxy(proxySettings1)
                .setAutProxy(autProxySettings1)
        );

        eyes.open(driver, "ProxyTest", "proxyAndDifferentAutProxyTestClassicRunner");

        DebugHistoryDto history = CommandExecutor.getDebugHistory();
        DebugEyesDto debugEyes = history.getManagers().get(0).getEyes().get(0);

        Assert.assertNotNull(debugEyes.getConfig());
        Assert.assertEquals(debugEyes.getConfig().getProxy().getUrl(), proxySettings1.getUri());
        Assert.assertEquals(debugEyes.getConfig().getProxy().getUsername(), proxySettings1.getUsername());
        Assert.assertEquals(debugEyes.getConfig().getProxy().getPassword(), proxySettings1.getPassword());
        Assert.assertEquals(debugEyes.getConfig().getAutProxy().getUrl(), autProxySettings1.getUri());
        Assert.assertEquals(debugEyes.getConfig().getAutProxy().getUsername(), autProxySettings1.getUsername());
        Assert.assertEquals(debugEyes.getConfig().getAutProxy().getPassword(), autProxySettings1.getPassword());
        Assert.assertEquals(debugEyes.getConfig().getAutProxy().getDomains(), autProxySettings1.getDomains());
        Assert.assertEquals(debugEyes.getConfig().getAutProxy().getAutProxyMode(), autProxySettings1.getAutProxyMode().getName());
    }

    @Test
    public void testUniversalWebDriverProxyWithVisualGridRunner() throws IllegalAccessException, IOException, InterruptedException {
        stopAndStartDockers(8080);
        debug.set(this, "true");

        final String USERNAME = GeneralUtils.getEnvString("SAUCE_USERNAME");
        final String ACCESS_KEY = GeneralUtils.getEnvString("SAUCE_ACCESS_KEY");
        final String SL_URL = "https://"+USERNAME+":" + ACCESS_KEY + "@ondemand.us-west-1.saucelabs.com:443/wd/hub";

        DesiredCapabilities caps = new DesiredCapabilities("chrome", "", Platform.ANY);

        WebDriver driver = new RemoteWebDriver(new URL(SL_URL), caps);
        VisualGridRunner runner = new VisualGridRunner();
        eyes = new Eyes(runner);

        eyes.setConfiguration(eyes.getConfiguration()
                .setWebDriverProxy(new WebDriverProxySettings("http://127.0.0.1:8080"))
        );

        eyes.open(driver, "ProxyTest", "webdriverProxyVisualGridRunner");
        driver.quit();

        DebugHistoryDto history = CommandExecutor.getDebugHistory();
        DebugEyesDto debugEyes = history.getManagers().get(0).getEyes().get(0);

        Assert.assertNotNull(debugEyes.getConfig());
        Assert.assertNull(debugEyes.getConfig().getProxy());
        Assert.assertNull(debugEyes.getConfig().getAutProxy());
        Assert.assertEquals(debugEyes.getDriver().getProxyUrl(), "http://127.0.0.1:8080");
    }

    @Test
    public void testUniversalWebDriverProxyWithClassicRunner() throws IllegalAccessException, IOException, InterruptedException {
        stopAndStartDockers(8081);
        debug.set(this, "true");

        final String USERNAME = GeneralUtils.getEnvString("SAUCE_USERNAME");
        final String ACCESS_KEY = GeneralUtils.getEnvString("SAUCE_ACCESS_KEY");
        final String SL_URL = "https://"+USERNAME+":" + ACCESS_KEY + "@ondemand.us-west-1.saucelabs.com:443/wd/hub";

        DesiredCapabilities caps = new DesiredCapabilities("chrome", "", Platform.ANY);

        WebDriver driver = new RemoteWebDriver(new URL(SL_URL), caps);
        ClassicRunner runner = new ClassicRunner();
        eyes = new Eyes(runner);

        eyes.setConfiguration(eyes.getConfiguration()
                .setWebDriverProxy(new WebDriverProxySettings("http://127.0.0.1:8081"))
        );

        eyes.open(driver, "ProxyTest", "webdriverProxyClassicRunner");
        driver.quit();

        DebugHistoryDto history = CommandExecutor.getDebugHistory();
        DebugEyesDto debugEyes = history.getManagers().get(0).getEyes().get(0);

        Assert.assertNotNull(debugEyes.getConfig());
        Assert.assertNull(debugEyes.getConfig().getProxy());
        Assert.assertNull(debugEyes.getConfig().getAutProxy());
        Assert.assertEquals(debugEyes.getDriver().getProxyUrl(), "http://127.0.0.1:8081");
    }

    private void stopAndStartDockers(int port) throws IOException, InterruptedException {
        stopAllDockers();
        startProxyDocker(port);
    }

    private void startProxyDocker(int port) throws IOException, InterruptedException {
        Process stopDocker = Runtime.getRuntime().exec(new String[]{"bash","-c","docker run -d --name='tinyproxy' -p "+port+":8888 dannydirect/tinyproxy:latest ANY"});
        stopDocker.waitFor();
    }

    private void stopAllDockers() throws IOException, InterruptedException {
        Process stopDocker = Runtime.getRuntime().exec(new String[]{"bash","-c","docker stop $(docker ps -a -q)"});
        stopDocker.waitFor();
        Process removeDocker = Runtime.getRuntime().exec(new String[]{"bash","-c","docker rm $(docker ps -a -q)"});
        removeDocker.waitFor();
    }
}
