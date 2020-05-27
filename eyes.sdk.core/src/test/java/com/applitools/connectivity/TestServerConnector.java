package com.applitools.connectivity;

import com.applitools.connectivity.api.*;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RunningSession;
import com.applitools.eyes.SessionStartInfo;
import com.applitools.eyes.TaskListener;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.visualgrid.model.RGridResource;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.HttpMethod;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.*;
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

    RunningSession runningSession = new RunningSession();

    // Below are the mocks for the long request
    String getRequestUri = "getUri";
    String deleteRequestUri = "deleteUri";

    @Mock
    ConnectivityTarget getRequestTarget;
    @Mock
    ConnectivityTarget deleteRequestTarget;

    @Mock
    Request firstRequest;
    @Mock
    Request getRequest;
    @Mock
    Request deleteRequest;

    @Mock
    Response firstResponse;
    @Mock
    Response getResponse;
    @Mock
    Response deleteResponse;

    static class MockedAsyncRequest extends AsyncRequest {
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
        public Future<?> method(String method, AsyncRequestCallback callback, Object data, String contentType) {
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
        runningSession.setUrl("");
        runningSession.setBaselineId("");
        runningSession.setBatchId("");
        runningSession.setId("");
        runningSession.setSessionId("");
    }

    /**
     * Mocking the process of long request
     * @param httpMethod The method of the first request
     * @return The last response from the long request
     */
    public Response mockLongRequest(String httpMethod) {
        // Handle the third request in the long request - DELETE request
        when(restClient.target(deleteRequestUri)).thenReturn(deleteRequestTarget);
        when(deleteRequestTarget.queryParam(anyString(), anyString())).thenReturn(deleteRequestTarget);
        when(deleteRequestTarget.request((String) any())).thenReturn(deleteRequest);
        when(deleteRequest.header(anyString(), anyString())).thenReturn(deleteRequest);
        when(deleteRequest.method(HttpMethod.DELETE, null, null)).thenReturn(deleteResponse);

        // Handle the second request in the long request - GET request
        when(restClient.target(getRequestUri)).thenReturn(getRequestTarget);
        when(getRequestTarget.queryParam(anyString(), anyString())).thenReturn(getRequestTarget);
        when(getRequestTarget.request((String) any())).thenReturn(getRequest);
        when(getRequest.header(anyString(), anyString())).thenReturn(getRequest);
        when(getRequest.method(HttpMethod.GET, null, null)).thenReturn(getResponse);
        when(getResponse.getHeader(HttpHeaders.LOCATION, false)).thenReturn(deleteRequestUri);
        when(getResponse.getStatusCode()).thenReturn(HttpStatus.SC_CREATED);

        // Handle the first request in the long request
        when(endPoint.request((String) any())).thenReturn(firstRequest);
        when(firstRequest.header(anyString(), anyString())).thenReturn(firstRequest);
        when(firstRequest.method(eq(httpMethod), (byte[]) any(), anyString())).thenReturn(firstResponse);
        when(firstResponse.getHeader(HttpHeaders.LOCATION, false)).thenReturn(getRequestUri);
        when(firstResponse.getStatusCode()).thenReturn(HttpStatus.SC_ACCEPTED);

        return deleteResponse;
    }

    public void verifyLongRequest() {
        verify(restClient).target(getRequestUri);
        verify(getRequestTarget).request((String) any());
        verify(getRequest).method(HttpMethod.GET, null, null);

        verify(restClient).target(deleteRequestUri);
        verify(deleteRequestTarget).request((String) any());
        verify(deleteRequest).method(HttpMethod.DELETE, null, null);
    }

    @Test
    public void testStartSessionGotIsNew() throws JsonProcessingException {
        Response response = mockLongRequest(HttpMethod.POST);
        when(response.getStatusPhrase()).thenReturn("");

        ServerConnector connector = new ServerConnector();
        connector.updateClient(restClient);
        connector.setLogger(new Logger());
        connector.setAgentId("agent_id");

        when(response.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        runningSession.setIsNew(true);
        when(response.getBodyString()).thenReturn(jsonMapper.writeValueAsString(runningSession));
        RunningSession session = connector.startSession(new SessionStartInfo());
        Assert.assertTrue(session.getIsNew());
        verifyLongRequest();

        runningSession.setIsNew(false);
        when(response.getBodyString()).thenReturn(jsonMapper.writeValueAsString(runningSession));
        session = connector.startSession(new SessionStartInfo());
        Assert.assertFalse(session.getIsNew());

        runningSession.setIsNew(null);
        when(response.getBodyString()).thenReturn(jsonMapper.writeValueAsString(runningSession));
        session = connector.startSession(new SessionStartInfo());
        Assert.assertFalse(session.getIsNew());

        when(response.getStatusCode()).thenReturn(HttpStatus.SC_CREATED);
        session = connector.startSession(new SessionStartInfo());
        Assert.assertTrue(session.getIsNew());
    }

    @Test
    public void testDownloadResourceRequestHeaders() throws Exception {
        String userAgent = "userAgent";
        String referer = "referer";
        URI url = new URI("http://downloadResource.com");
        ConnectivityTarget target = mock(ConnectivityTarget.class);
        MockedAsyncRequest mockedAsyncRequest = new MockedAsyncRequest(new Logger());

        when(restClient.target(url.toString())).thenReturn(target);
        when(target.asyncRequest(anyString())).thenReturn(mockedAsyncRequest);

        ServerConnector connector = new ServerConnector();
        connector.updateClient(restClient);
        connector.downloadResource(url, userAgent, referer, new TaskListener<RGridResource>() {
            @Override
            public void onComplete(RGridResource taskResponse) {}

            @Override
            public void onFail() {}
        });

        Map<String, String> expectedHeaders = new HashMap<>();
        expectedHeaders.put("User-Agent", userAgent);
        expectedHeaders.put("Referer", referer);
        Assert.assertEquals(mockedAsyncRequest.headers, expectedHeaders);
    }
}
