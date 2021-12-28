package com.applitools.eyes;

import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestEyesRunner {
    @Test
    public void testConcurrencyAmount() {
        EyesRunner runner = new VisualGridRunner();
        Assert.assertEquals(runner.testConcurrency.actualConcurrency, EyesRunner.DEFAULT_CONCURRENCY);
        Assert.assertEquals(runner.testConcurrency.userConcurrency, EyesRunner.DEFAULT_CONCURRENCY);
        Assert.assertTrue(runner.testConcurrency.isDefault);
        Assert.assertFalse(runner.testConcurrency.isLegacy);

        runner = new VisualGridRunner("");
        Assert.assertEquals(runner.testConcurrency.actualConcurrency, EyesRunner.DEFAULT_CONCURRENCY);
        Assert.assertEquals(runner.testConcurrency.userConcurrency, EyesRunner.DEFAULT_CONCURRENCY);
        Assert.assertTrue(runner.testConcurrency.isDefault);
        Assert.assertFalse(runner.testConcurrency.isLegacy);

        runner = new VisualGridRunner(10);
        Assert.assertEquals(runner.testConcurrency.actualConcurrency, 50);
        Assert.assertEquals(runner.testConcurrency.userConcurrency, 10);
        Assert.assertFalse(runner.testConcurrency.isDefault);
        Assert.assertTrue(runner.testConcurrency.isLegacy);

        runner = new VisualGridRunner(5, "");
        Assert.assertEquals(runner.testConcurrency.actualConcurrency, 25);
        Assert.assertEquals(runner.testConcurrency.userConcurrency, 5);
        Assert.assertFalse(runner.testConcurrency.isDefault);
        Assert.assertTrue(runner.testConcurrency.isLegacy);

        runner = new VisualGridRunner(new RunnerOptions().testConcurrency(5));
        Assert.assertEquals(runner.testConcurrency.actualConcurrency, 5);
        Assert.assertEquals(runner.testConcurrency.userConcurrency, 5);
        Assert.assertFalse(runner.testConcurrency.isDefault);
        Assert.assertFalse(runner.testConcurrency.isLegacy);

        runner = new VisualGridRunner(new RunnerOptions().testConcurrency(5).testConcurrency(7));
        Assert.assertEquals(runner.testConcurrency.actualConcurrency, 7);
        Assert.assertEquals(runner.testConcurrency.userConcurrency, 7);
        Assert.assertFalse(runner.testConcurrency.isDefault);
        Assert.assertFalse(runner.testConcurrency.isLegacy);

        runner = new VisualGridRunner(new RunnerOptions().testConcurrency(10), "");
        Assert.assertEquals(runner.testConcurrency.actualConcurrency, 10);
        Assert.assertEquals(runner.testConcurrency.userConcurrency, 10);
        Assert.assertFalse(runner.testConcurrency.isDefault);
        Assert.assertFalse(runner.testConcurrency.isLegacy);
    }

    @Test
    public void testConcurrencyLogMessage() throws JsonProcessingException {
        VisualGridRunner runner = new VisualGridRunner();
        Assert.assertEquals(runner.getConcurrencyLog(),
                String.format("{\"type\":\"runnerStarted\",\"defaultConcurrency\":%d}", EyesRunner.DEFAULT_CONCURRENCY));

        runner = new VisualGridRunner(10);
        Assert.assertEquals(runner.getConcurrencyLog(),
                String.format("{\"type\":\"runnerStarted\",\"concurrency\":%d}", 10));

        runner = new VisualGridRunner(new RunnerOptions().testConcurrency(10));
        Assert.assertEquals(runner.getConcurrencyLog(),
                String.format("{\"type\":\"runnerStarted\",\"testConcurrency\":%d}", 10));

        Assert.assertNull(runner.getConcurrencyLog());
    }
}
