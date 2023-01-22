package com.applitools.eyes.playwright;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.locators.BaseOcrRegion;
import com.applitools.eyes.locators.TextRegion;
import com.applitools.eyes.locators.TextRegionSettings;
import com.applitools.eyes.locators.VisualLocatorSettings;
import com.applitools.eyes.playwright.fluent.IPlaywrightCheckSettings;
import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.playwright.universal.mapper.PlaywrightCheckSettingsMapper;
import com.applitools.eyes.playwright.universal.mapper.PlaywrightOCRExtractSettingsDtoMapper;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.universal.CommandExecutor;
import com.applitools.eyes.universal.Reference;
import com.applitools.eyes.playwright.universal.dto.Driver;
import com.applitools.eyes.universal.dto.*;
import com.applitools.eyes.universal.dto.response.CommandCloseResponseDto;
import com.applitools.eyes.universal.mapper.*;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.microsoft.playwright.Page;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Eyes implements IEyesBase {

    /**
     * the runner.
     */
    private EyesRunner runner;

    /**
     * the command executor to execute the commands.
     */
    private CommandExecutor commandExecutor;

    /**
     * the configuration that will be used in eyes related requests.
     */
    private Configuration configuration = new Configuration();

    /**
     * this reference has to be used in eyes related requests (Eyes.check, Eyes.locate, Eyes.extractTextRegions, Eyes.extractText, Eyes.close, Eyes.abort)
     */
    private Reference eyesRef;

    /**
     * is eyes closed, used to check abort after close.
     */
    private boolean isClosed;

    /**
     * the driver reference associated with the test.
     */
    private Driver driver;


    /**
     * Instantiates a new Eyes.
     */
    public Eyes() {
        this(new ClassicRunner());
    }

    /**
     * Instantiates a new Eyes.
     *
     * @param runner0  the runner
     */
    public Eyes(EyesRunner runner0) {
        this.runner = runner0 == null ? new ClassicRunner() : runner0;
        commandExecutor = runner.getCommandExecutor();
    }

    /**
     * Starts a test.
     *
     * @param page   The web driver that controls the browser hosting the application under test.
     * @return the page
     */
    public Page open(Page page) {
        return open(page, configuration.getAppName(), configuration.getTestName());
    }

    /**
     * Starts a test.
     *
     * @param page      The web driver that controls the browser hosting the application under test.
     * @param appName   The name of the application under test.
     * @param testName  The test name. (i.e., the visible part of the document's body) or {@code null} to use the current window's viewport.
     * @return the page
     */
    public Page open(Page page, String appName, String testName) {
        return open(page, appName, testName, configuration.getViewportSize());
    }

    /**
     * Starts a test.
     *
     * @param page          The web driver that controls the browser hosting the application under test.
     * @param appName       The name of the application under test.
     * @param testName      The test name.
     * @param viewportSize  The required browser's viewport size (i.e., the visible part of the document's body) or {@code null} to use the current window's viewport.
     * @return the page
     */
    public Page open(Page page, String appName, String testName,
                          RectangleSize viewportSize) {
        if (getIsDisabled()) {
            return page;
        }

        driver = new Driver(page);
        driver.setApplitoolsRefId(getRefer().ref(page, driver.getRoot()));
        driver.setWebDriverProxy(configuration.getWebDriverProxy());

        configuration.setAppName(appName).setTestName(testName);
        if (viewportSize != null && !viewportSize.isEmpty()) {
            configuration.setViewportSize(new RectangleSize(viewportSize));
        }

        ConfigurationDto configurationDto = ConfigurationMapper
                .toConfigurationDto(configuration, runner.isDontCloseBatches());
        OpenSettingsDto settingsDto = SettingsMapper.toOpenSettingsDto(configuration, runner.isDontCloseBatches());

        eyesRef = commandExecutor.managerOpenEyes(runner.getManagerRef(), driver, settingsDto, configurationDto);
        return page;
    }

    /**
     * Takes multiple screenshots at once (given all <code>ICheckSettings</code> objects are on the same level).
     * @param checkSettings Multiple <code>ICheckSettings</code> object representing different regions in the viewport.
     */
    public void check(ICheckSettings... checkSettings) {
        for (ICheckSettings checkSettings1 : checkSettings) {
            check(checkSettings1);
        }
    }

    /**
     * Check.
     * @param tag           the tag
     * @param checkSettings the check settings
     */
    public void check(String tag, ICheckSettings checkSettings) {
        check(checkSettings.withName(tag));
    }

    /**
     * Check.
     *
     * @param checkSettings the check settings
     */
    public void check(ICheckSettings checkSettings) {
        if (getIsDisabled()) {
            return;
        }

        IPlaywrightCheckSettings playwrightCheckSettings = (checkSettings instanceof IPlaywrightCheckSettings) ? (IPlaywrightCheckSettings) checkSettings : null;
        ArgumentGuard.notNull(playwrightCheckSettings, "checkSettings");

        CheckSettingsDto checkSettingsDto = PlaywrightCheckSettingsMapper.toCheckSettingsDto(playwrightCheckSettings,
                configure(), getRefer(), driver.getRoot());

        checkDto(checkSettingsDto, driver);
    }

    /**
     * Extract text.
     *
     * @param ocrRegions  the ocrRegions.
     * @return the extracted text
     */
    public List<String> extractText(BaseOcrRegion... ocrRegions) {
        if (!getIsOpen()) {
            this.abort();
            throw new EyesException("Eyes not open");
        }

        List<OCRExtractSettingsDto> ocrExtractSettingsDtoList = PlaywrightOCRExtractSettingsDtoMapper
                .toOCRExtractSettingsDtoList(Arrays.asList(ocrRegions), getRefer(), driver.getRoot());
        ConfigurationDto configurationDto = ConfigurationMapper
                .toConfigurationDto(configure(), false);

        return extractTextDto(driver, ocrExtractSettingsDtoList, configurationDto);
    }

    /**
     * Locate text. Formerly known as extractTextRegions.
     *
     * @param textRegionSettings  the image from which the location of the text will be extracted.
     * @return mapping of the located text regions
     */
    public Map<String, List<TextRegion>> extractTextRegions(TextRegionSettings textRegionSettings) {
        OCRSearchSettingsDto ocrSearchSettingsDto = OCRSearchSettingsMapper.toOCRSearchSettingsDto(textRegionSettings);
        ConfigurationDto configurationDto = ConfigurationMapper
                .toConfigurationDto(configure(), false);

        return locateTextDto(driver, ocrSearchSettingsDto, configurationDto);
    }

    /**
     * Locate.
     * @param visualLocatorSettings the locator settings.
     * @return mapping of the region locations.
     */
    public Map<String, List<Region>> locate(VisualLocatorSettings visualLocatorSettings) {
        ArgumentGuard.notNull(visualLocatorSettings, "visualLocatorSettings");

        if (!getIsOpen()) {
            this.abort();
            throw new EyesException("Eyes not open");
        }

        VisualLocatorSettingsDto visualLocatorSettingsDto = VisualLocatorSettingsMapper
                .toVisualLocatorSettingsDto(visualLocatorSettings);

        ConfigurationDto configurationDto = ConfigurationMapper
                .toConfigurationDto(configure(), false);

        return locateDto(driver, visualLocatorSettingsDto, configurationDto);
    }

    /**
     * See {@link #close(boolean)}.
     * {@code throwEx} defaults to {@code true}.
     *
     * @return The test results.
     */
    public TestResults close() {
        return close(true);
    }

    /**
     * Close test results.
     *
     * @param shouldThrowException the should throw exception
     * @return the test results
     */
    public TestResults close(boolean shouldThrowException) {
        if (getIsDisabled()) {
            return null;
        }

        if (!getIsOpen()) {
            throw new EyesException("Eyes not open");
        }

        ConfigurationDto configurationDto = ConfigurationMapper
                .toConfigurationDto(configuration, runner.isDontCloseBatches());
        CloseSettingsDto settings = SettingsMapper.toCloseSettingsDto(getConfiguration(), shouldThrowException);

        List<CommandCloseResponseDto> closeResponse = commandExecutor.close(eyesRef, settings, configurationDto, true);
        this.eyesRef = null;
        isClosed = true;
        getRefer().destroy(driver.getRoot());
        driver = null;
        List<TestResults> testResults = TestResultsMapper.toTestResultsList(closeResponse);
        if (testResults == null)
            return null;
        testResults.forEach(testResults1 -> runner.logSessionResultsAndThrowException(shouldThrowException, testResults1));
        return testResults.get(0);
    }

    public Configuration configure() {
        return this.configuration;
    }

    /**
     * @param appName The name of the application under test.
     */
    public Configuration setAppName(String appName) {
        return configure().setAppName(appName);
    }

    public Configuration setTestName(String testName) {
        return configure().setTestName(testName);
    }

    /**
     * @return The name of the application under test.
     */
    public String getAppName() {
        return configure().getAppName();
    }

    public String getTestName() {
        return configure().getTestName();
    }

    /**
     * Sets the branch in which the baseline for subsequent test runs resides.
     * If the branch does not already exist it will be created under the
     * specified parent branch (see {@link #setParentBranchName}).
     * Changes to the baseline or model of a branch do not propagate to other
     * branches.
     * @param branchName Branch name or {@code null} to specify the default branch.
     */
    public Configuration setBranchName(String branchName) {
        return configure().setBranchName(branchName);
    }

    public Configuration setAgentId(String agentId) {
        return configure().setAgentId(agentId);
    }

    /**
     * @return The current branch (see {@link #setBranchName(String)}).
     */
    public String getBranchName() {
        return configure().getBranchName();
    }

    public String getAgentId() {
        return configure().getAgentId();
    }

    /**
     * Sets the branch under which new branches are created. (see {@link
     * #setBranchName(String)}.
     * @param branchName Branch name or {@code null} to specify the default branch.
     */
    public Configuration setParentBranchName(String branchName) {
        return configure().setParentBranchName(branchName);
    }

    /**
     * @return The name of the current parent branch under which new branches
     * will be created. (see {@link #setParentBranchName(String)}).
     */
    public String getParentBranchName() {
        return configure().getParentBranchName();
    }

    /**
     * Sets the branch under which new branches are created. (see {@link
     * #setBranchName(String)}.
     * @param branchName Branch name or {@code null} to specify the default branch.
     */
    public Configuration setBaselineBranchName(String branchName) {
        return configure().setBaselineBranchName(branchName);
    }

    /**
     * @return The name of the current parent branch under which new branches
     * will be created. (see {@link #setBaselineBranchName(String)}).
     */
    public String getBaselineBranchName() {
        return configure().getBaselineBranchName();
    }

    /**
     * Automatically save differences as a baseline.
     * @param saveDiffs Sets whether to automatically save differences as baseline.
     */
    public Configuration setSaveDiffs(Boolean saveDiffs) {
        return configure().setSaveDiffs(saveDiffs);
    }

    /**
     * Returns whether to automatically save differences as a baseline.
     * @return Whether to automatically save differences as baseline.
     */
    public Boolean getSaveDiffs() {
        return configure().getSaveDiffs();
    }

    /**
     * Sets the maximum time (in ms) a match operation tries to perform a match.
     * @param ms Total number of ms to wait for a match.
     */
    public Configuration setMatchTimeout(int ms) {
        return configure().setMatchTimeout(ms);
    }

    /**
     * @return The maximum time in ms.
     */
    public int getMatchTimeout() {
        return configure().getMatchTimeout();
    }

    /**
     * Set whether or not new tests are saved by default.
     * @param saveNewTests True if new tests should be saved by default. False otherwise.
     */
    public Configuration setSaveNewTests(boolean saveNewTests) {
        return configure().setSaveNewTests(saveNewTests);
    }

    /**
     * @return True if new tests are saved by default.
     */
    public boolean getSaveNewTests() {
        return configure().getSaveNewTests();
    }

    /**
     * Set whether or not failed tests are saved by default.
     * @param saveFailedTests True if failed tests should be saved by default, false otherwise.
     */
    public Configuration setSaveFailedTests(boolean saveFailedTests) {
        return configure().setSaveFailedTests(saveFailedTests);
    }

    /**
     * @return True if failed tests are saved by default.
     */
    public boolean getSaveFailedTests() {
        return configure().getSaveFailedTests();
    }

    /**
     * Sets the batch in which context future tests will run or {@code null}
     * if tests are to run standalone.
     * @param batch The batch info to set.
     */
    public Configuration setBatch(BatchInfo batch) {
        if (getIsDisabled()) {
            return configure();
        }

        return configure().setBatch(batch);
    }

    /**
     * @return The currently set batch info.
     */
    public BatchInfo getBatch() {
        return configure().getBatch();
    }


    /**
     * Updates the match settings to be used for the session.
     * @param defaultMatchSettings The match settings to be used for the session.
     */
    public Configuration setDefaultMatchSettings(ImageMatchSettings
                                                         defaultMatchSettings) {
        ArgumentGuard.notNull(defaultMatchSettings, "defaultMatchSettings");

        return configure().setDefaultMatchSettings(defaultMatchSettings);
    }

    /**
     * @return The match settings used for the session.
     */
    public ImageMatchSettings getDefaultMatchSettings() {
        return configure().getDefaultMatchSettings();
    }

    /**
     * This function is deprecated. Please use {@link #setDefaultMatchSettings} instead.
     * <p>
     * The test-wide match level to use when checking application screenshot
     * with the expected output.
     * @param matchLevel The match level setting.
     * @return The match settings used for the session.
     * @see com.applitools.eyes.MatchLevel
     */
    public Configuration setMatchLevel(MatchLevel matchLevel) {
        Configuration config = configure();
        config.getDefaultMatchSettings().setMatchLevel(matchLevel);
        return config;
    }

    public Configuration setIgnoreDisplacements(boolean isIgnoreDisplacements) {
        return configure().setIgnoreDisplacements(isIgnoreDisplacements);
    }

    public Configuration setAccessibilityValidation(AccessibilitySettings accessibilityValidation) {
        return configure().setAccessibilityValidation(accessibilityValidation);
    }

    public Configuration setUseDom(boolean useDom) {
        return configure().setUseDom(useDom);
    }

    public Configuration setEnablePatterns(boolean enablePatterns) {
        return configure().setEnablePatterns(enablePatterns);
    }

    /**
     * @return The test-wide match level.
     * @deprecated Please use{@link #getDefaultMatchSettings} instead.
     */
    public MatchLevel getMatchLevel() {
        return configure().getDefaultMatchSettings().getMatchLevel();
    }

    public boolean getIgnoreDisplacements() {
        return configure().getIgnoreDisplacements();
    }

    public AccessibilitySettings getAccessibilityValidation() {
        return configure().getAccessibilityValidation();
    }

    public boolean getUseDom() {
        return configure().getUseDom();
    }

    public boolean getEnablePatterns() {
        return configure().getEnablePatterns();
    }

    /**
     * @return Whether to ignore or the blinking caret or not when comparing images.
     */
    public boolean getIgnoreCaret() {
        Boolean ignoreCaret = configure().getDefaultMatchSettings().getIgnoreCaret();
        return ignoreCaret == null || ignoreCaret;
    }

    /**
     * Sets the ignore blinking caret value.
     * @param value The ignore value.
     */
    public Configuration setIgnoreCaret(boolean value) {
        Configuration config = configure();
        configure().getDefaultMatchSettings().setIgnoreCaret(value);
        return config;
    }

    /**
     * Returns the stitching overlap in pixels.
     */
    public int getStitchOverlap() {
        return configure().getStitchOverlap();
    }

    /**
     * Sets the stitching overlap in pixels.
     * @param pixels The width (in pixels) of the overlap.
     */
    public Configuration setStitchOverlap(int pixels) {
        return configure().setStitchOverlap(pixels);
    }

    /**
     * @param hostOS The host OS running the AUT.
     */
    public Configuration setHostOS(String hostOS) {
        Configuration config = configure();
        if (hostOS == null || hostOS.isEmpty()) {
            config.setHostOS(null);
        } else {
            config.setHostOS(hostOS.trim());
        }
        return config;
    }

    /**
     * @return get the host OS running the AUT.
     */
    public String getHostOS() {
        return configure().getHostOS();
    }

    /**
     * @param hostApp The application running the AUT (e.g., Chrome).
     */
    public Configuration setHostApp(String hostApp) {
        Configuration config = configure();
        if (hostApp == null || hostApp.isEmpty()) {
            config.setHostApp(null);
        } else {
            config.setHostApp(hostApp.trim());
        }
        return config;
    }

    /**
     * @return The application name running the AUT.
     */
    public String getHostApp() {
        return configure().getHostApp();
    }

    /**
     * @param baselineName If specified, determines the baseline to compare
     *                     with and disables automatic baseline inference.
     * @deprecated Only available for backward compatibility. See {@link #setBaselineEnvName(String)}.
     */
    public void setBaselineName(String baselineName) {
        setBaselineEnvName(baselineName);
    }

    /**
     * @return The baseline name, if specified.
     * @deprecated Only available for backward compatibility. See {@link #getBaselineEnvName()}.
     */
    @SuppressWarnings("UnusedDeclaration")
    public String getBaselineName() {
        return getBaselineEnvName();
    }

    /**
     * If not {@code null}, determines the name of the environment of the baseline.
     * @param baselineEnvName The name of the baseline's environment.
     */
    public Configuration setBaselineEnvName(String baselineEnvName) {
        Configuration config = configure();
        if (baselineEnvName == null || baselineEnvName.isEmpty()) {
            config.setBaselineEnvName(null);
        } else {
            config.setBaselineEnvName(baselineEnvName.trim());
        }
        return config;
    }

    public Configuration setEnvironmentName(String environmentName) {
        return configure().setEnvironmentName(environmentName);
    }

    /**
     * If not {@code null}, determines the name of the environment of the baseline.
     * @return The name of the baseline's environment, or {@code null} if no such name was set.
     */
    public String getBaselineEnvName() {
        return configure().getBaselineEnvName();
    }

    public String getEnvironmentName() {
        return configure().getEnvironmentName();
    }


    /**
     * If not {@code null} specifies a name for the environment in which the application under test is running.
     * @param envName The name of the environment of the baseline.
     */
    public void setEnvName(String envName) {
        if (envName == null || envName.isEmpty()) {
            configure().setEnvironmentName(null);
        } else {
            configure().setEnvironmentName(envName.trim());
        }
    }

    /**
     * If not {@code null} specifies a name for the environment in which the application under test is running.
     * @return The name of the environment of the baseline, or {@code null} if no such name was set.
     */
    public String getEnvName() {
        return configure().getEnvironmentName();
    }


    /**
     * Superseded by {@link #setHostOS(String)} and {@link #setHostApp(String)}.
     * Sets the OS (e.g., Windows) and application (e.g., Chrome) that host the application under test.
     * @param hostOS  The name of the OS hosting the application under test or {@code null} to auto-detect.
     * @param hostApp The name of the application hosting the application under test or {@code null} to auto-detect.
     */
    @Deprecated
    public void setAppEnvironment(String hostOS, String hostApp) {
        if (getIsDisabled()) {
            return;
        }
        setHostOS(hostOS);
        setHostApp(hostApp);
    }

    public void setConfiguration(Configuration configuration) {
        ArgumentGuard.notNull(configuration, "configuration");
        String apiKey = configuration.getApiKey();
        if (apiKey != null) {
            this.setApiKey(apiKey);
        }
        URI serverUrl = configuration.getServerUrl();
        if (serverUrl != null) {
            this.setServerUrl(serverUrl.toString());
        }
        AbstractProxySettings proxy = configuration.getProxy();
        if (proxy != null) {
            this.setProxy(proxy);
        }
        this.configuration = new Configuration(configuration);
    }

    public Configuration getConfiguration() { return new Configuration(configuration); }

    public void setProxy(AbstractProxySettings proxySettings) { configure().setProxy(proxySettings); }

    @Override
    public String getApiKey() {
        return configure().getApiKey();
    }

    public void setApiKey(String apiKey) { configure().setApiKey(apiKey); }

    @Override
    public URI getServerUrl() {
        return configure().getServerUrl();
    }

    public void setServerUrl(String serverUrl) { configure().setServerUrl(serverUrl); }

    @Override
    public void setIsDisabled(Boolean isDisabled) {
        configure().setIsDisabled(isDisabled);
    }

    @Override
    public Boolean getIsDisabled() {
        return configure().getIsDisabled();
    }

    @Override
    public String getFullAgentId() {
        return configure().getAgentId();
    }

    /**
     * Gets is open.
     * @return Whether a session is open.
     */
    public boolean getIsOpen() {
        return eyesRef != null;
    }

    @Override
    public void setLogHandler(LogHandler logHandler) {
        // For backward compatibility
    }

    @Override
    public LogHandler getLogHandler() {
        return new NullLogHandler();
    }

    @Override
    public Logger getLogger() {
        return new Logger();
    }

    @Override
    public void addProperty(String name, String value) {
        configure().addProperty(name, value);
    }

    @Override
    public void clearProperties() {
        configure().clearProperties();
    }

    @Override
    public TestResults abortIfNotClosed() {
        return abort();
    }

    @Override
    public void closeAsync() {
        if (Boolean.TRUE.equals(getIsDisabled())) {
            return;
        }

        if (!getIsOpen()) {
            throw new EyesException("Eyes not open");
        }

        ConfigurationDto configurationDto = ConfigurationMapper
                .toConfigurationDto(configuration, runner.isDontCloseBatches());
        CloseSettingsDto settings = SettingsMapper.toCloseSettingsDto(getConfiguration(), false);

        List<CommandCloseResponseDto> closeResponse = commandExecutor.close(eyesRef, settings, configurationDto, false);
        this.eyesRef = null;
        isClosed = true;
        getRefer().destroy(driver.getRoot());
        driver = null;
        if (closeResponse != null) {
            List<TestResults> testResults = TestResultsMapper.toTestResultsList(closeResponse);
            testResults.forEach(testResults1 -> runner.logSessionResultsAndThrowException(false, testResults1));
        }
    }

    @Override
    public void abortAsync() {
        if (!isClosed && getIsOpen()) {
            commandExecutor.abort(eyesRef, false);
            this.eyesRef = null;
            getRefer().destroy(driver.getRoot());
            driver = null;
        }
    }

    @Override
    public TestResults abort() {
        if (!isClosed && getIsOpen()) {
            List<CommandCloseResponseDto> abortResponse = commandExecutor.abort(eyesRef, true);
            List<TestResults> testResults = TestResultsMapper.toTestResultsList(abortResponse);
            this.eyesRef = null;
            getRefer().destroy(driver.getRoot());
            driver = null;
            if (testResults != null) {
                return testResults.isEmpty() ? null : testResults.get(0);
            }
        }
        return null;
    }

    public void setImageCut(UnscaledFixedCutProvider unscaledFixedCutProvider) {
        configure().setCutProvider(unscaledFixedCutProvider);
    }

    /**
     * Sets debug screenshots path.
     * @param pathToSave Path where you want to save the debug screenshots.
     */
    public void setDebugScreenshotsPath(String pathToSave) {
        configure().setDebugScreenshotsPath(pathToSave);
    }

    /**
     * Gets debug screenshots path.
     * @return The path where you want to save the debug screenshots.
     */
    public String getDebugScreenshotsPath() {
        return configure().getDebugScreenshotsPath();
    }

    /**
     * Sets debug screenshots prefix.
     * @param prefix The prefix for the screenshots' names.
     */
    public void setDebugScreenshotsPrefix(String prefix) {
        configure().setDebugScreenshotsPrefix(prefix);
    }

    /**
     * Gets debug screenshots prefix.
     * @return The prefix for the screenshots' names.
     */
    public String getDebugScreenshotsPrefix() {
        return configure().getDebugScreenshotsPrefix();
    }

    public void setHideScrollbars(Boolean hideScrollbars) {
        configure().setHideScrollbars(hideScrollbars);
    }

    public void setHideCaret(Boolean hideCaret) {
        configure().setHideCaret(hideCaret);
    }

    public void setStitchMode(StitchMode stitchMode) {
        configure().setStitchMode(stitchMode);
    }

    private void checkDto(CheckSettingsDto checkSettingsDto, ITargetDto driverTargetDto) throws EyesException {
        if (getIsDisabled()) {
            return;
        }

        if (!getIsOpen()) {
            this.abort();
            throw new EyesException("you must call open() before checking");
        }

        ArgumentGuard.notNull(checkSettingsDto, "checkSettings");

        ConfigurationDto configurationDto = ConfigurationMapper
                .toConfigurationDto(configuration, runner.isDontCloseBatches());
        commandExecutor.eyesCheck(eyesRef, driverTargetDto, checkSettingsDto, configurationDto);
    }

    private List<String> extractTextDto(ITargetDto target, List<OCRExtractSettingsDto> settings, ConfigurationDto config) throws EyesException {
        return commandExecutor.extractText(eyesRef, target, settings, config);
    }

    private Map<String, List<TextRegion>> locateTextDto(ITargetDto target, OCRSearchSettingsDto settings, ConfigurationDto config) {
        return commandExecutor.locateText(eyesRef, target, settings, config);
    }

    private Map<String, List<Region>> locateDto(ITargetDto target, VisualLocatorSettingsDto settings, ConfigurationDto config) {
        return commandExecutor.locate(target, settings, config);
    }

    private Refer getRefer() {
        try {
            Method getRef = runner.getClass().getDeclaredMethod("getRefer");
            getRef.setAccessible(true);
            return (Refer) getRef.invoke(runner);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            String message = GeneralUtils.createErrorMessageFromExceptionWithText(e, "got an error trying to getRef!");
            throw new EyesException(message);
        }
    }
}
