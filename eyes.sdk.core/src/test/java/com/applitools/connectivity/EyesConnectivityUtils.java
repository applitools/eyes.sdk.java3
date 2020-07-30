package com.applitools.connectivity;

import com.applitools.connectivity.api.HttpClient;

public class EyesConnectivityUtils {
    public static HttpClient getClient(ServerConnector serverConnector) {
        return serverConnector.restClient;
    }
}
