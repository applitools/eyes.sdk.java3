package com.applitools.eyes;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.helpers.BasicMDCAdapter;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.FileLogger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.exceptions.NewTestException;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.ScreenOrientation;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;

/**
 * @author Kanan
 */
public class UFG_BrowserStack_test {
  static final String USERNAME = "levvolkovich1";
  static final String AUTOMATE_KEY = "ytgFRKspjgy3oaHs3Gq2";
  static final String URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub-cloud.browserstack.com/wd/hub";

  public static void main(String[] args) throws MalformedURLException {

    // Create a new chrome web driver
    DesiredCapabilities caps = new DesiredCapabilities();
//		caps.setBrowserName("chrome");

    caps.setCapability("browser", "chrome");
    caps.setCapability("browser_version", "93.0");
    caps.setCapability("os", "Windows");
    caps.setCapability("os_version", "10");
    caps.setCapability("build", "browserstack-build-1");


    WebDriver webDriver = new RemoteWebDriver(new URL(URL), caps);

//		WebDriver webDriver = new ChromeDriver();

    VisualGridRunner runner = new VisualGridRunner(8);
    Eyes eyes = new Eyes(runner);

    setUp(eyes);

    try {
      // ⭐️ Note to see visual bugs, run the test using the above URL for the 1st run.
      // but then change the above URL to https://demo.applitools.com/index_v2.html
      // (for the 2nd run)
      ultraFastTest(webDriver, eyes);

    } finally {
      tearDown(webDriver, runner);
    }

  }

  public static void setUp(Eyes eyes) {

    // Initialize eyes Configuration
    Configuration config = new Configuration();

    // You can get your api key from the Applitools dashboard
    config.setApiKey("fcq4rttvnfdjwWt6v99c8cC6FomulWtHwxz3fn104kPf6o110");

    BatchInfo batch= new BatchInfo("UFG + BrowserStack Test");
    batch.setNotifyOnCompletion(true);
    // create a new batch info instance and set it to the configuration
    config.setBatch(batch);

    // Add browsers with different viewports
    config.addBrowser(1600, 1200, BrowserType.CHROME);
    config.addBrowser(800, 600, BrowserType.CHROME);
    config.addBrowser(1600, 1000, BrowserType.CHROME);
    config.addBrowser(1600, 1200, BrowserType.SAFARI);
    config.addBrowser(1600, 1200, BrowserType.IE_11);
    config.addBrowser(1024, 768, BrowserType.EDGE_CHROMIUM);
    config.addBrowser(800, 600, BrowserType.SAFARI);
    // Add mobile emulation devices in Portrait mode
    config.addDeviceEmulation(DeviceName.iPhone_X, ScreenOrientation.PORTRAIT);
    config.addDeviceEmulation(DeviceName.Pixel_2, ScreenOrientation.PORTRAIT);

    // Set the configuration object to eyes
//		eyes.setLogHandler(new StdoutLogHandler(true));
    eyes.setLogHandler(new FileLogger("log.log",true,true));
    eyes.setConfiguration(config);


    System.out.println(eyes.getConfiguration().getAccessibilityValidation());

  }

  public static void ultraFastTest(WebDriver webDriver, Eyes eyes) {

    try {
//			HttpCommandExecutor ce = (HttpCommandExecutor)((RemoteWebDriver) webDriver).getCommandExecutor();
//			System.out.println(ce.getAddressOfRemoteServer());
      // Navigate to the url we want to test
//			webDriver.get("https://demo.applitools.com");
      webDriver.get("http://ncu-newskit-docs.s3-website-eu-west-1.amazonaws.com/ppdsc-1883-grid/storybook/iframe.html?id=newskit-light-video-player-default--story-default-video-player&args=&viewMode=story");

      // Call Open on eyes to initialize a test session
      eyes.open(webDriver, "NewsUK", "Video Redner", new RectangleSize(1024, 748));
      eyes.check(Target.window());
      webDriver.quit();
      eyes.closeAsync();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally  {
      eyes.abortAsync();
    }

  }

  private static void tearDown(WebDriver webDriver, EyesRunner runner) {
    // Close the browser


    // we pass false to this method to suppress the exception that is thrown if we
    // find visual differences
    TestResultsSummary allTestResults = runner.getAllTestResults();
    System.out.println(allTestResults);
  }
}
