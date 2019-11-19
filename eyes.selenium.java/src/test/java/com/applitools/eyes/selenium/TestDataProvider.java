package com.applitools.eyes.selenium;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.utils.TestUtils;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariOptions;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestDataProvider {
    public final static BatchInfo batchInfo = new BatchInfo("Java3 Tests");

    public final static String SAUCE_USERNAME = System.getenv("SAUCE_USERNAME");
    public final static String SAUCE_ACCESS_KEY = System.getenv("SAUCE_ACCESS_KEY");
    public final static String SAUCE_SELENIUM_URL = "https://ondemand.saucelabs.com:443/wd/hub";

    public final static String BROWSERSTACK_USERNAME = System.getenv("BROWSERSTACK_USERNAME");
    public final static String BROWSERSTACK_ACCESS_KEY = System.getenv("BROWSERSTACK_ACCESS_KEY");
    public final static String BROWSERSTACK_SELENIUM_URL = "http://hub-cloud.browserstack.com/wd/hub/";

    @DataProvider(name = "booleanDP", parallel = true)
    public static Object[][] booleanDP() {
        return new Object[][]{
                {true}, {false}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dp() {
        ChromeOptions chromeOptions = new ChromeOptions();
        String platformName = System.getenv("APPLITOOLS_TEST_PLATFORM");
        chromeOptions.setCapability(CapabilityType.PLATFORM_NAME, platformName == null ? "Any" : platformName);

        FirefoxOptions firefoxOptions = new FirefoxOptions();

        DesiredCapabilities ie11Options = DesiredCapabilities.internetExplorer();
        ie11Options.setCapability(CapabilityType.BROWSER_VERSION, "11");

        SafariOptions safariOptions = new SafariOptions();

        if (TestUtils.runHeadless) {
            chromeOptions.setHeadless(true);
            firefoxOptions.setHeadless(true);
        }

        String testPlatforms = System.getenv("APPLITOOLS_TEST_PLATFORMS");
        if (testPlatforms == null || testPlatforms.isEmpty()) {
            testPlatforms = System.getProperty("os.name");
        }

        Object[] platforms = testPlatforms.split(";");

        List<List<Object>> lists = new ArrayList<>();
        lists.add(Arrays.asList(new Object[]{
                chromeOptions,
//                firefoxOptions,
//                ie11Options,
//                safariOptions
        }));
        //lists.add(Arrays.asList(platforms));
        lists.add(Arrays.asList(new Object[]{
                "CSS", "SCROLL", "VG"
        }));

        List<Object[]> permutations = TestUtils.generatePermutationsList(lists);
//        int i = 0;
//        while (permutations.size() > 0 && i < permutations.size()) {
//            Object[] perm = permutations.get(i);
//            String browser = ((Capabilities) perm[0]).getBrowserName().toUpperCase().trim();
//            String platform = ((String) perm[1]).toUpperCase().trim();
//            if ((platform.startsWith("WIN") && browser.equals("SAFARI")) ||
//                    (platform.startsWith("MAC") && browser.equals("INTERNET EXPLORER"))) {
//                permutations.remove(i);
//            } else {
//                i++;
//            }
//        }

        return permutations.toArray(new Object[0][]);
    }

}
