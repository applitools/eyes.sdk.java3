package com.applitools.eyes.fluent;

import com.applitools.connectivity.ServerConnector;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TestBatchClose {

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
}
