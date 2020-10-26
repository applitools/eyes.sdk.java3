package com.applitools.eyes.fluent;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.IBatchCloser;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class TestBatchClose {

    private VisualGridRunner initRunnerWithBatches(Map<String, IBatchCloser> batchClosers) {
        VisualGridRunner runner = new VisualGridRunner(10);
        for (String key : batchClosers.keySet()) {
            runner.addBatch(key, batchClosers.get(key));
        }

        return runner;
    }

    @Test
    public void testBatchClose() {
        List<String> batchIds = new ArrayList<>();
        batchIds.add("first");
        batchIds.add("second");
        batchIds.add("third");

        String serverUrl1 = "customUrl1";
        String serverUrl2 = "customUrl2";

        BatchClose batchClose = new BatchClose();
        EnabledBatchClose enabledBatchClose = batchClose.setUrl(serverUrl1).setBatchId(batchIds);
        Assert.assertEquals(enabledBatchClose.serverUrl, serverUrl1);
        enabledBatchClose.setUrl(serverUrl2);
        Assert.assertEquals(enabledBatchClose.serverUrl, serverUrl2);

        ServerConnector serverConnector = mock(ServerConnector.class);
        enabledBatchClose.serverConnector = serverConnector;

        enabledBatchClose.close();
        verify(serverConnector).closeBatch("first", true, serverUrl2);
        verify(serverConnector).closeBatch("second", true, serverUrl2);
        verify(serverConnector).closeBatch("third", true, serverUrl2);
    }

    @Test
    public void testBatchCloseFlag() {
        IBatchCloser first = mock(IBatchCloser.class);
        IBatchCloser second = mock(IBatchCloser.class);
        IBatchCloser third = mock(IBatchCloser.class);
        Map<String, IBatchCloser> batchCloserMap = new HashMap<>();
        batchCloserMap.put("first", first);
        batchCloserMap.put("second", second);
        batchCloserMap.put("third", third);

        // default
        VisualGridRunner runner = initRunnerWithBatches(batchCloserMap);
        runner.getAllTestResults();
        verify(first).closeBatch("first");
        verify(second).closeBatch("second");
        verify(third).closeBatch("third");

        // set true
        reset(first);
        reset(second);
        reset(third);
        runner = initRunnerWithBatches(batchCloserMap);
        runner.setDontCloseBatches(true);
        runner.getAllTestResults();
        verify(first, never()).closeBatch("first");
        verify(second, never()).closeBatch("second");
        verify(third, never()).closeBatch("third");

        // set false
        reset(first);
        reset(second);
        reset(third);
        runner = initRunnerWithBatches(batchCloserMap);
        runner.setDontCloseBatches(false);
        runner.getAllTestResults();
        verify(first).closeBatch("first");
        verify(second).closeBatch("second");
        verify(third).closeBatch("third");
    }
}
