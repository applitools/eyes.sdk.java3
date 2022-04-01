package com.applitools.eyes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.selenium.fluent.Target;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * @author Kanan
 */
public class CaptchaTest {
  private static final String TEST_NAME = "Applitools test - Captcha test";
  private static final String URL = "https://www.ns.nl/contactvoorkeuren/aanvragennieuwsbrief";
  private static WebDriver driver;

  @Test
  public void testCaptcha() throws IOException {
    Eyes eyes = setupEyes();

    ChromeOptions browserOptions = new ChromeOptions();
    browserOptions.setCapability("platformName", "Windows 10");
    browserOptions.setCapability("browserVersion", "latest");
    Map<String, Object> sauceOptions = new HashMap<>();
    sauceOptions.put("screenResolution", "1920x1080");
    sauceOptions.put("name", TEST_NAME + " - Chrome");
    browserOptions.setCapability("sauce:options", sauceOptions);

//        LOGGER.info("Start remote webdriver for saucelabs {}", SAUCE_LABS_URL);
//        driver = new RemoteWebDriver(new URL(SAUCE_LABS_URL), browserOptions);
    driver=new ChromeDriver();

    try {
//            LOGGER.info("Start visual UI testing.");
      driver.get(URL);

      RectangleSize rectangleSize = new RectangleSize(1000, 600);
      driver = eyes.open(driver, "NS.nl - Debug", TEST_NAME, rectangleSize);
//            TestUtil.openUrl(driver, URL);
      eyes.check(null, Target.window().fully().ignore(By.cssSelector(".formfield__captchaImage")));

      WebElement submit = driver.findElement(By.cssSelector(".actionBar__item"));
      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", submit);
//            (new WebDriverWait(driver, 90)).until(TestUtil.getPageLoadCondition());
      eyes.check("Aanmeldformulier validatie panel", Target.window().fully().ignore(By.cssSelector(".formfield__captchaImage")));

      List<WebElement> checkboxElements = driver.findElements(By.cssSelector(".checkbox__input"));
      for (WebElement checkboxElement : checkboxElements) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkboxElement);
      }
      enterAanmeldFormData("test1@ns.nl");
//            driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
      //eyes.setWaitBeforeScreenshots(10000);
//            driver.manage().window().scr;
//            WebElement elem = driver.findElement(By.id("s"));
//            JavascriptExecutor js = (JavascriptExecutor)driver;
////Scroll to top
//            js.executeScript("arguments[0].scrollIntoView()", elem);
      WebElement elem = driver.findElement(By.cssSelector(".formfield__captchaImage"));
      int y = elem.getLocation().y;
      int x = elem.getLocation().x;
      int width = elem.getSize().width;
      int height = elem.getSize().getHeight();

      ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,0)");
      eyes.check("Aanmeldformulier ingevuld", Target.window().fully().ignore(By.cssSelector(".formfield__captchaImage"))); // INCORRECT!
      //eyes.check("Aanmeldformulier ingevuld", Target.window().fully().ignore(new Region(x, y, width, height))); //----------> CORRECT!!!!!

//            LOGGER.info("End visual testing.");
      eyes.close();
    } finally {
      eyes.abortIfNotClosed();
      driver.quit();
    }
  }

  private Eyes setupEyes() {
//        LOGGER.info("Setup Applitools Eyes.");
    ClassicRunner runner = new ClassicRunner();
//        VisualGridRunner runner = new VisualGridRunner(5);
    Eyes eyes = new Eyes(runner);
    eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
//        eyes.setServerUrl(APPLITOOLS_SERVER_URL);

    eyes.setStitchMode(StitchMode.CSS);
//        eyes.setLogHandler(new FileLogger(APPLITOOLS_LOG_FILE, true, true));
    eyes.setHideCaret(true);
    return eyes;
  }

  public void enterAanmeldFormData(String email) {
    String[] formData = {email, "static"};
    entertextFieldFormData(formData);
  }
  private void entertextFieldFormData(String[] formData) {
    List<WebElement> textElements = driver.findElements(By.cssSelector(".textInput.textInput--fullWidth"));
    //driver.findElement(By.className("cookie-notice__btn-accept")).click();
    for (int i = 0; i < textElements.size(); i++) {
      if (formData.length > i) {
        textElements.get(i).sendKeys(formData[i]);
      }
    }
  }
}
