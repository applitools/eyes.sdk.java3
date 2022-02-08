package coverage;

import com.applitools.eyes.*;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.appium.Eyes;
import coverage.drivers.appium.NativeDriverBuilder;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import com.applitools.eyes.utils.TestUtils;
import org.testng.Assert;

public class NativeSetup extends GlobalSetup {

    protected Eyes eyes;

    public NativeDriverBuilder buildDriver() {
        return  new NativeDriverBuilder();
    }

    // Eyes configuration

    public void initEyes(boolean isVisualGrid, String stitching, String branchName) {
        eyes = new Eyes();
        eyes.setMatchTimeout(0);
        eyes.setApiKey(apiKey);
        eyes.setBranchName(branchName);
        eyes.setParentBranchName("master");
        eyes.setBatch(batch);
        eyes.setSaveNewTests(false);
        eyes.setForceFullPageScreenshot(false);
        String showLogs = System.getenv("APPLITOOLS_SHOW_LOGS");
        String verbose = System.getenv("APPLITOOLS_SHOW_LOGS_VERBOSE");
        if (showLogs != null && showLogs.equals("true")) {
            eyes.setLogHandler(new StdoutLogHandler((verbose != null && verbose.equals("true"))));
        }
    }

    // Eyes configuration

    public void setParentBranchName(String parentTestBranch) {
        eyes.setParentBranchName(parentTestBranch);
    }

    public void setIsDisabled(Boolean isDisabled) {
        eyes.setIsDisabled(isDisabled);
    }

    public void setBranchName(String name) {
        eyes.setBranchName(name);
    }

    public void setAccessibilitySettings(AccessibilitySettings settings) {
        ImageMatchSettings current = eyes.getDefaultMatchSettings();
        current.setAccessibilitySettings(settings);
        eyes.setDefaultMatchSettings(current);
    }

    // Open

    public void open(WebDriver driver, String appName, String testName) {
        eyesDriver = eyes.open(driver, appName, testName);
    }

    public void open(WebDriver driver, String appName, String testName, RectangleSize rectangleSize) {
        eyesDriver = eyes.open(driver, appName, testName, rectangleSize);
    }

    // Test info

    public SessionResults getTestInfo(TestResults results) {
        SessionResults sessionResults = null;
        try {
            sessionResults = TestUtils.getSessionResults(eyes.getApiKey(), results);
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail("Exception appeared while getting session results");
        }
        return sessionResults;
    }

    public void hover(WebElement element) {
        Actions action = new Actions(driver);
        action.moveToElement(element).build().perform();
    }
}
