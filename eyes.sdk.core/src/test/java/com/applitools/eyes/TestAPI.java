package com.applitools.eyes;

import com.applitools.eyes.exceptions.TestFailedException;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.utils.ReportingTestSuite;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestAPI extends ReportingTestSuite {
    public TestAPI(){
        super.setGroupName("core");
    }
    @Test
    public void TestBrowserNames() {
        List<BrowserType> browsers = new ArrayList<>(Arrays.asList(BrowserType.values()));
        Assert.assertEquals(browsers.size(), 16, "wrong number of browser types");
        for (BrowserType browser : browsers) {
            Assert.assertNotNull(BrowserNames.getBrowserName(browser), "BrowserType." + browser.name() + " is not covered by BrowserNames.getBrowserName");
        }
        Assert.assertEquals(BrowserNames.CHROME, BrowserNames.getBrowserName(BrowserType.CHROME));
        browsers.remove(BrowserType.CHROME);
        Assert.assertEquals(BrowserNames.CHROME, BrowserNames.getBrowserName(BrowserType.CHROME_ONE_VERSION_BACK));
        browsers.remove(BrowserType.CHROME_ONE_VERSION_BACK);
        Assert.assertEquals(BrowserNames.CHROME, BrowserNames.getBrowserName(BrowserType.CHROME_TWO_VERSIONS_BACK));
        browsers.remove(BrowserType.CHROME_TWO_VERSIONS_BACK);

        Assert.assertEquals(BrowserNames.FIREFOX, BrowserNames.getBrowserName(BrowserType.FIREFOX));
        browsers.remove(BrowserType.FIREFOX);
        Assert.assertEquals(BrowserNames.FIREFOX, BrowserNames.getBrowserName(BrowserType.FIREFOX_ONE_VERSION_BACK));
        browsers.remove(BrowserType.FIREFOX_ONE_VERSION_BACK);
        Assert.assertEquals(BrowserNames.FIREFOX, BrowserNames.getBrowserName(BrowserType.FIREFOX_TWO_VERSIONS_BACK));
        browsers.remove(BrowserType.FIREFOX_TWO_VERSIONS_BACK);

        Assert.assertEquals(BrowserNames.SAFARI, BrowserNames.getBrowserName(BrowserType.SAFARI));
        browsers.remove(BrowserType.SAFARI);
        Assert.assertEquals(BrowserNames.SAFARI, BrowserNames.getBrowserName(BrowserType.SAFARI_ONE_VERSION_BACK));
        browsers.remove(BrowserType.SAFARI_ONE_VERSION_BACK);
        Assert.assertEquals(BrowserNames.SAFARI, BrowserNames.getBrowserName(BrowserType.SAFARI_TWO_VERSIONS_BACK));
        browsers.remove(BrowserType.SAFARI_TWO_VERSIONS_BACK);
        Assert.assertEquals(BrowserNames.SAFARI, BrowserNames.getBrowserName(BrowserType.SAFARI_EARLY_ACCESS));
        browsers.remove(BrowserType.SAFARI_EARLY_ACCESS);

        Assert.assertEquals(BrowserNames.IE + " 10", BrowserNames.getBrowserName(BrowserType.IE_10));
        browsers.remove(BrowserType.IE_10);
        Assert.assertEquals(BrowserNames.IE + " 11", BrowserNames.getBrowserName(BrowserType.IE_11));
        browsers.remove(BrowserType.IE_11);

        Assert.assertEquals(BrowserNames.EDGE, BrowserNames.getBrowserName(BrowserType.EDGE));
        browsers.remove(BrowserType.EDGE);
        Assert.assertEquals(BrowserNames.EDGE, BrowserNames.getBrowserName(BrowserType.EDGE_LEGACY));
        browsers.remove(BrowserType.EDGE_LEGACY);

        Assert.assertEquals(BrowserNames.EDGE_CHROMIUM, BrowserNames.getBrowserName(BrowserType.EDGE_CHROMIUM));
        browsers.remove(BrowserType.EDGE_CHROMIUM);
        Assert.assertEquals(BrowserNames.EDGE_CHROMIUM, BrowserNames.getBrowserName(BrowserType.EDGE_CHROMIUM_ONE_VERSION_BACK));
        browsers.remove(BrowserType.EDGE_CHROMIUM_ONE_VERSION_BACK);

        Assert.assertEquals(0, browsers.size(), "Not all browser types names has been verified. Remaining browser types: " + StringUtils.join(browsers, ", "));
    }

    @Test
    public void testFailedException() {
        TestResults testResults = new TestResults();
        testResults.setStatus(TestResultsStatus.Failed);
        TestFailedException testFailedException = new TestFailedException(testResults, "message");
        Assert.assertEquals(testFailedException.getTestResults(), testResults);
        testFailedException = new TestFailedException("message");
        Assert.assertNull(testFailedException.getTestResults());
    }
}
