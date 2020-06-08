package com.applitools.eyes.visualgrid.services;

import com.applitools.eyes.Logger;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.ISeleniumConfigurationProvider;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.visualgrid.model.DesktopBrowserInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TestRunningTest extends ReportingTestSuite {

    DesktopBrowserInfo browserInfo = mock(DesktopBrowserInfo.class);
    Logger logger = mock(Logger.class);
    ISeleniumConfigurationProvider configurationProvider = mock(ISeleniumConfigurationProvider.class);

    RunningTest runningTest;
    VisualGridTask openTask;
    VisualGridTask checkTask;
    VisualGridTask closeTask;

    public TestRunningTest() {
        super.setGroupName("core");
    }

    @BeforeMethod
    public void beforeEach() {
        doNothing().when(logger).verbose(anyString());
        doNothing().when(logger).log(anyString());

        when(configurationProvider.get()).thenReturn(new Configuration());

        runningTest = new RunningTest(browserInfo, logger, configurationProvider);
        openTask = new VisualGridTask(VisualGridTask.TaskType.OPEN, logger, runningTest);
        checkTask = new VisualGridTask(VisualGridTask.TaskType.CHECK, logger, runningTest);
        closeTask = new VisualGridTask(VisualGridTask.TaskType.CLOSE, logger, runningTest);
    }

    @Test
    public void testAbortWhenCloseCalled() {
        List<VisualGridTask> tasks = runningTest.getVisualGridTaskList();
        tasks.add(openTask);
        tasks.add(checkTask);
        tasks.add(closeTask);
        runningTest.setCloseTask(closeTask);
        runningTest.abort(false, null);

        Assert.assertEquals(3, tasks.size());
        Assert.assertEquals(tasks.get(0), openTask);
        Assert.assertEquals(tasks.get(1), checkTask);
        Assert.assertEquals(tasks.get(2), closeTask);
    }

    @Test
    public void testForceAbortWhenCloseCalled() {
        List<VisualGridTask> tasks = runningTest.getVisualGridTaskList();
        tasks.add(openTask);
        tasks.add(checkTask);
        tasks.add(closeTask);
        runningTest.setCloseTask(closeTask);

        Exception exception = new Exception();
        runningTest.abort(true, exception);

        Assert.assertEquals(2, tasks.size());
        Assert.assertEquals(tasks.get(0), openTask);
        Assert.assertEquals(tasks.get(1), closeTask);
        Assert.assertEquals(closeTask.getType(), VisualGridTask.TaskType.ABORT);
        Assert.assertEquals(closeTask.getException(), exception);
    }

    @Test
    public void testAbortWhenCloseNotCalled() {
        List<VisualGridTask> tasks = runningTest.getVisualGridTaskList();
        tasks.add(openTask);
        tasks.add(checkTask);
        runningTest.setOpenTask(openTask);

        Exception exception = new Exception();
        runningTest.abort(false, exception);

        Assert.assertEquals(2, tasks.size());
        Assert.assertEquals(tasks.get(0), openTask);
        Assert.assertEquals(openTask.getException(), exception);

        VisualGridTask abortTask = tasks.get(1);
        Assert.assertEquals(abortTask.getType(), VisualGridTask.TaskType.ABORT);
        Assert.assertEquals(runningTest.getCloseTask(), abortTask);
    }

    @Test
    public void testForceAbortWhenCloseNotCalled() {
        List<VisualGridTask> tasks = runningTest.getVisualGridTaskList();
        tasks.add(openTask);
        tasks.add(checkTask);
        runningTest.setOpenTask(openTask);

        Exception exception = new Exception();
        runningTest.abort(true, exception);

        Assert.assertEquals(2, tasks.size());
        Assert.assertEquals(tasks.get(0), openTask);
        Assert.assertEquals(openTask.getException(), exception);

        VisualGridTask abortTask = tasks.get(1);
        Assert.assertEquals(abortTask.getType(), VisualGridTask.TaskType.ABORT);
        Assert.assertEquals(runningTest.getCloseTask(), abortTask);
    }
}
