package com.applitools.eyes.selenium.universal.dto;


import com.applitools.eyes.RectangleSize;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * The environment in which the application under test is executing.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppEnvironmentDto {

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
    private RectangleSizeDto viewportSize;

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
    public RectangleSizeDto getViewportSize() {
        return viewportSize;
    }

    /**
     * Sets display size.
     *
     * @param size -  the display size of the application or {@code null} if unknown.
     */
    public void setViewportSize(RectangleSizeDto size) {
        this.viewportSize = size;
    }

    @Override
    public String toString() {
        return "[os = " + (os == null ? "?" : "'" + os + "'") + " hostingApp = "
                + (hostingApp == null ? "?" : "'" + hostingApp + "'")
                + " displaySize = " + viewportSize + "]";
    }

    /**
     * Gets the device name (not part of test signature)
     *
     * @return the device name
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Sets the device name (not part of test signature)
     *
     * @param deviceName the device name
     */
    public void setDeviceName(String deviceName) {
        this.deviceName =  deviceName != null  ? deviceName : "Desktop";
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