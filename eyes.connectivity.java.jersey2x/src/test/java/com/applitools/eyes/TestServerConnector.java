package com.applitools.eyes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RestClient.class)
public class TestServerConnector {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Client restClient;

    @Mock
    WebTarget target;

    @Mock
    Invocation.Builder request;

    @Mock
    Response response;

    ObjectMapper jsonMapper = new ObjectMapper();

    RunningSession runningSession = new RunningSession();

    @Before
    public void setMocks() {
        PowerMockito.mockStatic(RestClient.class);
        when(RestClient.buildRestClient(anyInt(), (AbstractProxySettings) any())).thenReturn(restClient);
        when(restClient.target((URI) any())).thenReturn(target);
        when(target.path(anyString())).thenReturn(target);
        when(target.queryParam(anyString(), any())).thenReturn(target);
        when(target.request(anyString())).thenReturn(request);
        when(request.header(anyString(), any())).thenReturn(request);
        when(request.method(anyString(), (Entity<?>) any())).thenReturn(response);
        when(response.getHeaderString(anyString())).thenReturn(null);

        Response.StatusType statusType = mock(Response.StatusType.class);
        when(response.getStatusInfo()).thenReturn(statusType);
        when(statusType.getReasonPhrase()).thenReturn("");

        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        runningSession.setUrl("");
        runningSession.setBaselineId("");
        runningSession.setBatchId("");
        runningSession.setId("");
        runningSession.setSessionId("");
    }

    @Test
    public void TestStartSessionGotIsNew() throws JsonProcessingException {
        ServerConnector connector = new ServerConnector();
        connector.setLogger(new Logger());

        when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
        runningSession.setIsNew(true);
        when(response.readEntity(String.class)).thenReturn(jsonMapper.writeValueAsString(runningSession));
        RunningSession session = connector.startSession(new SessionStartInfo());
        Assert.assertTrue(session.getIsNew());

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
