package com.applitools.eyes;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.logging.TraceLevel;
import org.testng.annotations.Test;

public class TestLogger {

    @Test
    public void testNetworkLogger() {
        ServerConnector serverConnector = new ServerConnector();
        serverConnector.setProxy(new ProxySettings("http://localhost:8888"));
        NetworkLogHandler networkLogHandler = new NetworkLogHandler(serverConnector);
        Logger logger = new Logger(networkLogHandler);
        logger.log(TraceLevel.Info, "hello");
        networkLogHandler.close();
    }
}
