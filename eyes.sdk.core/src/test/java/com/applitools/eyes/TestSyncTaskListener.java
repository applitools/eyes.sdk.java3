package com.applitools.eyes;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class TestSyncTaskListener {

    @Test
    public void testTwoThreadsWaiting() throws InterruptedException {
        final SyncTaskListener<String> listener = new SyncTaskListener<>(new Logger(new StdoutLogHandler()), "test");

        ExecutorService executor = Executors.newFixedThreadPool(2);
        final AtomicReference<String> result1 = new AtomicReference<>();
        final AtomicReference<String> result2 = new AtomicReference<>();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                result1.set(listener.get());
            }
        });

        executor.execute(new Runnable() {
            @Override
            public void run() {
                result2.set(listener.get());
            }
        });

        Thread.sleep(1000);
        listener.onComplete("hello");
        executor.shutdown();
        Assert.assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        Assert.assertEquals(result1.get(), "hello");
        Assert.assertEquals(result2.get(), "hello");
    }
}
