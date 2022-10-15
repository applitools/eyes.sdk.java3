package com.applitools.eyes;

/**
 * The environment in which the application under test is executing.
 */
public class AppEnvironment {

    /**
     * the OS name.
     * former "hostOs".
     */
    private String os;

    /**
     * the OS info.
     * former "hostOsInfo".
     */
    private String osInfo;

    /**
     * the hosting app name.
     * former "hostApp".
     */
    private String hostingApp;

    /**
     * the hosting app info.
     * former "hostAppInfo".
     */
    private String hostingAppInfo;

    /**
     * the device name.
     * former "deviceInfo".
     */
    private String deviceName;

    /**
     * the viewport size.
     */
    private RectangleSize viewportSize;

    /**
     * Creates a new AppEnvironment instance.
     */
    public AppEnvironment() {
    }

    /**
     * Creates a new AppEnvironment instance.
     *
     * @param os          the OS hosting the application under test or {@code null} if                    unknown.
     * @param hostingApp  the application hosting the application under test or {@code null}                    * if unknown.
     * @param displaySize the display size of the application or {@code null} if unknown.
     */
    public AppEnvironment(String os, String hostingApp,
                          RectangleSize displaySize) {
        setOs(os);
        setHostingApp(hostingApp);
        setDisplaySize(displaySize);
    }

    /**
     * Creates a new AppEnvironment instance.
     *
     * @param os             the OS hosting the application under test or {@code null} if                    unknown.
     * @param hostingApp     the application hosting the application under test or {@code null}                    if unknown.
     * @param viewportSize    the display size of the application or {@code null} if unknown.
     * @param deviceName     specifies
     * @param osInfo         the os info
     * @param hostingAppInfo the hosting app info
     */
    public AppEnvironment(String os, String hostingApp, RectangleSize viewportSize, String deviceName, String osInfo, String hostingAppInfo) {
        this.os = os;
        this.hostingApp = hostingApp;
        this.viewportSize = viewportSize;
        this.deviceName = deviceName;
        this.osInfo = osInfo;
        this.hostingAppInfo = hostingAppInfo;
    }

    /**
     * Gets os.
     *
     * @return the OS hosting the application under test or {@code null} if unknown.
     */
    public String getOs() {
        return os;
    }

    /**
     * Sets os.
     *
     * @param os -  the OS hosting the application under test or {@code null} if           unknown.
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * Gets hosting app.
     *
     * @return the application hosting the application under test or {@code null} if unknown.
     */
    @SuppressWarnings("UnusedDeclaration")
    public String getHostingApp() {
        return hostingApp;
    }

    /**
     * Sets hosting app.
     *
     * @param hostingApp -  the application hosting the application under test or {@code null}                   if unknown.
     */
    public void setHostingApp(String hostingApp) {
        this.hostingApp = hostingApp;
    }

    /**
     * Gets display size.
     *
     * @return the display size of the application or {@code null} if unknown.
     */
    public RectangleSize getDisplaySize() {
        return viewportSize;
    }

    /**
     * Sets display size.
     *
     * @param size -  the display size of the application or {@code null} if unknown.
     */
    public void setDisplaySize(RectangleSize size) {
        this.viewportSize = size;
    }

    @Override
    public String toString() {
        return "[os = " + (os == null ? "?" : "'" + os + "'") + " hostingApp = "
                + (hostingApp == null ? "?" : "'" + hostingApp + "'")
                + " displaySize = " + viewportSize + "]";
    }

    /**
     * Gets the device info (not part of test signature)
     *
     * @return the device info
     */
    public String getDeviceInfo() {
        return deviceName;
    }

    /**
     * Sets the device info (not part of test signature)
     *
     * @param deviceInfo the device info
     */
    public void setDeviceInfo(String deviceInfo) {
        this.deviceName =  deviceInfo != null  ? deviceInfo : "Desktop";
    }

    /**
     * Gets the device info (not part of test signature)
     *
     * @return the os info
     */
    public String getOsInfo() {
        return osInfo;
    }

    /**
     * Sets the os info (not part of test signature)
     *
     * @param osInfo the os info
     */
    public void setOsInfo(String osInfo) {
        this.osInfo = osInfo;
    }

    /**
     * Gets the hosting app info info (not part of test signature)
     *
     * @return the hosting app info
     */
    public String getHostingAppInfo() {
        return hostingAppInfo;
    }

    /**
     * Sets the hosting app info info (not part of test signature)
     *
     * @param hostingAppInfo the hosting app
     */
    public void setHostingAppInfo(String hostingAppInfo) {
        this.hostingAppInfo = hostingAppInfo;
    }
}