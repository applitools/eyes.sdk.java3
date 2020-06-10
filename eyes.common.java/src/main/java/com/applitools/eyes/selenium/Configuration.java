package com.applitools.eyes.selenium;

import com.applitools.eyes.ImageMatchSettings;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.visualgrid.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Configuration extends com.applitools.eyes.config.Configuration implements IConfigurationGetter, IConfigurationSetter {

    private static final int DEFAULT_WAIT_BEFORE_SCREENSHOTS = 100;
    private Boolean forceFullPageScreenshot;
    private int waitBeforeScreenshots = DEFAULT_WAIT_BEFORE_SCREENSHOTS;
    private StitchMode stitchMode = StitchMode.SCROLL;
    private boolean hideScrollbars = true;
    private boolean hideCaret = true;
    private boolean isVisualGrid = false;

    //Rendering Configuration
    private Boolean isRenderingConfig = false;

    private List<RenderBrowserInfo> browsersInfo = new ArrayList<>();

    @SuppressWarnings("IncompleteCopyConstructor")
    public Configuration(IConfigurationGetter other) {
        super(other);
        this.forceFullPageScreenshot = other.getForceFullPageScreenshot();
        this.waitBeforeScreenshots = other.getWaitBeforeScreenshots();
        this.stitchMode = other.getStitchMode();
        this.hideScrollbars = other.getHideScrollbars();
        this.hideCaret = other.getHideCaret();
        this.isRenderingConfig = other.isRenderingConfig();
        this.browsersInfo.addAll(other.getBrowsersInfo());
        this.defaultMatchSettings = new ImageMatchSettings(other.getDefaultMatchSettings());
    }

    public Configuration() {
    }

    public Configuration(RectangleSize viewportSize) {
        super();
        ArrayList<RenderBrowserInfo> browsersInfo = new ArrayList<>();
        browsersInfo.add(new RenderBrowserInfo(viewportSize.getWidth(), viewportSize.getHeight(), BrowserType.CHROME, null));
        this.browsersInfo = browsersInfo;
    }

    public Configuration(String testName) {
        this.testName = testName;
    }

    public Configuration(String appName, String testName,
                         RectangleSize viewportSize) {
        super();
        ArrayList<RenderBrowserInfo> browsersInfo = new ArrayList<>();
        if (viewportSize != null) {
            browsersInfo.add(new RenderBrowserInfo(viewportSize.getWidth(), viewportSize.getHeight(), BrowserType.CHROME, null));
        }
        this.browsersInfo = browsersInfo;
        this.testName = testName;
        this.viewportSize = viewportSize;
        this.setAppName(appName);
    }

    @Override
    public Boolean getForceFullPageScreenshot() {
        return forceFullPageScreenshot;
    }

    @Override
    public int getWaitBeforeScreenshots() {
        return waitBeforeScreenshots;
    }

    @Override
    public IConfigurationSetter setWaitBeforeScreenshots(int waitBeforeScreenshots) {
        if (waitBeforeScreenshots <= 0) {
            this.waitBeforeScreenshots = DEFAULT_WAIT_BEFORE_SCREENSHOTS;
        } else {
            this.waitBeforeScreenshots = waitBeforeScreenshots;
        }
        return this;
    }

    @Override
    public StitchMode getStitchMode() {
        return stitchMode;
    }

    @Override
    public IConfigurationSetter setStitchMode(StitchMode stitchMode) {
        this.stitchMode = stitchMode;
        return this;
    }

    @Override
    public boolean getHideScrollbars() {
        return hideScrollbars;
    }

    @Override
    public IConfigurationSetter setHideScrollbars(boolean hideScrollbars) {
        this.hideScrollbars = hideScrollbars;
        return this;
    }

    @Override
    public boolean getHideCaret() {
        return hideCaret;
    }

    @Override
    public IConfigurationSetter setHideCaret(boolean hideCaret) {
        this.hideCaret = hideCaret;
        return this;
    }

    @Override
    public IConfigurationSetter addBrowsers(IRenderingBrowserInfo... browserInfos) {
        for (IRenderingBrowserInfo browserInfo : browserInfos) {
            addBrowser(browserInfo);
        }
        return this;
    }

    private void addBrowser(IRenderingBrowserInfo browserInfo) {
        if (browserInfo instanceof DesktopBrowserInfo) {
            addBrowser((DesktopBrowserInfo) browserInfo);
        } else if(browserInfo instanceof ChromeEmulationInfo) {
            addBrowser((ChromeEmulationInfo) browserInfo);
        } else if(browserInfo instanceof IosDeviceInfo) {
            addBrowser((IosDeviceInfo) browserInfo);
        }
    }

    @Override
    public IConfigurationSetter addBrowser(RenderBrowserInfo renderBrowserInfo) {
        this.browsersInfo.add(renderBrowserInfo);
        return this;
    }

    @Override
    public IConfigurationSetter addBrowser(DesktopBrowserInfo desktopBrowserInfo) {
        this.browsersInfo.add(desktopBrowserInfo.getRenderBrowserInfo());
        return this;
    }

    @Override
    public IConfigurationSetter addBrowser(ChromeEmulationInfo chromeEmulationInfo) {
        RenderBrowserInfo renderBrowserInfo = new RenderBrowserInfo(chromeEmulationInfo);
        this.browsersInfo.add(renderBrowserInfo);
        return this;
    }

    @Override
    public IConfigurationSetter addBrowser(IosDeviceInfo iosDeviceInfo) {
        RenderBrowserInfo renderBrowserInfo = new RenderBrowserInfo(iosDeviceInfo);
        this.browsersInfo.add(renderBrowserInfo);
        return this;
    }

    @Override
    public IConfigurationSetter addBrowser(int width, int height, BrowserType browserType, String baselineEnvName) {
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(width, height, browserType, baselineEnvName);
        addBrowser(browserInfo);
        return this;
    }

    @Override
    public IConfigurationSetter addBrowser(int width, int height, BrowserType browserType) {
        return addBrowser(width, height, browserType, baselineEnvName);
    }

    @Override
    public IConfigurationSetter addDeviceEmulation(DeviceName deviceName, ScreenOrientation orientation) {
        EmulationBaseInfo emulationInfo = new ChromeEmulationInfo(deviceName, orientation);
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(emulationInfo, baselineEnvName);
        this.browsersInfo.add(browserInfo);
        return this;
    }

    @Override
    public IConfigurationSetter addDeviceEmulation(DeviceName deviceName) {
        EmulationBaseInfo emulationInfo = new ChromeEmulationInfo(deviceName, ScreenOrientation.PORTRAIT);
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(emulationInfo, baselineEnvName);
        this.browsersInfo.add(browserInfo);
        return this;
    }

    @Override
    public IConfigurationSetter addDeviceEmulation(DeviceName deviceName, String baselineEnvName) {
        EmulationBaseInfo emulationInfo = new ChromeEmulationInfo(deviceName, ScreenOrientation.PORTRAIT);
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(emulationInfo, baselineEnvName);
        this.browsersInfo.add(browserInfo);
        return this;
    }

    @Override
    public IConfigurationSetter addDeviceEmulation(DeviceName deviceName, ScreenOrientation orientation, String baselineEnvName) {
        EmulationBaseInfo emulationInfo = new ChromeEmulationInfo(deviceName, orientation);
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(emulationInfo, baselineEnvName);
        this.browsersInfo.add(browserInfo);
        return this;
    }

    @Override
    public List<RenderBrowserInfo> getBrowsersInfo() {
        if (browsersInfo != null && !browsersInfo.isEmpty()) {
            return browsersInfo;
        }

        if (this.viewportSize != null) {
            RenderBrowserInfo renderBrowserInfo = new RenderBrowserInfo(this.viewportSize.getWidth(), this.viewportSize.getHeight(), BrowserType.CHROME, baselineEnvName);
            return Collections.singletonList(renderBrowserInfo);
        }
        return browsersInfo;
    }

    @Override
    public IConfigurationSetter setBrowsersInfo(List<RenderBrowserInfo> browsersInfo) {
        this.browsersInfo = browsersInfo;
        return this;
    }

    @Override
    public String getTestName() {
        return testName;
    }

    @Override
    public IConfigurationSetter setTestName(String testName) {
        this.testName = testName;
        return this;
    }

    @Override
    public Configuration cloneConfig() {
        return new Configuration(this);
    }

    @Override
    public RectangleSize getViewportSize() {
        if (isRenderingConfig) {
            RenderBrowserInfo renderBrowserInfo = this.browsersInfo.get(0);
            return new RectangleSize(renderBrowserInfo.getWidth(), renderBrowserInfo.getHeight());
        }
        return super.viewportSize;
    }

    @Override
    public Boolean isForceFullPageScreenshot() {
        return forceFullPageScreenshot;
    }

    @Override
    public IConfigurationSetter setForceFullPageScreenshot(boolean forceFullPageScreenshot) {
        this.forceFullPageScreenshot = forceFullPageScreenshot;
        return this;
    }

    @Override
    public boolean isRenderingConfig() {
        return isRenderingConfig;
    }

    @Override
    public IConfigurationSetter setRenderingConfig(boolean renderingConfig) {
        isRenderingConfig = renderingConfig;
        return this;
    }

    @Override
    public String toString() {
        return super.toString() +
                "\n\tforceFullPageScreenshot = " + forceFullPageScreenshot +
                "\n\twaitBeforeScreenshots = " + waitBeforeScreenshots +
                "\n\tstitchMode = " + stitchMode +
                "\n\thideScrollbars = " + hideScrollbars +
                "\n\thideCaret = " + hideCaret;
    }

    public IConfigurationSetter setIgnoreDisplacements(boolean ignoreDisplacements) {
        super.setIgnoreDisplacements(ignoreDisplacements);
        return this;
    }

    public IConfigurationSetter setIsVisualGrid(boolean isVisualGrid) {
        this.isVisualGrid = isVisualGrid;
        return this;
    }

    @Override
    public boolean isVisualGrid() {
        return isVisualGrid;
    }
}
