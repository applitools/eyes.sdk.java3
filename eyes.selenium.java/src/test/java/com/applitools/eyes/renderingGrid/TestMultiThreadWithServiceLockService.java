package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.model.CompletableTask;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.eyes.visualgrid.services.VisualGridTask;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public final class TestMultiThreadWithServiceLockService {

    private VisualGridRunner renderingManager;
    private WebDriver webDriver;
    private final Object openerLock = new Object();
    private final Object checkerLock = new Object();
    private final Object closerLock = new Object();
    private final Object renderLock = new Object();
    private final Object threadALock = new Object();
    private final Object threadBLock = new Object();
    private int concurrentOpenSessions;

    @BeforeMethod
    public void Before(ITestContext testContext) {
        concurrentOpenSessions = 3;
        renderingManager = new VisualGridRunner(concurrentOpenSessions, openerLock, checkerLock, closerLock, renderLock);
        renderingManager.setLogHandler(new StdoutLogHandler(TestUtils.verboseLogs));

        webDriver = SeleniumUtils.createChromeDriver();
        webDriver.get("https://applitools.github.io/demo/TestPages/VisualGridTestPage");
        //webDriver.get("http://applitools-vg-test.surge.sh/test.html");

//        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test() throws Exception {

        Thread threadA = new Thread(new Runnable() {
            @Override
            public void run() {
                String baselineEnvName = "";
                TestThreadMethod("VG-MultiThreadBatchC11", threadALock,
                        new RenderBrowserInfo(800, 600, BrowserType.CHROME, baselineEnvName),
                        new RenderBrowserInfo(700, 500, BrowserType.CHROME, baselineEnvName),
                        new RenderBrowserInfo(400, 300, BrowserType.CHROME, baselineEnvName));
            }
        }, "ThreadA");

        Thread threadB = new Thread(new Runnable() {
            @Override
            public void run() {
                String baselineEnvName = "";
                TestThreadMethod("VG-MultiThreadBatchC22", threadBLock,
                        new RenderBrowserInfo(840, 680, BrowserType.CHROME, baselineEnvName),
                        new RenderBrowserInfo(750, 530, BrowserType.CHROME, baselineEnvName));
            }
        }, "ThreadB");

        //Start the thread and wait for all tasks to be created
        threadA.start();
        synchronized (threadALock) {
            threadALock.wait();
        }

        //Start the thread and wait for all tasks to be created
        threadB.start();
        synchronized (threadBLock) {
            threadBLock.wait();
        }


        //Get all tasks
        List<CompletableTask> allOpenTasks = renderingManager.getAllTasksByType(VisualGridTask.TaskType.OPEN);
        List<CompletableTask> allCheckTasks = renderingManager.getAllTasksByType(VisualGridTask.TaskType.CHECK);
        List<CompletableTask> allCloseTasks = renderingManager.getAllTasksByType(VisualGridTask.TaskType.CLOSE);
        List<CompletableTask> allRenderingTasks = (List<CompletableTask>) renderingManager.getAllRenderingTasks();


        //Test that all tests are not opened yet.
        checkAllTaskAreNotComplete(allOpenTasks, "OPEN");

        //Test that all tests are not checked yet.
        checkAllTaskAreNotComplete(allCheckTasks, "CHECK");

        //Test that all tests are not closed yet.
        checkAllTaskAreNotComplete(allCloseTasks, "CLOSE");

        //Test that all tests are not rendered yet.
        checkAllTaskAreNotComplete(allRenderingTasks, "RENDER");

        //Start Rendering
        synchronized (renderLock) {
            renderLock.notify();
            //Wait for 2 renders to be submitted
            renderLock.wait();
            renderLock.wait();
        }

        int openedTaskCount = startServiceAndCountCompletedTasks(allOpenTasks, openerLock);

        Assert.assertEquals(openedTaskCount, concurrentOpenSessions, "Completed opened tasks are not equal to concurrency");

        int closeTasksCount = startServiceAndCountCompletedTasks(allCloseTasks, closerLock);

        Assert.assertEquals(closeTasksCount, 0, "Close tasks are completed before check tasks");

        int checkedTasksCount = startServiceAndCountCompletedTasks(allCheckTasks, checkerLock);

        Assert.assertEquals(checkedTasksCount, concurrentOpenSessions, "Completed checked tasks are not equal to concurrency");

        pauseAllServices();

        closeTasksCount = startServiceAndCountCompletedTasks(allCloseTasks, closerLock);

        Assert.assertEquals(closeTasksCount, concurrentOpenSessions);

        openedTaskCount = startServiceAndCountCompletedTasks(allOpenTasks, openerLock);

        Assert.assertEquals(openedTaskCount, 5, "Completed opened tasks are not equal to concurrency");

        checkedTasksCount = startServiceAndCountCompletedTasks(allCheckTasks, checkerLock);

        Assert.assertEquals(checkedTasksCount, 5, "Completed checked tasks are not equal to concurrency");

        closeTasksCount = startServiceAndCountCompletedTasks(allCloseTasks, closerLock);

        Assert.assertEquals(closeTasksCount, 5, "Close tasks are completed before check tasks");

    }

    private void pauseAllServices() {
        this.renderingManager.pauseAllService();
    }

    private int startServiceAndCountCompletedTasks(List<CompletableTask> allOpenTasks, Object debugLock) throws InterruptedException {

        //Start Opener and wait for 5 open tasks
        synchronized (debugLock) {
            debugLock.notify();
        }

        for (int i = 0; i < 5; i++) {
            synchronized (debugLock) {
                debugLock.wait(1000);
            }
        }

        int completedTasks = 0;
        for (CompletableTask openTask : allOpenTasks) {
            if (openTask.getIsTaskComplete()) completedTasks++;
        }

        return completedTasks;
    }

    private void checkAllTaskAreNotComplete(List<CompletableTask> allOpenTasks, String type) throws Exception {
        for (CompletableTask task : allOpenTasks) {
            Assert.assertFalse(task.getIsTaskComplete(), type + " VisualGridTask is complete without notify openerService");
        }
    }

    private void TestThreadMethod(String batchName, Object lock, RenderBrowserInfo... browsersInfo) {
        Eyes eyes = new Eyes(renderingManager);
        eyes.setBatch(new BatchInfo(batchName));
        Configuration configuration = new Configuration();
        configuration.setTestName("Open Concurrency with Batch 3");
        configuration.setAppName("RenderingGridIntegration");
        configuration.addBrowsers(browsersInfo);
        eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888));
        eyes.setConfiguration(configuration);
        eyes.open(webDriver);
        eyes.check(Target.window().withName("test").sendDom(false));
        TestResults close = eyes.close();
        Assert.assertNotNull(close);
    }

    @AfterMethod
    public void After(ITestContext testContext) {
        renderingManager.getAllTestResults();
        renderingManager.getLogger().log(renderingManager.getAllTestResults().toString());
        webDriver.quit();
    }
}