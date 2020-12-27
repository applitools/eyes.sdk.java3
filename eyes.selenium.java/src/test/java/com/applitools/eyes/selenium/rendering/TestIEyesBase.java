package com.applitools.eyes.selenium.rendering;

import com.applitools.ICheckSettings;
import com.applitools.eyes.LogHandler;
import com.applitools.eyes.Logger;
import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public abstract class TestIEyesBase extends ReportingTestSuite {

    protected final LogHandler logHandler;
    protected final String SERVER_URL = "https://eyes.applitools.com/";
    protected final String API_KEY = System.getenv("APPLITOOLS_API_KEY");
    protected Logger logger;
    public String LogPath;


    @DataProvider(name = "TTS")
    public Object[][] dp() {
        return new Object[][]{
                new Object[]{"https://amazon.com", MatchLevel.LAYOUT},
                new Object[]{"https://applitools.com/features/frontend-development", MatchLevel.STRICT},
                new Object[]{"https://applitools.com/docs/topics/overview.html", MatchLevel.STRICT},
                new Object[]{"https://docs.microsoft.com/en-us/", MatchLevel.STRICT},
                new Object[]{"https://ebay.com", MatchLevel.LAYOUT},
                new Object[]{"https://facebook.com", MatchLevel.STRICT},
                new Object[]{"https://google.com", MatchLevel.STRICT},
                new Object[]{"https://instagram.com", MatchLevel.STRICT},
                new Object[]{"https://twitter.com", MatchLevel.STRICT},
                new Object[]{"https://wikipedia.org", MatchLevel.STRICT},
                new Object[]{"https://www.target.com/c/blankets-throws/-/N-d6wsb?lnk=ThrowsBlankets%E2%80%9C,tc", MatchLevel.STRICT},
                new Object[]{"https://youtube.com", MatchLevel.LAYOUT},

                //new Object[] {"https://www.usatoday.com", MatchLevel.LAYOUT },
                //new Object[] {"https://www.vans.com", // TODO - this website get the flow to stuck in an endless loop.
                //new Object[] { "https://www.qq.com/", MatchLevel.STRICT },
        };
    }

    protected TestIEyesBase(String fixtureName) {
        super.setGroupName("selenium");
        LogPath = TestUtils.initLogPath(fixtureName);
        logHandler = TestUtils.initLogger(fixtureName, LogPath);
    }


    @Test(dataProvider = "TTS")
    public void TestEyesDifferentRunners(String testedUrl, MatchLevel matchLevel) {
        super.addSuiteArg("testUrl", testedUrl);
        super.addSuiteArg("matchLevel", matchLevel);
        WebDriver webDriver = SeleniumUtils.createChromeDriver();
        Logger logger;
        Eyes eyes = null;
        try {
            webDriver.get(testedUrl);
            eyes = initEyes(webDriver, testedUrl);
            eyes.setSaveNewTests(false);
            logger = eyes.getLogger();
            ICheckSettings checkSettings = getCheckSettings();
            eyes.setMatchLevel(matchLevel);
            eyes.check(checkSettings.withName("Step1 - " + testedUrl));
            eyes.check(checkSettings.fully().withName("Step2 - " + testedUrl));
            TestResults results = eyes.close(false);
            validateResults(eyes, results);
        } finally {
            if (eyes != null) {
                eyes.abort();
            }
            webDriver.quit();
        }
    }

    abstract void validateResults(Eyes eyes, TestResults results);

    protected ICheckSettings getCheckSettings() {
        return Target.window();
    }

    protected abstract Eyes initEyes(WebDriver webDriver, String testedUrl);
}
