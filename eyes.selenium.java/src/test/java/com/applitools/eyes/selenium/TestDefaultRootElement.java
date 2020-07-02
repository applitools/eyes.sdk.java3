package com.applitools.eyes.selenium;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

public class TestDefaultRootElement extends ReportingTestSuite {

    public TestDefaultRootElement() {
        super.setGroupName("selenium");
    }

    @Test
    public void testCheckDefaultElementBiggerBody() {
        EyesRunner runner =  new ClassicRunner();
        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(new StdoutLogHandler());
        ChromeDriver driver = SeleniumUtils.createChromeDriver();
        try {
            eyes.open(driver, "Applitools Eyes SDK", "Check Element Bigger Body");
            driver.get("https://applitools.github.io/demo/TestPages/TestBigBody/");
            driver.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            WebElement select = driver.findElement(By.cssSelector("html > body > div > select"));
            eyes.check(Target.region(select).fully());
            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortAsync();
            runner.getAllTestResults();
        }
    }
}
