package com.applitools.eyes;

import com.applitools.connectivity.ServerConnector;
import com.applitools.connectivity.api.AsyncRequestCallback;
import com.applitools.connectivity.api.Response;
import com.applitools.eyes.logging.ClientEvent;
import com.applitools.eyes.logging.LogSessionsClientEvents;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import org.apache.http.HttpStatus;

import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

public class NetworkLogHandler extends LogHandler {

    private static final int MAX_EVENTS_SIZE = 100;

    private final ServerConnector serverConnector;
    final LogSessionsClientEvents clientEvents;

    protected NetworkLogHandler(ServerConnector serverConnector) {
        super(TraceLevel.Notice);
        ArgumentGuard.notNull(serverConnector, "serverConnector");
        this.serverConnector = serverConnector;
        this.clientEvents = new LogSessionsClientEvents();
    }

    @Override
    public void open() {}

    @Override
    public void onMessageInner(ClientEvent event) {
        synchronized (clientEvents) {
            clientEvents.addEvent(event);
            if (clientEvents.size() >= MAX_EVENTS_SIZE) {
                sendLogs();
            }
        }
    }

    @Override
    public void close() {
        sendLogs();
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    private void sendLogs() {
        synchronized (clientEvents) {
            if (clientEvents.size() == 0) {
                return;
            }

            final SyncTaskListener<Void> listener = new SyncTaskListener<>(null, "sendLogs");
            serverConnector.sendLogs(new AsyncRequestCallback() {
                @Override
                public void onComplete(Response response) {
                    if (response.getStatusCode() != HttpStatus.SC_OK) {
                        System.out.printf("Failed sending logs. Status code %d%n", response.getStatusCode());
                    }
                    listener.onComplete(null);
                }

                @Override
                public void onFail(Throwable throwable) {
                    System.out.printf("Failed sending logs: %s%n", throwable);
                    listener.onComplete(null);
                }
            }, clientEvents);

           listener.get();
           clientEvents.clear();
        }
    }

    public static void sendSingleLog(ServerConnector serverConnector, TraceLevel level, String message) {
        String currentTime = GeneralUtils.toISO8601DateTime(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
        ClientEvent event = new ClientEvent(currentTime, message, level);
        NetworkLogHandler logHandler = new NetworkLogHandler(serverConnector);
        logHandler.onMessage(event);
        logHandler.close();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof NetworkLogHandler &&
                ((NetworkLogHandler) other).serverConnector.getServerUrl().equals(serverConnector.getServerUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverConnector.getServerUrl());
    }
}
