package com.applitools.eyes.selenium;

import com.applitools.eyes.FileLogger;
import com.applitools.eyes.ServerConnector;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static com.applitools.eyes.selenium.TestDataProvider.*;

public class IOSTest {

    @DataProvider(parallel = true)
    public static Object[][] data() {
//        Object[][] iPhoneXPermutations = TestUtils.generatePermutations(
//                Arrays.asList(new Object[]{"iPhone X Simulator"}), // device
//                Arrays.asList(new Object[]{"portrait", "landscape"}), // orientation
//                Arrays.asList(new Object[]{"11.2"}), // OS Version
//                Arrays.asList(new Object[]{false, true}) // fully
//        );
//
//        Object[][] iPhonePermutations = TestUtils.generatePermutations(
//                Arrays.asList(new Object[]{"iPhone 7 Simulator", "iPhone 6 Plus Simulator"}), // device
//                Arrays.asList(new Object[]{"portrait", "landscape"}), // orientation
//                Arrays.asList(new Object[]{"10.0", "11.0"}), // OS Version
//                Arrays.asList(new Object[]{false, true}) // fully
//        );
//
//        Object[][] iPhone5Permutations = TestUtils.generatePermutations(
//                Arrays.asList(new Object[]{"iPhone 5s Simulator"}), // device
//                Arrays.asList(new Object[]{"portrait", "landscape"}), // orientation
//                Arrays.asList(new Object[]{"10.0"}), // OS Version
//                Arrays.asList(new Object[]{false, true}) // fully
//        );
//
//        Object[][] iPadPermutations = TestUtils.generatePermutations(
//                Arrays.asList(new Object[]{
//                        "iPad Simulator",
//                        "iPad Pro (9.7 inch) Simulator",
//                        "iPad Pro (12.9 inch) Simulator",
//                        "iPad Pro (12.9 inch) (2nd generation) Simulator",
//                        "iPad Pro (10.5 inch) Simulator",
//                        "iPad (5th generation) Simulator",
//                        "iPad Air Simulator",
//                        "iPad Air 2 Simulator"}), // device
//                Arrays.asList(new Object[]{"portrait", "landscape"}), // orientation
//                Arrays.asList(new Object[]{"11.0"}), // OS Version
//                Arrays.asList(new Object[]{false, true}) // fully
//        );
//
//        Object[][] iPadLegacyPermutations = TestUtils.generatePermutations(
//                Arrays.asList(new Object[]{
//                        "iPad Simulator",
//                        "iPad Pro (9.7 inch) Simulator",
//                        "iPad Air Simulator",
//                        "iPad Air 2 Simulator"}), // device
//                Arrays.asList(new Object[]{"portrait", "landscape"}), // orientation
//                Arrays.asList(new Object[]{"10.0"}), // OS Version
//                Arrays.asList(new Object[]{false, true}) // fully
//        );
//
//        ArrayList<Object[]> returnValue = new ArrayList<>();
//        returnValue.addAll(Arrays.asList(iPhoneXPermutations));
//        returnValue.addAll(Arrays.asList(iPhonePermutations));
//        returnValue.addAll(Arrays.asList(iPhone5Permutations));
//        returnValue.addAll(Arrays.asList(iPadPermutations));
//        returnValue.addAll(Arrays.asList(iPadLegacyPermutations));


        //return returnValue.toArray(new Object[0][]);
        //return new Object[][]{{"iPhone 5s Simulator", "landscape", "10.0", true}};
        return new Object[][]{{"iPhone XR Simulator", "portrait", "12.2", true}};
    }

    @Test(dataProvider = "data")
    public void TestIOSSafariCrop(String deviceName, String deviceOrientation, String platformVersion, boolean fully) throws MalformedURLException {
        Eyes eyes = new Eyes();
        eyes.setServerConnector(new ServerConnector());

        eyes.setBatch(TestDataProvider.batchInfo);

        DesiredCapabilities caps = DesiredCapabilities.iphone();

        caps.setCapability("deviceName", deviceName);
        caps.setCapability("deviceOrientation", deviceOrientation);
        caps.setCapability("platformVersion", platformVersion);
        caps.setCapability("platformName", "iOS");
        caps.setCapability("browserName", "Safari");

        caps.setCapability("username",  SAUCE_USERNAME);
        caps.setCapability("accesskey", SAUCE_ACCESS_KEY);

        String testName = String.format("%s %s %s", deviceName, platformVersion, deviceOrientation);
        if (fully) {
            testName += " fully";
        }

        caps.setCapability("name", testName + " (" + eyes.getFullAgentId() + ")");

        WebDriver driver = new RemoteWebDriver(new URL(SAUCE_SELENIUM_URL), caps);
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

        if (!TestUtils.runOnCI) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd HH_mm_ss_SSS");
            String logPath = TestUtils.logsPath + File.separator + "java" + File.separator + String.format("IOSTest %s %s", testName, dateFormat.format(Calendar.getInstance().getTime()));
            String logFilename = logPath + File.separator + "log.log";
            eyes.setLogHandler(new FileLogger(logFilename, false, true));
            eyes.setSaveDebugScreenshots(true);
            eyes.setDebugScreenshotsPath(logPath);
        } else {
            eyes.setLogHandler(new StdoutLogHandler(TestUtils.verboseLogs));
        }

        eyes.setStitchMode(StitchMode.SCROLL);

        eyes.addProperty("Orientation", deviceOrientation);
        eyes.addProperty("Stitched", fully ? "True" : "False");

        try {
            driver.get("https://applitools.github.io/demo/TestPages/PageWithHeader/index.html");
            eyes.open(driver, "Eyes Selenium SDK - iOS Safari Cropping", testName);
            eyes.check("Initial view", Target.region(By.cssSelector("body")).fully(fully));
            eyes.close();
        } finally {
            eyes.abortIfNotClosed();
            driver.quit();
        }
    }
}