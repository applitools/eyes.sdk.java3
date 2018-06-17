package com.applitools.eyes.selenium;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariOptions;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestsDataProvider {
    @DataProvider(parallel = true)
    public static Object[][] dp() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("disable-infobars");

        FirefoxOptions firefoxOptions = new FirefoxOptions();

        DesiredCapabilities ie11Options = DesiredCapabilities.internetExplorer();
        ie11Options.setCapability(CapabilityType.BROWSER_VERSION, "11");

        SafariOptions safariOptions = new SafariOptions();

        if (!System.getenv("SELENIUM_SERVER_URL").contains("ondemand.saucelabs.com")) {
            chromeOptions.setHeadless(true);
            firefoxOptions.setHeadless(true);
        }

        String testPlatforms = System.getenv("APPLITOOLS_TEST_PLATFORMS");
        if (testPlatforms == null || testPlatforms.isEmpty()) {
            testPlatforms = System.getProperty("os.name");
        }

        Object[] platforms = testPlatforms.split(";");

        List<List<Object>> lists = new ArrayList<>();
        lists.add(Arrays.asList(new Object[]{chromeOptions, firefoxOptions/*, ie11Options, safariOptions*/}));
        lists.add(Arrays.asList(platforms));

        Object[][] basicPermutations = TestUtils.generatePermutations(lists);

        List<Object[]> extraPermutations = new ArrayList<>();

        System.out.println();
        System.out.println("Tests will run on the following platforms:");
        for (Object platform : platforms) {
            System.out.println(platform);
            if (((String) platform).startsWith("Windows")) {
                //extraPermutations.add(new Object[]{ie11Options, platform});
            } else if (((String) platform).startsWith("Mac")) {
                extraPermutations.add(new Object[]{safariOptions, platform});
            }
        }
        System.out.println();

        ArrayList<Object[]> permutations = new ArrayList<>();
        permutations.addAll(Arrays.asList(basicPermutations));
        permutations.addAll(extraPermutations);

        return permutations.toArray(new Object[0][]);
    }

//    public static void main(String[] args) {
//        Object[][] dpRes = dp();
//        for (Object[] pers:dpRes) {
//            for (Object p:pers) {
//                System.out.print(p);
//                System.out.print(",");
//            }
//            System.out.println();
//        }
//        System.out.println();
//    }
}
