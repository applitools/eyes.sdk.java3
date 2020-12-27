package com.applitools.eyes;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.logging.*;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TestLogger extends ReportingTestSuite {

    public TestLogger() {
        super.setGroupName("core");
    }

    @Test
    public void testNetworkLogger() {
        ServerConnector serverConnector = new ServerConnector();
        NetworkLogHandler networkLogHandler = new NetworkLogHandler(serverConnector);
        Logger logger = new Logger(networkLogHandler);
        logger.setAgentId("agentId");
        logger.log(TraceLevel.Warn, Collections.singleton("testId"), Stage.GENERAL, Type.CLOSE_BATCH, Pair.of("message", "hello"));
        Assert.assertEquals(networkLogHandler.clientEvents.size(), 1);
        ClientEvent event = networkLogHandler.clientEvents.getEvents().get(0);

        Map<String, Object> data = new HashMap<>();
        data.put("message", "hello");
        Message expected = new Message("agentId", Stage.GENERAL, Type.CLOSE_BATCH, Collections.singleton("testId"),
                Thread.currentThread().getId(), "com.applitools.eyes.TestLogger.testNetworkLogger()", data);
        Assert.assertEquals(event.getEvent(), expected);
        Assert.assertEquals(event.getLevel(), TraceLevel.Warn);
        networkLogHandler.close();
        Assert.assertEquals(networkLogHandler.clientEvents.size(), 0);
    }

    @Test
    public void testMultiLogHandler() {
        MultiLogHandler multiLogHandler = new MultiLogHandler(new StdoutLogHandler(), new StdoutLogHandler(),
                new FileLogger("a", false, false));
        Assert.assertEquals(multiLogHandler.logHandlers.size(), 2);

        multiLogHandler.addLogHandler(new StdoutLogHandler());
        multiLogHandler.addLogHandler(new FileLogger("a", false, false));
        Assert.assertEquals(multiLogHandler.logHandlers.size(), 2);

        multiLogHandler.addLogHandler(new FileLogger("b", false, false));
        Assert.assertEquals(multiLogHandler.logHandlers.size(), 3);
    }
}
