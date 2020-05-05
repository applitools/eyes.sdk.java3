package com.applitools.eyes;

import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RestClient.class)
public class TestServerConnector {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Client restClient;

    @Mock
    WebTarget endPoint;

    ObjectMapper jsonMapper = new ObjectMapper();

    RunningSession runningSession = new RunningSession();

    // Below are the mocks for the long request
    String getRequestUri = "getUri";
    String deleteRequestUri = "deleteUri";

    @Mock
    WebTarget getRequestTarget;
    @Mock
    WebTarget deleteRequestTarget;

    @Mock
    Invocation.Builder firstRequest;
    @Mock
    Invocation.Builder getRequest;
    @Mock
    Invocation.Builder deleteRequest;

    @Mock
    Response firstResponse;
    @Mock
    Response getResponse;
    @Mock
    Response deleteResponse;

    @Before
    public void setMocks() {
        PowerMockito.mockStatic(RestClient.class);
        when(RestClient.buildRestClient(anyInt(), (AbstractProxySettings) any())).thenReturn(restClient);
        when(restClient.target(GeneralUtils.getServerUrl())).thenReturn(endPoint);
        when(endPoint.path(anyString())).thenReturn(endPoint);
        when(endPoint.queryParam(anyString(), any())).thenReturn(endPoint);

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
        when(deleteRequestTarget.queryParam(anyString(), any())).thenReturn(deleteRequestTarget);
        when(deleteRequestTarget.request((String) null)).thenReturn(deleteRequest);
        when(deleteRequest.header(anyString(), any())).thenReturn(deleteRequest);
        when(deleteRequest.method(HttpMethod.DELETE)).thenReturn(deleteResponse);

        // Handle the second request in the long request - GET request
        when(restClient.target(getRequestUri)).thenReturn(getRequestTarget);
        when(getRequestTarget.queryParam(anyString(), any())).thenReturn(getRequestTarget);
        when(getRequestTarget.request((String) null)).thenReturn(getRequest);
        when(getRequest.header(anyString(), any())).thenReturn(getRequest);
        when(getRequest.method(HttpMethod.GET)).thenReturn(getResponse);
        when(getResponse.getHeaderString(HttpHeaders.LOCATION)).thenReturn(deleteRequestUri);
        when(getResponse.getStatus()).thenReturn(Response.Status.CREATED.getStatusCode());

        // Handle the first request in the long request
        when(endPoint.request(anyString())).thenReturn(firstRequest);
        when(firstRequest.header(anyString(), any())).thenReturn(firstRequest);
        when(firstRequest.method(eq(httpMethod), (Entity<?>) any())).thenReturn(firstResponse);
        when(firstResponse.getHeaderString(HttpHeaders.LOCATION)).thenReturn(getRequestUri);
        when(firstResponse.getStatus()).thenReturn(Response.Status.ACCEPTED.getStatusCode());

        return deleteResponse;
    }

    public void verifyLongRequest() {
        verify(restClient).target(getRequestUri);
        verify(getRequestTarget).request((String) null);
        verify(getRequest).method(HttpMethod.GET);

        verify(restClient).target(deleteRequestUri);
        verify(deleteRequestTarget).request((String) null);
        verify(deleteRequest).method(HttpMethod.DELETE);
    }

    @Test
    public void TestStartSessionGotIsNew() throws JsonProcessingException {
        Response response = mockLongRequest(HttpMethod.POST);
        Response.StatusType statusType = mock(Response.StatusType.class);
        when(response.getStatusInfo()).thenReturn(statusType);
        when(statusType.getReasonPhrase()).thenReturn("");

        ServerConnector connector = new ServerConnector();
        connector.setLogger(new Logger());

        when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
        runningSession.setIsNew(true);
        when(response.readEntity(String.class)).thenReturn(jsonMapper.writeValueAsString(runningSession));
        RunningSession session = connector.startSession(new SessionStartInfo());
        Assert.assertTrue(session.getIsNew());
        verifyLongRequest();

        runningSession.setIsNew(false);
        when(response.readEntity(String.class)).thenReturn(jsonMapper.writeValueAsString(runningSession));
        session = connector.startSession(new SessionStartInfo());
        Assert.assertFalse(session.getIsNew());

        runningSession.setIsNew(null);
        when(response.readEntity(String.class)).thenReturn(jsonMapper.writeValueAsString(runningSession));
        session = connector.startSession(new SessionStartInfo());
        Assert.assertFalse(session.getIsNew());

        when(response.getStatus()).thenReturn(Response.Status.CREATED.getStatusCode());
        session = connector.startSession(new SessionStartInfo());
        Assert.assertTrue(session.getIsNew());
    }
}
