package com.applitools.eyes.visualgrid.services;

import com.applitools.ICheckSettings;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RunningTest;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Mockito.mock;

public class TestRunningTest extends ReportingTestSuite {

    RunningTest runningTest;

    public TestRunningTest() {
        super.setGroupName("core");
    }

    @BeforeMethod
    public void beforeEach() {
        runningTest = new VisualGridRunningTest(new Logger(), true, "", mock(RenderBrowserInfo.class), new Configuration());
        runningTest.issueCheck(mock(ICheckSettings.class), mock(List.class), "");
        runningTest.issueCheck(mock(ICheckSettings.class), mock(List.class), "");
        runningTest.issueCheck(mock(ICheckSettings.class), mock(List.class), "");
    }

    @Test
    public void testAbortWhenCloseCalled() {
        Assert.assertFalse(runningTest.isCloseTaskIssued());
        runningTest.issueClose();
        runningTest.issueAbort(new Exception(""), false);

        Assert.assertEquals(runningTest.getNumberOfStepsLeft(), 3);
        Assert.assertTrue(runningTest.isCloseTaskIssued());
        Assert.assertFalse(runningTest.isTestAborted());
    }

    @Test
    public void testForceAbortWhenCloseCalled() {
        runningTest.issueClose();
        runningTest.issueAbort(new Exception(""), true);

        Assert.assertEquals(runningTest.getNumberOfStepsLeft(), 0);
        Assert.assertTrue(runningTest.isCloseTaskIssued());
        Assert.assertTrue(runningTest.isTestAborted());
    }

    @Test
    public void testAbortWhenCloseNotCalled() {
        runningTest.issueAbort(new Exception(""), false);

        Assert.assertEquals(runningTest.getNumberOfStepsLeft(), 0);
        Assert.assertTrue(runningTest.isCloseTaskIssued());
        Assert.assertTrue(runningTest.isTestAborted());
    }

    @Test
    public void testForceAbortWhenCloseNotCalled() {
        runningTest.issueAbort(new Exception(""), true);

        Assert.assertEquals(runningTest.getNumberOfStepsLeft(), 0);
        Assert.assertTrue(runningTest.isCloseTaskIssued());
        Assert.assertTrue(runningTest.isTestAborted());
    }
}
