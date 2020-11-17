package com.applitools.eyes.selenium;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

public class TestMatchWindow extends ReportingTestSuite {

    private WebDriver driver;

    public TestMatchWindow() {
        super.setGroupName("selenium");
    }

    @BeforeMethod
    public void beforeEach() {
        driver = SeleniumUtils.createChromeDriver();
    }

    @DataProvider(name = "testParameters")
    public static Object[][] testParameters() {
        List<Object[]> params = Arrays.asList(new Object[][]{
                {"https://applitools.github.io/demo/TestPages/SpecialCases/everchanging.html",
                        Arrays.asList(false, true)},
                {"https://applitools.github.io/demo/TestPages/SpecialCases/neverchanging.html",
                        Collections.singletonList(false)}
        });
        return params.toArray(new Object[0][]);
    }

    @Test(dataProvider = "testParameters")
    public void testReplaceMatchedStep(String testedUrl, List<Boolean> expectedReplaceLastList) {
        super.addSuiteArg("testedUrl", testedUrl);
        super.addSuiteArg("expectedReplaceLastList", expectedReplaceLastList);
        ServerConnector serverConnector = spy(ServerConnector.class);
        final List<Boolean> replaceLastList = new ArrayList<>();
        doAnswer(new Answer<MatchResult>() {
            @Override
            public MatchResult answer(InvocationOnMock invocationOnMock) {
                TaskListener<MatchResult> listener = invocationOnMock.getArgument(0);
                MatchWindowData matchWindowData = invocationOnMock.getArgument(1);
                replaceLastList.add(matchWindowData.getOptions().getReplaceLast());
                MatchResult matchResult = new MatchResult();
                matchResult.setAsExpected(false);
                listener.onComplete(matchResult);
                return null;
            }
        }).when(serverConnector).matchWindow(ArgumentMatchers.<TaskListener<MatchResult>>any(), any(MatchWindowData.class));
        EyesRunner runner = new ClassicRunner();
        Eyes eyes = new Eyes(runner);
        eyes.setServerConnector(serverConnector);
        eyes.setLogHandler(new StdoutLogHandler());
        try {
            driver.get(testedUrl);
            eyes.open(driver, "Applitools Eyes SDK", "testReplaceMatchedStep", new RectangleSize(700, 460));
            eyes.checkWindow("Step 1");
            eyes.closeAsync();
        } finally {
            eyes.abortIfNotClosed();
            runner.getAllTestResults();
        }

        TestResultsSummary summary = runner.getAllTestResults(false);
        TestResults results = summary.getAllResults()[0].getTestResults();
        results.delete();

        Assert.assertEquals(replaceLastList, expectedReplaceLastList);
    }

    @AfterMethod
    public void afterEach() {
        driver.quit();
    }
}