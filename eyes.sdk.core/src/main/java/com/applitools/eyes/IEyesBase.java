package com.applitools.eyes;

import java.net.URI;

public interface IEyesBase {

    String APPLITOOLS_PUBLIC_CLOUD_URL="https://eyes.applitools.com";

    String getApiKey();

    URI getServerUrl();

    void setIsDisabled(Boolean isDisabled);

    Boolean getIsDisabled();

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
