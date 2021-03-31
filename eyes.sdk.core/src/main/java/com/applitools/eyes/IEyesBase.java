package com.applitools.eyes;

import java.net.URI;

public interface IEyesBase {

    String getApiKey();

    URI getServerUrl();

    void setIsDisabled(Boolean isDisabled);

    boolean getIsDisabled();

    String getFullAgentId();

    boolean getIsOpen();

    void setLogHandler(LogHandler logHandler);

    LogHandler getLogHandler();

    Logger getLogger();

    void addProperty(String name, String value);

    void clearProperties();

    TestResults abortIfNotClosed();

    void closeAsync();

    void abortAsync();

    TestResults abort();
}
