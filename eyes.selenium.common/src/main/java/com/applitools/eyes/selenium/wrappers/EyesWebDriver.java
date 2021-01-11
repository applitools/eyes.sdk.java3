package com.applitools.eyes.selenium.wrappers;

import com.applitools.eyes.EyesBase;
import com.applitools.eyes.Logger;
import com.applitools.eyes.MobileDeviceInfo;
import com.applitools.eyes.config.Feature;
import com.applitools.eyes.selenium.EyesDriverUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.List;
import java.util.Map;

public abstract class EyesWebDriver implements WebDriver, JavascriptExecutor, TakesScreenshot, SearchContext, HasCapabilities {

    protected final Logger logger;
    private final EyesBase eyesBase;

    protected EyesWebDriver(Logger logger, EyesBase eyesBase) {
        this.logger = logger;
        this.eyesBase = eyesBase;
    }

    public abstract RemoteWebDriver getRemoteWebDriver();
    public abstract WebElement findElement(By by);
    public abstract List<WebElement> findElements(By by);

    public double getDevicePixelRatio() {
        if (eyesBase.getConfiguration().isFeatureActivated(Feature.USE_PREDEFINED_DEVICE_INFO)) {
            Map<String, MobileDeviceInfo> mobileDevicesInfo = eyesBase.getMobileDeviceInfo();
            String deviceName = eyesBase.getConfiguration().getDeviceInfo();
            deviceName = deviceName == null ? EyesDriverUtils.getMobileDeviceName(this) : deviceName;
            for (MobileDeviceInfo mobileDeviceInfo : mobileDevicesInfo.values()) {
                for (String name : mobileDeviceInfo.getAliases()) {
                    if (deviceName.equalsIgnoreCase(name)) {
                        return mobileDeviceInfo.getPixelRatio();
                    }
                }
            }
        }

        return getDevicePixelRatioInner();
    }

    protected abstract double getDevicePixelRatioInner();

    public String getTestId() {
        return eyesBase.getTestId();
    }
}
