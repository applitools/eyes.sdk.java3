package com.applitools.connectivity;

import com.applitools.connectivity.api.*;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RunningSession;
import com.applitools.eyes.SessionStartInfo;
import com.applitools.eyes.TaskListener;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.visualgrid.model.RGridResource;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TestServerConnector extends ReportingTestSuite {

    public TestServerConnector(){
        super.setGroupName("core");
    }

    @Mock
    HttpClient restClient;

    @Mock
    ConnectivityTarget endPoint;

    ObjectMapper jsonMapper = new ObjectMapper();

    RunningSession runningSession;

    public static class MockedAsyncRequest extends AsyncRequest {
        final Map<String,String> headers = new HashMap<>();

        public MockedAsyncRequest(Logger logger) {
            super(logger);
        }

        @Override
        public AsyncRequest header(String name, String value) {
            headers.put(name, value);
            return this;
        }

        @Override
        public Future<?> method(String method, AsyncRequestCallback callback, Object data, String contentType, boolean logIfError) {
            return null;
        }
    }

    @BeforeMethod
    public void setMocks() {
        MockitoAnnotations.initMocks(this);
        when(restClient.target(GeneralUtils.getServerUrl())).thenReturn(endPoint);
        when(endPoint.path(anyString())).thenReturn(endPoint);
        when(endPoint.queryParam(anyString(), anyString())).thenReturn(endPoint);

        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        runningSession = new RunningSession();
        runningSession.setUrl("");
        runningSession.setBaselineId("");
        runningSession.setBatchId("");
        runningSession.setId("");
        runningSession.setSessionId("");
    }

    public static ServerConnector getOfflineServerConnector(Request mockedRequest, AsyncRequest mockedAsyncRequest) {
        HttpClient client = mock(HttpClient.class);
        ConnectivityTarget target = mock(ConnectivityTarget.class);
        when(client.target(anyString())).thenReturn(target);
        when(target.path(anyString())).thenReturn(target);
        when(target.queryParam(anyString(), anyString())).thenReturn(target);
        when(target.request(anyString())).thenReturn(mockedRequest);
        when(target.asyncRequest(anyString())).thenReturn(mockedAsyncRequest);
        ServerConnector serverConnector = new ServerConnector();
        serverConnector.updateClient(client);
        return serverConnector;
    }

    @Test
    public void testStartSessionGotIsNew() throws Exception {
        Assert.assertNull(runningSession.getIsNew());
        final Response response = mock(Response.class);
        when(response.getStatusPhrase()).thenReturn("");

        TaskListener<RunningSession> listener = new TaskListener<RunningSession>() {
            @Override
            public void onComplete(RunningSession result) {
                if (runningSession.getIsNew() != null) {
                    Assert.assertEquals(result.getIsNew(), runningSession.getIsNew());
                    return;
                }

                Assert.assertEquals(result.getIsNew().booleanValue(), response.getStatusCode() == HttpStatus.SC_CREATED);
            }

            @Override
            public void onFail() {
                Assert.fail();
            }
        };

        ServerConnector connector = spy(new ServerConnector());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                AsyncRequestCallback callback = invocation.getArgument(2);
                callback.onComplete(response);
                return null;
            }
        }).when(connector).sendLongRequest(ArgumentMatchers.<AsyncRequest>any(), anyString(),
                ArgumentMatchers.<AsyncRequestCallback>any(), anyString(), anyString());
        connector.setAgentId("agent_id");

        when(response.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        runningSession.setIsNew(true);
        when(response.getBodyString()).thenReturn(jsonMapper.writeValueAsString(runningSession));
        connector.startSession(listener, new SessionStartInfo());

        runningSession.setIsNew(false);
        when(response.getBodyString()).thenReturn(jsonMapper.writeValueAsString(runningSession));
        connector.startSession(listener, new SessionStartInfo());

        runningSession.setIsNew(null);
        when(response.getBodyString()).thenReturn(jsonMapper.writeValueAsString(runningSession));
        connector.startSession(listener, new SessionStartInfo());

        when(response.getStatusCode()).thenReturn(HttpStatus.SC_CREATED);
        connector.startSession(listener, new SessionStartInfo());
    }

    @Test
    public void testDownloadResourceRequestHeaders() throws Exception {
        String userAgent = "userAgent";
        String referer = "referer";
        TaskListener<RGridResource> emptyListener = new TaskListener<RGridResource>() {
            @Override
            public void onComplete(RGridResource taskResponse) {}

            @Override
            public void onFail() {}
        };
        ServerConnector connector = new ServerConnector();
        connector.updateClient(restClient);

        // Regular Domain
        URI url = new URI("http://downloadResource.com");
        ConnectivityTarget target = mock(ConnectivityTarget.class);
        MockedAsyncRequest mockedAsyncRequest = new MockedAsyncRequest(new Logger());
        when(restClient.target(url.toString())).thenReturn(target);
        when(target.asyncRequest(anyString())).thenReturn(mockedAsyncRequest);

        connector.downloadResource(url, userAgent, referer, emptyListener);
        Map<String, String> expectedHeaders = new HashMap<>();
        expectedHeaders.put("User-Agent", userAgent);
        expectedHeaders.put("Referer", referer);
        Assert.assertEquals(mockedAsyncRequest.headers, expectedHeaders);

        // Filtered Domain
        URI filteredUrl = new URI("https://fonts.googleapis.com");
        target = mock(ConnectivityTarget.class);
        mockedAsyncRequest = new MockedAsyncRequest(new Logger());
        when(restClient.target(filteredUrl.toString())).thenReturn(target);
        when(target.asyncRequest(anyString())).thenReturn(mockedAsyncRequest);

        connector.downloadResource(filteredUrl, userAgent, referer, emptyListener);
        expectedHeaders = new HashMap<>();
        expectedHeaders.put("Referer", referer);
        Assert.assertEquals(mockedAsyncRequest.headers, expectedHeaders);
    }

    @Test
    public void testNullHeader() {
        HttpClient client = new HttpClientImpl(new Logger(), 0, null);
        ConnectivityTarget target = client.target(GeneralUtils.getServerUrl());
        final Request request = target.request();
        final AsyncRequest asyncRequest = target.asyncRequest();

        Assert.assertThrows(IllegalArgumentException.class, new Assert.ThrowingRunnable() {
            @Override
            public void run() {
                request.header(null, "");
            }
        });

        Assert.assertThrows(IllegalArgumentException.class, new Assert.ThrowingRunnable() {
            @Override
            public void run() {
                asyncRequest.header(null, "");
            }
        });

        request.header("1", null);
        asyncRequest.header("2", null);
    }
}