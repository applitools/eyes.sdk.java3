package com.applitools.eyes.fluent;

import com.applitools.connectivity.ServerConnector;
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

        String serverUrl = "customUrl";

        BatchClose batchClose = new BatchClose();
        BatchClose.EnableBatchClose enableBatchClose = batchClose.setUrl(serverUrl).setBatchId(batchIds);

        ServerConnector serverConnector = mock(ServerConnector.class);
        enableBatchClose.serverConnector = serverConnector;

        enableBatchClose.close();
        verify(serverConnector).closeBatch("first", true, serverUrl);
        verify(serverConnector).closeBatch("second", true, serverUrl);
        verify(serverConnector).closeBatch("third", true, serverUrl);
    }
}
