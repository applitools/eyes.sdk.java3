package com.applitools.eyes.universal.dto;

import com.applitools.eyes.LazyLoadOptions;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CombinedSettingsDto {
    /*
     * OpenSettingsDto
     */
    private Boolean isDisabled;
    private String serverUrl;
    private String apiKey;
    private ProxyDto proxy;
    private String appName;
    private Integer connectionTimeout;
    private Boolean removeSession;
    private String agentId;
    private String testName;
    private String displayName;
    private String userTestId;
    private String sessionType;
    private List<CustomPropertyDto> properties;
    private BatchDto batch;
    private Boolean keepBatchOpen;
    private String environmentName;
    private AppEnvironmentDto environment;
    private String branchName;
    private String parentBranchName;
    private String baselineEnvName;
    private String baselineBranchName;
    private Boolean compareWithParentBranch;
    private Boolean ignoreBaseline;
    private Boolean ignoreGitBranching;
    private Boolean saveDiffs;
    private Integer abortIdleTestTimeout;

    /*
     * CheckSettingsDto
     */
    // screenshot
    private TRegion region;
    private Object frames;
    private Boolean fully;
    private TRegion scrollRootElement;
    private String stitchMode; // "CSS" | "Scroll"
    private Boolean hideScrollBars;
    private Boolean hideCaret;
    private Integer overlap;
    private Integer waitBeforeCapture;
    private LazyLoadOptions lazyLoad;
    private Boolean ignoreDisplacements;
    private NormalizationDto normalization;
    private Map<String, String> debugImages;

    // check
    private String name;
    private String pageId;
    private List<CodedRegionReference> ignoreRegions;
    private List<CodedRegionReference> layoutRegions;
    private List<CodedRegionReference> strictRegions;
    private List<CodedRegionReference> contentRegions;
    private Object floatingRegions;
    private Object accessibilityRegions;
    private AccessibilitySettingsDto accessibilitySettings;
    private String matchLevel;
    private Integer retryTimeout;
    private Boolean sendDom;
    private Boolean useDom;
    private Boolean enablePatterns;
    private Boolean ignoreCaret;
    private Map<String, Object> ufgOptions;
    private Object layoutBreakpoints;
    private Boolean disableBrowserFetching;
    private AutProxyDto autProxy;
    private Map<String, String> hooks;

    /*
     * CloseSettingsDto
     */
    private Boolean throwErr;
    private Boolean updateBaselineIfNew;
    private Boolean updateBaselineIfDifferent;

    public Boolean getDisabled() {
        return isDisabled;
    }

    public void setDisabled(Boolean disabled) {
        isDisabled = disabled;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public ProxyDto getProxy() {
        return proxy;
    }

    public void setProxy(ProxyDto proxy) {
        this.proxy = proxy;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Boolean getRemoveSession() {
        return removeSession;
    }

    public void setRemoveSession(Boolean removeSession) {
        this.removeSession = removeSession;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserTestId() {
        return userTestId;
    }

    public void setUserTestId(String userTestId) {
        this.userTestId = userTestId;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public List<CustomPropertyDto> getProperties() {
        return properties;
    }

    public void setProperties(List<CustomPropertyDto> properties) {
        this.properties = properties;
    }

    public BatchDto getBatch() {
        return batch;
    }

    public void setBatch(BatchDto batch) {
        this.batch = batch;
    }

    public Boolean getKeepBatchOpen() {
        return keepBatchOpen;
    }

    public void setKeepBatchOpen(Boolean keepBatchOpen) {
        this.keepBatchOpen = keepBatchOpen;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public AppEnvironmentDto getEnvironment() {
        return environment;
    }

    public void setEnvironment(AppEnvironmentDto environment) {
        this.environment = environment;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getParentBranchName() {
        return parentBranchName;
    }

    public void setParentBranchName(String parentBranchName) {
        this.parentBranchName = parentBranchName;
    }

    public String getBaselineEnvName() {
        return baselineEnvName;
    }

    public void setBaselineEnvName(String baselineEnvName) {
        this.baselineEnvName = baselineEnvName;
    }

    public String getBaselineBranchName() {
        return baselineBranchName;
    }

    public void setBaselineBranchName(String baselineBranchName) {
        this.baselineBranchName = baselineBranchName;
    }

    public Boolean getCompareWithParentBranch() {
        return compareWithParentBranch;
    }

    public void setCompareWithParentBranch(Boolean compareWithParentBranch) {
        this.compareWithParentBranch = compareWithParentBranch;
    }

    public Boolean getIgnoreBaseline() {
        return ignoreBaseline;
    }

    public void setIgnoreBaseline(Boolean ignoreBaseline) {
        this.ignoreBaseline = ignoreBaseline;
    }

    public Boolean getIgnoreGitBranching() {
        return ignoreGitBranching;
    }

    public void setIgnoreGitBranching(Boolean ignoreGitBranching) {
        this.ignoreGitBranching = ignoreGitBranching;
    }

    public Boolean getSaveDiffs() {
        return saveDiffs;
    }

    public void setSaveDiffs(Boolean saveDiffs) {
        this.saveDiffs = saveDiffs;
    }

    public Integer getAbortIdleTestTimeout() {
        return abortIdleTestTimeout;
    }

    public void setAbortIdleTestTimeout(Integer abortIdleTestTimeout) {
        this.abortIdleTestTimeout = abortIdleTestTimeout;
    }

    public TRegion getRegion() {
        return region;
    }

    public void setRegion(TRegion region) {
        this.region = region;
    }

    public Object getFrames() {
        return frames;
    }

    public void setFrames(Object frames) {
        this.frames = frames;
    }

    public Boolean getFully() {
        return fully;
    }

    public void setFully(Boolean fully) {
        this.fully = fully;
    }

    public TRegion getScrollRootElement() {
        return scrollRootElement;
    }

    public void setScrollRootElement(TRegion scrollRootElement) {
        this.scrollRootElement = scrollRootElement;
    }

    public String getStitchMode() {
        return stitchMode;
    }

    public void setStitchMode(String stitchMode) {
        this.stitchMode = stitchMode;
    }

    public Boolean getHideScrollBars() {
        return hideScrollBars;
    }

    public void setHideScrollBars(Boolean hideScrollBars) {
        this.hideScrollBars = hideScrollBars;
    }

    public Boolean getHideCaret() {
        return hideCaret;
    }

    public void setHideCaret(Boolean hideCaret) {
        this.hideCaret = hideCaret;
    }

    public Integer getOverlap() {
        return overlap;
    }

    public void setOverlap(Integer overlap) {
        this.overlap = overlap;
    }

    public Integer getWaitBeforeCapture() {
        return waitBeforeCapture;
    }

    public void setWaitBeforeCapture(Integer waitBeforeCapture) {
        this.waitBeforeCapture = waitBeforeCapture;
    }

    public LazyLoadOptions getLazyLoad() {
        return lazyLoad;
    }

    public void setLazyLoad(LazyLoadOptions lazyLoad) {
        this.lazyLoad = lazyLoad;
    }

    public Boolean getIgnoreDisplacements() {
        return ignoreDisplacements;
    }

    public void setIgnoreDisplacements(Boolean ignoreDisplacements) {
        this.ignoreDisplacements = ignoreDisplacements;
    }

    public NormalizationDto getNormalization() {
        return normalization;
    }

    public void setNormalization(NormalizationDto normalization) {
        this.normalization = normalization;
    }

    public Map<String, String> getDebugImages() {
        return debugImages;
    }

    public void setDebugImages(Map<String, String> debugImages) {
        this.debugImages = debugImages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public List<CodedRegionReference> getIgnoreRegions() {
        return ignoreRegions;
    }

    public void setIgnoreRegions(List<CodedRegionReference> ignoreRegions) {
        this.ignoreRegions = ignoreRegions;
    }

    public List<CodedRegionReference> getLayoutRegions() {
        return layoutRegions;
    }

    public void setLayoutRegions(List<CodedRegionReference> layoutRegions) {
        this.layoutRegions = layoutRegions;
    }

    public List<CodedRegionReference> getStrictRegions() {
        return strictRegions;
    }

    public void setStrictRegions(List<CodedRegionReference> strictRegions) {
        this.strictRegions = strictRegions;
    }

    public List<CodedRegionReference> getContentRegions() {
        return contentRegions;
    }

    public void setContentRegions(List<CodedRegionReference> contentRegions) {
        this.contentRegions = contentRegions;
    }

    public Object getFloatingRegions() {
        return floatingRegions;
    }

    public void setFloatingRegions(Object floatingRegions) {
        this.floatingRegions = floatingRegions;
    }

    public Object getAccessibilityRegions() {
        return accessibilityRegions;
    }

    public void setAccessibilityRegions(Object accessibilityRegions) {
        this.accessibilityRegions = accessibilityRegions;
    }

    public AccessibilitySettingsDto getAccessibilitySettings() {
        return accessibilitySettings;
    }

    public void setAccessibilitySettings(AccessibilitySettingsDto accessibilitySettings) {
        this.accessibilitySettings = accessibilitySettings;
    }

    public String getMatchLevel() {
        return matchLevel;
    }

    public void setMatchLevel(String matchLevel) {
        this.matchLevel = matchLevel;
    }

    public Integer getRetryTimeout() {
        return retryTimeout;
    }

    public void setRetryTimeout(Integer retryTimeout) {
        this.retryTimeout = retryTimeout;
    }

    public Boolean getSendDom() {
        return sendDom;
    }

    public void setSendDom(Boolean sendDom) {
        this.sendDom = sendDom;
    }

    public Boolean getUseDom() {
        return useDom;
    }

    public void setUseDom(Boolean useDom) {
        this.useDom = useDom;
    }

    public Boolean getEnablePatterns() {
        return enablePatterns;
    }

    public void setEnablePatterns(Boolean enablePatterns) {
        this.enablePatterns = enablePatterns;
    }

    public Boolean getIgnoreCaret() {
        return ignoreCaret;
    }

    public void setIgnoreCaret(Boolean ignoreCaret) {
        this.ignoreCaret = ignoreCaret;
    }

    public Map<String, Object> getUfgOptions() {
        return ufgOptions;
    }

    public void setUfgOptions(Map<String, Object> ufgOptions) {
        this.ufgOptions = ufgOptions;
    }

    public Object getLayoutBreakpoints() {
        return layoutBreakpoints;
    }

    public void setLayoutBreakpoints(Object layoutBreakpoints) {
        this.layoutBreakpoints = layoutBreakpoints;
    }

    public Boolean getDisableBrowserFetching() {
        return disableBrowserFetching;
    }

    public void setDisableBrowserFetching(Boolean disableBrowserFetching) {
        this.disableBrowserFetching = disableBrowserFetching;
    }

    public AutProxyDto getAutProxy() {
        return autProxy;
    }

    public void setAutProxy(AutProxyDto autProxy) {
        this.autProxy = autProxy;
    }

    public Map<String, String> getHooks() {
        return hooks;
    }

    public void setHooks(Map<String, String> hooks) {
        this.hooks = hooks;
    }

    public Boolean getThrowErr() {
        return throwErr;
    }

    public void setThrowErr(Boolean throwErr) {
        this.throwErr = throwErr;
    }

    public Boolean getUpdateBaselineIfNew() {
        return updateBaselineIfNew;
    }

    public void setUpdateBaselineIfNew(Boolean updateBaselineIfNew) {
        this.updateBaselineIfNew = updateBaselineIfNew;
    }

    public Boolean getUpdateBaselineIfDifferent() {
        return updateBaselineIfDifferent;
    }

    public void setUpdateBaselineIfDifferent(Boolean updateBaselineIfDifferent) {
        this.updateBaselineIfDifferent = updateBaselineIfDifferent;
    }
}
