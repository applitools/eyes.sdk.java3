package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.Logger;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.selenium.rendering.VisualGridEyes;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;
import com.applitools.eyes.visualgrid.services.RunningTest;
import com.applitools.eyes.visualgrid.services.VisualGridTask;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.applitools.eyes.visualgrid.services.VisualGridTask.TaskType.*;

public class TestVGNoMissingSteps extends ReportingTestSuite {
    private static Logger logger = new Logger();
    private static RenderBrowserInfo browserInfo = new RenderBrowserInfo(10, 10);

    public TestVGNoMissingSteps() {
        super.setGroupName("selenium");
    }

    @Test
    public void TestNoMissingSteps_UT() {
        logger.setLogHandler(TestUtils.initLogger());

        Configuration config = new Configuration();
        config.setTestName("TestNoMissingSteps_UT");

        List<RunningTest> tests = new ArrayList<>();

        createTest("test1").addTask(OPEN).addTo(tests);
        createTest("test2").setOpenTaskIssued().addTo(tests);
        createTest("test3").setCloseTaskIssued().addTo(tests);
        createTest("test4").addTask(OPEN).addTask(CHECK).addTo(tests);
        createTest("test5").addTask(OPEN).addTask(CHECK).addTask(CLOSE).addTo(tests);
        createTest("test6").addTask(OPEN).addTask(ABORT).addTo(tests);
        createTest("test7").setOpenTaskIssued().setCloseTaskIssued().addTo(tests);
        createTest("test8").addTo(tests);
        createTest("test9").setOpenTaskIssued().addTask(CHECK).addTo(tests);

        List<RunningTest> filteredList = VisualGridEyes.collectTestsForCheck(logger, tests);

        Assert.assertEquals(filteredList.size(), 4);
        Assert.assertEquals(filteredList.get(0).getTestName(), "test1");
        Assert.assertEquals(filteredList.get(1).getTestName(), "test2");
        Assert.assertEquals(filteredList.get(2).getTestName(), "test4");
        Assert.assertEquals(filteredList.get(3).getTestName(), "test9");
    }

    private RunningTestBuilder createTest(String testName) {
        RunningTest test = new RunningTest(browserInfo, logger);
        test.setTestName(testName);
        //noinspection UnnecessaryLocalVariable
        RunningTestBuilder builder = new RunningTestBuilder(test);
        return builder;
    }

    private static class RunningTestBuilder {
        private RunningTest runningTest;

        RunningTestBuilder(RunningTest runningTest) {
            this.runningTest = runningTest;
        }

        void addTo(List<RunningTest> tests) {
            tests.add(runningTest);
        }

        RunningTestBuilder addTask(VisualGridTask.TaskType taskType) {
            VisualGridTask task = createTask(taskType);
            runningTest.getVisualGridTaskList().add(task);
            return this;
        }

        private VisualGridTask createTask(VisualGridTask.TaskType taskType) {
            return new VisualGridTask(taskType, logger, runningTest);
        }

        RunningTestBuilder setOpenTaskIssued() {
            runningTest.setOpenTask(createTask(OPEN));
            return this;
        }

        RunningTestBuilder setCloseTaskIssued() {
            runningTest.setCloseTask(createTask(CLOSE));
            return this;
        }
    }
}
