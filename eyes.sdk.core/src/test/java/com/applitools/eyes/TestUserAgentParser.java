package com.applitools.eyes;

import com.applitools.eyes.utils.ReportingTestSuite;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestUserAgentParser extends ReportingTestSuite {

    public TestUserAgentParser() {
        super.setGroupName("core");
    }

    @DataProvider(name = "dp")
    public static Object[][] dp() {
        return new Object[][]{
                {"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36", OSNames.WINDOWS, "10", "0", BrowserNames.CHROME, "75", "0"},
                {"Mozilla/5.0 (Linux; Android 9; Android SDK built for x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.105 Mobile Safari/537.36", OSNames.ANDROID, "9", "0", BrowserNames.CHROME, "72", "0"},
                {"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0", OSNames.WINDOWS, "7", "0", BrowserNames.FIREFOX, "54", "0"},
                {"Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko", OSNames.WINDOWS, "7", "0", BrowserNames.IE, "11", "0"},
                {"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)", OSNames.WINDOWS, "7", "0", BrowserNames.IE, "10", "0"},
                {"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/74.0.3729.157 Safari/537.36", OSNames.LINUX, "", "", BrowserNames.CHROME, "74", "0"},
                {"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:50.0) Gecko/20100101 Firefox/50.0", OSNames.LINUX, "", "", BrowserNames.FIREFOX, "50", "0"},
                {"Mozilla/5.0 (Linux; Android 6.0.1; SM-J700M Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Mobile Safari/537.36", OSNames.ANDROID, "6", "0", BrowserNames.CHROME, "69", "0"},
                {"Mozilla/5.0 (iPhone; CPU iPhone OS 12_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.0 Mobile/15E148 Safari/604.1", OSNames.IOS, "12", "1", BrowserNames.SAFARI, "12", "0"},
                {"Mozilla/5.0 (iPad; CPU OS 11_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1", OSNames.IOS, "11", "3", BrowserNames.SAFARI, "11", "0"},
                {"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_1) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.0.1 Safari/605.1.15", OSNames.MAC_OS_X, "10", "14", BrowserNames.SAFARI, "12", "0"},
                {"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36", OSNames.WINDOWS, "10", "0", BrowserNames.CHROME, "74", "0"},
                {"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.117 Safari/537.36", OSNames.WINDOWS, "7", "0", BrowserNames.CHROME, "33", "0"},
                {"Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36", OSNames.WINDOWS, "6", "3", BrowserNames.CHROME, "60", "0"},
                {"Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5355d Safari/8536.25", OSNames.IOS, "6", "0", BrowserNames.SAFARI, "6", "0"},
                {"Mozilla/5.0 (iPad; CPU OS 5_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko ) Version/5.1 Mobile/9B176 Safari/7534.48.3", OSNames.IOS, "5", "1", BrowserNames.SAFARI, "5", "1"},
                {"Mozilla/5.0 (iPod; U; CPU iPhone OS 4_3_3 like Mac OS X; ja-jp) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5", OSNames.IOS, "4", "3", BrowserNames.SAFARI, "5", "0"},
                {"Mozilla/5.0 (iPhone Simulator; U; CPU iPhone OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7D11 Safari/531.21.10", OSNames.IOS, "3", "2", BrowserNames.SAFARI, "4", "0"},
                {"Mozilla/5.0 (Linux; Android 9; Pixel 3 XL) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.99 Mobile Safari/537.36", OSNames.ANDROID, "9", "0", BrowserNames.CHROME, "80", "0"}
        };
    }

    @Test(dataProvider = "dp")
    public void TestUAParsing(ITestContext testContext,
                              String uaStr,
                              String expectedOs,
                              String expectedOsMajorVersion,
                              String expectedOsMinorVersion,
                              String expectedBrowser,
                              String expectedBrowserMajorVersion,
                              String expectedBrowserMinorVersion) {
        super.addTestParameter(testContext,"UserAgent", uaStr);
        UserAgent ua = UserAgent.parseUserAgentString(uaStr);
        Assert.assertEquals(ua.getOS(), expectedOs, "OS");
        Assert.assertEquals(ua.getOSMajorVersion(), expectedOsMajorVersion, "OS Major Version");
        Assert.assertEquals(ua.getOSMinorVersion(), expectedOsMinorVersion, "OS Minor Version");
        Assert.assertEquals(ua.getBrowser(), expectedBrowser, "Browser");
        Assert.assertEquals(ua.getBrowserMajorVersion(), expectedBrowserMajorVersion, "Browser Major Version");
        Assert.assertEquals(ua.getBrowserMinorVersion(), expectedBrowserMinorVersion, "Browser Minor Version");
    }
}
