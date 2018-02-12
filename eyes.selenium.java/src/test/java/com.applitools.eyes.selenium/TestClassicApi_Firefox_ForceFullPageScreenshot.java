package com.applitools.eyes.selenium;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.model.Statement;
import org.openqa.selenium.firefox.FirefoxOptions;

@RunWith(Parameterized.class)
@Category(CI.class)
public class TestClassicApi_Firefox_ForceFullPageScreenshot extends TestClassicApi {

    @ClassRule
    public static final TestRule setTestSuitName = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            testSuitName = "Eyes Selenium SDK - Classic API - ForceFPS";
            testedPageUrl = "http://applitools.github.io/demo/TestPages/FramesTestPage/";
            forceFullPageScreenshot = true;
        }
    };

    @Rule
    public final TestRule beforeTest = new TestWatcher() {
        @Override
        public Statement apply(Statement statement, Description description) {
            FirefoxOptions options = new FirefoxOptions();
            if (!System.getenv("SELENIUM_SERVER_URL").contains("ondemand.saucelabs.com")) {
                options.setHeadless(true);
            }
            caps = options;

            return statement;
        }
    };
}
