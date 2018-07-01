package com.applitools.eyes.selenium;

import org.openqa.selenium.Capabilities;
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

        String runHeadless = System.getenv("APPLITOOLS_RUN_HEADLESS");
        if (runHeadless != null && runHeadless.equalsIgnoreCase("true")) {
            chromeOptions.setHeadless(true);
            firefoxOptions.setHeadless(true);
        }

        String testPlatforms = System.getenv("APPLITOOLS_TEST_PLATFORMS");
        if (testPlatforms == null || testPlatforms.isEmpty()) {
            testPlatforms = System.getProperty("os.name");
        }

        Object[] platforms = testPlatforms.split(";");

        List<List<Object>> lists = new ArrayList<>();
        lists.add(Arrays.asList(new Object[]{chromeOptions, firefoxOptions, ie11Options, safariOptions}));
        lists.add(Arrays.asList(platforms));

        List<Object[]> permutations = TestUtils.generatePermutationsList(lists);
        int i=0;
        while (permutations.size() > 0 && i < permutations.size()){
            Object[] perm = permutations.get(i);
            String browser = ((Capabilities)perm[0]).getBrowserName().toUpperCase().trim();
            String platform = ((String)perm[1]).toUpperCase().trim();
            if ((platform.startsWith("WIN") && browser.equals("SAFARI")) ||
                (platform.startsWith("MAC") && browser.equals("INTERNET EXPLORER"))) {
                permutations.remove(i);
            } else {
                i++;
            }
        }

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
