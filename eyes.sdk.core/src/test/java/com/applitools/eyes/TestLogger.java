package com.applitools.eyes;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.logging.ClientEvent;
import com.applitools.eyes.logging.TraceLevel;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestLogger {

    @Test
    public void testNetworkLogger() {
        ServerConnector serverConnector = new ServerConnector();
        NetworkLogHandler networkLogHandler = new NetworkLogHandler(serverConnector);
        Logger logger = new Logger(networkLogHandler);
        logger.log(TraceLevel.Warn, "hello");
        Assert.assertEquals(networkLogHandler.clientEvents.size(), 1);
        ClientEvent event = networkLogHandler.clientEvents.getEvents().get(0);
        Assert.assertTrue(event.getEvent().endsWith("hello"));
        Assert.assertEquals(event.getLevel(), TraceLevel.Warn);
        networkLogHandler.close();
        Assert.assertEquals(networkLogHandler.clientEvents.size(), 0);
    }
}
