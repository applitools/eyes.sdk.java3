package com.applitools.eyes.fluent;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.IBatchCloser;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.triggers.VisualGridRunner0;
import com.applitools.utils.GeneralUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GeneralUtils.class)
public class TestBatchClose {

    private VisualGridRunner0 initRunnerWithBatches(Map<String, IBatchCloser> batchClosers) {
        VisualGridRunner0 runner = new VisualGridRunner0(10);
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
        EnabledBatchClose enabledBatchClose = batchClose.setUrl(serverUrl1).setApiKey("key").setProxy(new ProxySettings("")).setBatchId(batchIds);
        Assert.assertEquals(enabledBatchClose.serverUrl, serverUrl1);
        enabledBatchClose.setUrl(serverUrl2);
        Assert.assertEquals(enabledBatchClose.serverUrl, serverUrl2);

        ServerConnector serverConnector = mock(ServerConnector.class);
        Assert.assertEquals(enabledBatchClose.apiKey, "key");
        Assert.assertNotNull(enabledBatchClose.proxySettings);

        enabledBatchClose.close();
        verify(serverConnector).closeBatch("first", serverUrl2);
        verify(serverConnector).closeBatch("second", serverUrl2);
        verify(serverConnector).closeBatch("third", serverUrl2);
    }

    @Test
    public void testBatchCloseFlag() {
        PowerMockito.spy(GeneralUtils.class);
        when(GeneralUtils.getDontCloseBatches()).thenReturn(false);

        IBatchCloser first = mock(IBatchCloser.class);
        IBatchCloser second = mock(IBatchCloser.class);
        IBatchCloser third = mock(IBatchCloser.class);
        Map<String, IBatchCloser> batchCloserMap = new HashMap<>();
        batchCloserMap.put("first", first);
        batchCloserMap.put("second", second);
        batchCloserMap.put("third", third);

        // default
        VisualGridRunner0 runner = initRunnerWithBatches(batchCloserMap);
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
