package com.applitools.eyes;

import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class AccessibilityStatusTest {

    @Test
    public void testAccessibilityStatus() {
        String apiKey = System.getenv("APPLITOOLS_API_KEY");
        boolean headless = Boolean.parseBoolean(System.getenv().getOrDefault("HEADLESS", "true"));
        ClassicRunner runner = new ClassicRunner();
        BatchInfo batch = new BatchInfo("Applitools Example: Selenium Java JUnit with the Classic Runner");
        Configuration config = new Configuration();
        config.setAccessibilityValidation(new AccessibilitySettings(AccessibilityLevel.AA, AccessibilityGuidelinesVersion.WCAG_2_1));
        config.setApiKey(apiKey);
        config.setBatch(batch);
        WebDriver driver = new ChromeDriver(new ChromeOptions().setHeadless(headless));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        Eyes eyes = new Eyes(runner);
        eyes.setConfiguration(config);
        eyes.open(driver, "ACME Bank Web App", "name of test", new RectangleSize(1024, 768));

        driver.get("https://demo.applitools.com");
        eyes.check(Target.window().fully().withName("Login page"));

        driver.findElement(By.id("username")).sendKeys("andy");
        driver.findElement(By.id("password")).sendKeys("i<3pandas");
        driver.findElement(By.id("log-in")).click();
        eyes.check(Target.window().fully().withName("Main page").layout());
        driver.quit();

        TestResults chaseResult = eyes.close();
        Assert.assertNotNull(chaseResult.getAccessibilityStatus().getStatus());
        Assert.assertNotNull(chaseResult.getAccessibilityStatus().getLevel());
        Assert.assertNotNull(chaseResult.getAccessibilityStatus().getVersion());
    }

}
