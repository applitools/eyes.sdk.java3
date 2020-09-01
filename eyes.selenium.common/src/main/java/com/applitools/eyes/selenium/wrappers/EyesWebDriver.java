package com.applitools.eyes.selenium.wrappers;

import com.applitools.eyes.EyesBase;
import com.applitools.eyes.MobileDeviceInfo;
import com.applitools.eyes.config.Feature;
import com.applitools.eyes.selenium.EyesDriverUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.List;
import java.util.Map;

public abstract class EyesWebDriver implements WebDriver, JavascriptExecutor, TakesScreenshot, SearchContext, HasCapabilities {

    private final EyesBase eyesBase;

    protected EyesWebDriver(EyesBase eyesBase) {
        this.eyesBase = eyesBase;
    }

    public abstract RemoteWebDriver getRemoteWebDriver();
    public abstract WebElement findElement(By by);
    public abstract List<WebElement> findElements(By by);

    public double getDevicePixelRatio() {
        if (eyesBase.getConfiguration().isFeatureActivated(Feature.OPTIMIZE_MOBILE_DEVICES_INFO)) {
            Map<String, MobileDeviceInfo> mobileDevicesInfo = eyesBase.getMobileDeviceInfo();
            String deviceName = EyesDriverUtils.getMobileDeviceName(this);
            for (String name : mobileDevicesInfo.keySet()) {
                if (deviceName.equals(name) || deviceName.equals(name + " GoogleAPI Emulator")) {
                    return mobileDevicesInfo.get(name).getPixelRatio();
                }
            }
        }

        return getDevicePixelRatioInner();
    }

    protected abstract double getDevicePixelRatioInner();
}
