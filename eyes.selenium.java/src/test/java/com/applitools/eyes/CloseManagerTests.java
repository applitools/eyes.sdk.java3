package com.applitools.eyes;

import com.applitools.eyes.exceptions.NewTestException;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

public class CloseManagerTests {

    @Test(expectedExceptions = NewTestException.class)
    public void should_ThrowNewTestException_When_CloseManagerWthNew() {
        ClassicRunner classicRunner = new ClassicRunner();
        Eyes eyes = new Eyes(classicRunner);
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
        eyes.setBranchName("master");
        eyes.setParentBranchName("master");
        eyes.setSaveNewTests(false);
        eyes.setHideScrollbars(true);
        eyes.setHideCaret(true);
        ChromeDriver driver = new ChromeDriver();
        try {
            driver.get("https://applitools.github.io/demo/TestPages/FramesTestPage/");
            eyes.open(driver, "newApp", "newTest", new RectangleSize(700, 460));
            eyes.check(Target.frame(By.cssSelector("[name=\"frame1\"]")).fully());
            classicRunner.getAllTestResults(true);
            driver.quit();
        } catch (Exception e) {
            eyes.abortIfNotClosed();
            driver.quit();
        }
    }

}
