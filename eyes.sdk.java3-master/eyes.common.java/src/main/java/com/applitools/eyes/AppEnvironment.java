package com.applitools.eyes;

/**
 * The environment in which the application under test is executing.
 */
public class AppEnvironment {
    private String inferred;
    private String os;
    private String hostingApp;
    private RectangleSize displaySize;

    /**
     * Creates a new AppEnvironment instance.
     */
    public AppEnvironment() {
    }

    /**
     * Creates a new AppEnvironment instance.
     */
    public AppEnvironment(String inferred) {
        this.inferred = inferred;
    }


    /**
     * Creates a new AppEnvironment instance.
     */
    public AppEnvironment(String os, String hostingApp,
                          RectangleSize displaySize) {
        setOs(os);
        setHostingApp(hostingApp);
        setDisplaySize(displaySize);
    }

    /**
     * Gets the information inferred from the execution environment or {@code null} if no
     * information could be inferred.
     */
    public String getInferred() {
        return inferred;
    }

    /**
     * Sets the inferred environment information.
     */
    public void setInferred(String inferred) {
        this.inferred = inferred;
    }

    /**
     * Gets the OS hosting the application under test or {@code null} if
     * unknown.
     */
    public String getOs() {
        return os;
    }

    /**
     * Sets the OS hosting the application under test or {@code null} if
     * unknown.
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * Gets the application hosting the application under test or {@code null}
     * if unknown.
     */
    @SuppressWarnings("UnusedDeclaration")
    public String getHostingApp() {
        return hostingApp;
    }

    /**
     * Sets the application hosting the application under test or {@code null}
     * if unknown.
     */
    public void setHostingApp(String hostingApp) {
        this.hostingApp = hostingApp;
    }

    /**
     * Gets the display size of the application or {@code null} if unknown.
     */
    public RectangleSize getDisplaySize() {
        return displaySize;
    }

    /**
     * Sets the display size of the application or {@code null} if unknown.
     */
    public void setDisplaySize(RectangleSize size) {
        this.displaySize = size;
    }

    @Override
    public String toString() {
        return "[os = " + (os == null ? "?" : "'" + os + "'") + " hostingApp = "
                + (hostingApp == null ? "?" : "'" + hostingApp + "'")
                + " displaySize = " + displaySize + "]";
    }
}