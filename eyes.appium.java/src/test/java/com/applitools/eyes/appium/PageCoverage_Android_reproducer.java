package com.applitools.eyes.appium;

import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.appium.Target;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.net.MalformedURLException;
import java.net.URL;

public class PageCoverage_Android_reproducer {
  AndroidDriver driver;
  Eyes eyes;
  public static final String testName = "page coverage tests";
  public static final String appName = "Android java reproducer";
  private static final String SAUCE_SERVER_URL = "https://ondemand.saucelabs.com:443/wd/hub";
  private static String apiKey = "knscFXvI6Kjc109ifbi4i101dmMfFtNmnGTbXtnFThyFVWk110";
  private static String serverUrl = "https://testeyes.applitools.com/";
  @BeforeMethod(alwaysRun = true)
  public void before() throws MalformedURLException {
    eyes = new Eyes();
    eyes.setMatchTimeout(0);
    eyes.setApiKey(apiKey);
    eyes.setServerUrl(serverUrl);
    eyes.setBranchName("master");
    eyes.setParentBranchName("master");
    eyes.setSaveNewTests(false);
    eyes.setForceFullPageScreenshot(false);
    DesiredCapabilities caps = new DesiredCapabilities();
    caps.setCapability("browserName", "");
    caps.setCapability("platformName", "Android");
    caps.setCapability("appium:platformVersion", "10.0");
    caps.setCapability("appium:deviceName", "Google Pixel 3a XL GoogleAPI Emulator");
    caps.setCapability("appium:newCommandTimeout", 600);
    caps.setCapability("appium:app", "https://applitools.jfrog.io/artifactory/Examples/eyes-android-hello-world.apk");
    MutableCapabilities sauceOpts = new MutableCapabilities();
    sauceOpts.setCapability("username", System.getenv("SAUCE_USERNAME"));
    sauceOpts.setCapability("accessKey", System.getenv("SAUCE_ACCESS_KEY"));
    sauceOpts.setCapability("appiumVersion", "1.20.2");
    sauceOpts.setCapability("name", "Android PageCoverage");
    caps.setCapability("sauce:options", sauceOpts);
    driver = new AndroidDriver(new URL(SAUCE_SERVER_URL), caps);
    System.out.println(getClass().getName());
  }

  @AfterMethod(alwaysRun = true)
  public void after(){
    if (driver != null) driver.quit();
    eyes.abort();
  }

  @Test
  public void Test() {
    eyes.open(driver, appName, testName);
    eyes.check(Target.window().pageId("Test coverage"));
    eyes.close(false);
  }
}
