package com.applitools.eyes;


import java.util.List;
import java.util.Map;

import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.locators.OcrRegion;
import com.applitools.eyes.locators.VisualLocator;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

public class TestRegionOCR {
  public static void main(String[] args) throws InterruptedException {

    boolean firstTimeRun = true;

    // TODO, change to yours
    String url = "C:\\Users\\AlizadaKE\\Documents\\Classic_Eyes\\eyes.sdk.java3\\eyes.selenium.java\\src\\test\\resources\\canvas.html";

    String testName = "Canvas";

    EyesRunner runner = null;

    runner = new ClassicRunner();

    Eyes eyes = new Eyes(runner);

    ChromeOptions co = new ChromeOptions();
    WebDriver driver = new ChromeDriver(co);

    try {

      Configuration sconf = eyes.getConfiguration();

      sconf.setAppName(testName);

      sconf.setTestName(testName);

      sconf.setApiKey(System.getenv("APPLITOOLS_API_KEY"));

      eyes.setConfiguration(sconf);

      eyes.open(driver);

      System.out.println("Open " + url);

      driver.get(url);

      sleepy(5);

      driver.manage().window().maximize();

      if (firstTimeRun == false) {

        // on first run grab a screenshot and mark the word 'great'
        eyes.check(Target.window().fully());
      } else {

        // on following runs locate the word 'great', take a screenshot of it and also extract text using OcrRegion
        Map<String, List<Region>> locators = eyes.locate(VisualLocator.name("great").first());

        System.out.println(locators);

        //eyes.check(Target.region(locators.get("great").get(0)).withName("great"));

        Region region = new Region(10,10,10,10);
        List<String> textsFound = eyes.extractText(new OcrRegion(region));

        System.out.println(textsFound);
      }
      eyes.closeAsync();

      TestResultsSummary allTestResults = runner.getAllTestResults(false);

      // System.out.println(allTestResults);

    } catch (Exception ex) {
      System.out.println(ex);
    } finally {
      eyes.abortAsync();
      driver.quit();
    }

  }

  private static Region getCashLocation(Eyes eyes) {
    Map<String, List<Region>> locators;
    locators = eyes.locate(VisualLocator.name("Cash").first());
    System.out.println(locators);
    List<Region> listViewLocatorRegions = locators.get("Cash");
    Region cash = listViewLocatorRegions.get(0);
    cash.setTop(cash.getTop() + 500);
    cash.setWidth(cash.getWidth() + 100);
    return cash;
  }

  private static void ClickButton(WebDriver driver, Map<String, List<Region>> locators, String locator)
      throws InterruptedException {
    List<Region> listViewLocatorRegions = locators.get(locator);

    if (listViewLocatorRegions != null && !listViewLocatorRegions.isEmpty()) {
      Region listViewLocator = listViewLocatorRegions.get(0);
      Location clickLocation = new Location(listViewLocator.getLeft() + listViewLocator.getWidth() / 2,
          listViewLocator.getTop() + listViewLocator.getHeight() / 2);

      Actions builder = new Actions(driver);
      builder.moveByOffset(clickLocation.getX(), clickLocation.getY()).click().build().perform();

      sleepy(2);

      builder.moveByOffset(clickLocation.getX() * -1, clickLocation.getY() * -1).build().perform();

    }

  }

  private static void sleepy(int seconds) throws InterruptedException {

    while (seconds-- > 0) {
      Thread.sleep(1000);
    }

  }
}
