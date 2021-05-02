package com.applitools.connectivity;

import com.applitools.connectivity.api.*;
import com.applitools.eyes.*;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.visualgrid.model.RGridResource;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpStatus;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.HttpMethod;
import java.net.URI;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

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

        public MockedAsyncRequest() {
            super(new Logger());
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

    public static class MockedResponse extends Response {

        private final int statusCode;
        private final Map<String, String> headers;

        @SafeVarargs
        public MockedResponse(int statusCode, Pair<String, String>... headers) {
            super(new Logger());
            this.statusCode =  statusCode;
            this.headers = new HashMap<>();
            if (headers == null || headers.length == 0) {
                return;
            }

            for (Pair<String, String> header : headers) {
                this.headers.put(header.getLeft(), header.getRight());
            }
        }

        @Override
        public int getStatusCode() {
            return statusCode;
        }

        @Override
        public String getStatusPhrase() {
            return null;
        }

        @Override
        public String getHeader(String name, boolean ignoreCase) {
            if (!ignoreCase) {
                return headers.get(name);
            }

            for (String key : headers.keySet()) {
                if (key.equalsIgnoreCase(name)) {
                    return headers.get(key);
                }
            }

            return null;
        }

        @Override
        protected Map<String, String> getHeaders() {
            return headers;
        }

        @Override
        protected void readEntity() {

        }

        @Override
        public void close() {

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
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
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
        MockedAsyncRequest mockedAsyncRequest = new MockedAsyncRequest();
        when(restClient.target(url.toString())).thenReturn(target);
        when(target.asyncRequest(anyString())).thenReturn(mockedAsyncRequest);

        connector.downloadResource(url, userAgent, referer, Collections.<Cookie>emptySet(), emptyListener);
        Map<String, String> expectedHeaders = new HashMap<>();
        expectedHeaders.put("User-Agent", userAgent);
        expectedHeaders.put("Referer", referer);
        Assert.assertEquals(mockedAsyncRequest.headers, expectedHeaders);

        // Filtered Domain
        URI filteredUrl = new URI("https://fonts.googleapis.com");
        target = mock(ConnectivityTarget.class);
        mockedAsyncRequest = new MockedAsyncRequest();
        when(restClient.target(filteredUrl.toString())).thenReturn(target);
        when(target.asyncRequest(anyString())).thenReturn(mockedAsyncRequest);

        connector.downloadResource(filteredUrl, userAgent, referer, Collections.<Cookie>emptySet(), emptyListener);
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

    @Test
    public void testLongRequest() {
        final AtomicLong firstPollingCompletionTime = new AtomicLong();
        final AtomicLong secondPollingCompletionTime = new AtomicLong();
        final AtomicLong thirdPollingCompletionTime = new AtomicLong();
        final AtomicLong lastRequestCompletionTime = new AtomicLong();

        ServerConnector serverConnector = spy(new ServerConnector());
        MockedAsyncRequest request = new MockedAsyncRequest();
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                AsyncRequestCallback callback = invocation.getArgument(2);
                callback.onComplete(new MockedResponse(HttpStatus.SC_ACCEPTED, Pair.of("location", "url1")));
                return null;
            }
        }).when(serverConnector).sendAsyncRequest(eq(request), anyString(), ArgumentMatchers.<AsyncRequestCallback>any(), ArgumentMatchers.<String>isNull(), ArgumentMatchers.<String>isNull());

        final AtomicBoolean wasPolling = new AtomicBoolean(false);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                AsyncRequestCallback callback = invocation.getArgument(0);
                if (!wasPolling.get()) {
                    firstPollingCompletionTime.set(System.currentTimeMillis());
                    wasPolling.set(true);
                    callback.onComplete(new MockedResponse(HttpStatus.SC_OK, Pair.of("Retry-After", "5")));
                } else {
                    secondPollingCompletionTime.set(System.currentTimeMillis());
                    callback.onComplete(new MockedResponse(HttpStatus.SC_OK, Pair.of("location", "url2")));
                }
                return null;
            }
        }).when(serverConnector).sendAsyncRequest(ArgumentMatchers.<RequestPollingCallback>any(), eq("url1"), eq(HttpMethod.GET));

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                thirdPollingCompletionTime.set(System.currentTimeMillis());
                AsyncRequestCallback callback = invocation.getArgument(0);
                callback.onComplete(new MockedResponse(HttpStatus.SC_CREATED, Pair.of("location", "url3")));
                return null;
            }
        }).when(serverConnector).sendAsyncRequest(ArgumentMatchers.<AsyncRequestCallback>any(), eq("url2"), eq(HttpMethod.GET));

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                lastRequestCompletionTime.set(System.currentTimeMillis());
                AsyncRequestCallback callback = invocation.getArgument(0);
                callback.onComplete(new MockedResponse(HttpStatus.SC_OK, Pair.of("finished", "true")));
                return null;
            }
        }).when(serverConnector).sendAsyncRequest(ArgumentMatchers.<AsyncRequestCallback>any(), eq("url3"), eq(HttpMethod.DELETE));

        final SyncTaskListener<Response> listener = new SyncTaskListener<>(new Logger(new StdoutLogHandler()), "test long request");
        serverConnector.sendLongRequest(request, HttpMethod.GET, new AsyncRequestCallback() {
            @Override
            public void onComplete(Response response) {
                listener.onComplete(response);
            }

            @Override
            public void onFail(Throwable throwable) {
                listener.onFail();
            }
        }, null, null);

        Response response = listener.get();
        Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(response.getHeader("finished", false), "true");
        Assert.assertEquals(request.headers.keySet(), new HashSet<>(Arrays.asList("Eyes-Expect-Version", "Eyes-Expect", "Eyes-Date")));
        verify(serverConnector, times(1)).sendAsyncRequest(ArgumentMatchers.<AsyncRequest>any(), anyString(), ArgumentMatchers.<AsyncRequestCallback>any(), ArgumentMatchers.<String>isNull(), ArgumentMatchers.<String>isNull());
        verify(serverConnector, times(4)).sendAsyncRequest(ArgumentMatchers.<AsyncRequestCallback>any(), anyString(), anyString());

        Assert.assertTrue(secondPollingCompletionTime.get() - firstPollingCompletionTime.get() >= 5000);
        Assert.assertTrue(thirdPollingCompletionTime.get() - secondPollingCompletionTime.get() >= 500 &&
                thirdPollingCompletionTime.get() - secondPollingCompletionTime.get() < 1000);
        Assert.assertTrue(lastRequestCompletionTime.get() - thirdPollingCompletionTime.get() < 100);
    }
}