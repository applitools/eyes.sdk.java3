package com.applitools.eyes.images;

import com.applitools.ICheckSettings;
import com.applitools.ICheckSettingsInternal;
import com.applitools.eyes.*;
import com.applitools.eyes.capture.ScreenshotProvider;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.images.universal.mapper.ImageOCRExtractSettingsDtoMapper;
import com.applitools.eyes.images.universal.mapper.ImageTargetMapper;
import com.applitools.eyes.images.universal.mapper.ImagesCheckSettingsMapper;
import com.applitools.eyes.locators.BaseOcrRegion;
import com.applitools.eyes.locators.TextRegion;
import com.applitools.eyes.locators.TextRegionSettings;
import com.applitools.eyes.locators.VisualLocatorSettings;
import com.applitools.eyes.universal.CommandExecutor;
import com.applitools.eyes.universal.Reference;
import com.applitools.eyes.universal.dto.*;
import com.applitools.eyes.universal.dto.response.CommandCloseResponseDto;
import com.applitools.eyes.universal.mapper.*;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.ClassVersionGetter;
import com.applitools.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Eyes implements IEyesBase {

    /**
     * the title of the test, determined by the tag of the check.
     */
    private String title;

    /**
     * the runner.
     */
    private EyesRunner runner;

    /**
     * the command executor to execute the commands.
     */
    private CommandExecutor commandExecutor;

    /**
     * the configuration that will be used in eyes related requestes.
     */
    private Configuration configuration = new Configuration();

    /**
     * this reference has to be used in eyes related requests (Eyes.check, Eyes.locate, Eyes.extractTextRegions, Eyes.extractText, Eyes.close, Eyes.abort)
     */
    private Reference eyesRef;

    /**
     * inferred. backward compatibility.
     */
    private String inferred;

    /**
     * is eyes closed, used to check abort after close.
     */
    private boolean isClosed;


    /**
     * Instantiates a new Eyes.
     */
    public Eyes() {
        this(new ImageRunner());
    }

    /**
     * Instantiates a new Eyes.
     * @param runner0 the runner
     */
    public Eyes(EyesRunner runner0) {
        this.runner = runner0 == null ? new ImageRunner() : runner0;
        commandExecutor = runner.getCommandExecutor();
    }

    /**
     * Get the base agent id.
     * @return Base agent id.
     */
    public String getBaseAgentId() {
        return "eyes.images.java/" + ClassVersionGetter.CURRENT_VERSION;
    }

    /**
     * Starts a test.
     * @param appName    The name of the application under test.
     * @param testName   The test name.
     * @param viewportSize Determines the resolution used for the baseline.
     *                   {@code null} will automatically grab the resolution from the image.
     */
    public void open(String appName, String testName, RectangleSize viewportSize) {
        if (getIsDisabled()) {
            return;
        }

        configuration.setAppName(appName).setTestName(testName);
        if (viewportSize != null && !viewportSize.isEmpty()) {
            configuration.setViewportSize(new RectangleSize(viewportSize));
        }

        ConfigurationDto configurationDto = ConfigurationMapper
                .toConfigurationDto(configuration, runner.isDontCloseBatches());
        OpenSettingsDto settingsDto = SettingsMapper.toOpenSettingsDto(configuration, runner.isDontCloseBatches());

        eyesRef = commandExecutor.managerOpenEyes(runner.getManagerRef(), null, settingsDto, configurationDto);
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
        title = (tag != null) ? tag : "";
        check(checkSettings.withName(tag));
    }

    /**
     * Check.
     * @param checkSettings the check settings
     */
    public void check(ICheckSettings checkSettings) {
        IImagesCheckTarget imagesCheckTarget = (checkSettings instanceof IImagesCheckTarget) ? (IImagesCheckTarget) checkSettings : null;
        ArgumentGuard.notNull(imagesCheckTarget, "checkSettings");

        CheckSettingsDto checkSettingsDto = ImagesCheckSettingsMapper.toCheckSettingsDto(imagesCheckTarget, configure());
        ImageTargetDto imageTargetDto = ImageTargetMapper.toImageTargetDto(imagesCheckTarget);

        checkDto(checkSettingsDto, imageTargetDto);
    }

    /**
     * Extract text.
     * @param ocrRegions
     * @return
     */
    public List<String> extractText(BaseOcrRegion... ocrRegions) {
        List<OCRExtractSettingsDto> ocrExtractSettingsDtoList = ImageOCRExtractSettingsDtoMapper
                .toOCRExtractSettingsDtoList(Arrays.asList(ocrRegions));
        ConfigurationDto configurationDto = ConfigurationMapper
                .toConfigurationDto(configure(), false);
        OcrRegion ocrRegion = (OcrRegion) Arrays.asList(ocrRegions).get(0);
        ImageTargetDto target = ImageTargetMapper.toImageTargetDto(ocrRegion.getImage(), null);

        return extractTextDto(target, ocrExtractSettingsDtoList, configurationDto);
    }

    /**
     * Locate text. Formerly known as extractTextRegions
     * @param image the image from which the location of the text will be extracted.
     * @return mapping of the located text regions.
     */
    public Map<String, List<TextRegion>> extractTextRegions(TextRegionSettings image) {
        OCRSearchSettingsDto ocrSearchSettingsDto = OCRSearchSettingsMapper.toOCRSearchSettingsDto(image);
        ConfigurationDto configurationDto = ConfigurationMapper
                .toConfigurationDto(configure(), false);
        ITargetDto target = ImageTargetMapper.toImageTargetDto(image.getImage(), null);

        return locateTextDto(target, ocrSearchSettingsDto, configurationDto);
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
            throw new EyesException("you must call open() before locate");
        }

        VisualLocatorSettingsDto visualLocatorSettingsDto = VisualLocatorSettingsMapper
                .toVisualLocatorSettingsDto(visualLocatorSettings);

        ConfigurationDto configurationDto = ConfigurationMapper
                .toConfigurationDto(configure(), false);
        ImageTargetDto target = ImageTargetMapper.toImageTargetDto(visualLocatorSettings);

        return locateDto(target, visualLocatorSettingsDto, configurationDto);
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
        List<TestResults> testResults = TestResultsMapper.toTestResultsList(closeResponse);
        testResults.forEach(testResults1 -> runner.logSessionResultsAndThrowException(shouldThrowException, testResults1));
        return testResults.get(0);
    }

    /**
     * Superseded by {@link #checkImage(java.awt.image.BufferedImage)}.
     */
    @Deprecated
    public void checkWindow(BufferedImage image) {
        checkImage(image);
    }

    /** Superseded by {@link #checkImage(java.awt.image.BufferedImage, String)}.
     */
    @Deprecated
    public void checkWindow(BufferedImage image, String tag) {
        checkImage(image, tag);
    }

    /**
     * See {@link #checkImage(BufferedImage, String)}.
     * {@code tag} defaults to {@code null}.
     */
    public void checkImage(BufferedImage image) {
        checkImage(image, null);
    }

    /**
     * Matches the input image with the next expected image.
     * @param image          The image to perform visual validation for.
     * @param tag            An optional tag to be associated with the validation checkpoint.
     */
    public void checkImage(BufferedImage image, String tag){
        ArgumentGuard.notNull(image, "image");
        title = (tag != null) ? tag : "";
        check(Target.image(image).withName(tag));
    }

    /**
     * See {@link #checkImage(String, String)}.
     * {@code tag} defaults to {@code null}.
     */
    public void checkImage(String path) {
        checkImage(path, null);
    }

    /**
     * Matches the image stored in the input file with the next expected image.
     * @param path The path to the image to check.
     * @param tag  The tag to be associated with the visual checkpoint.
     */
    public void checkImage(String path, String tag) {
        checkImage(ImageUtils.imageFromFile(path), tag);
    }

    /**
     * See {@link #checkImage(byte[], String)}.
     * {@code tag} defaults to {@code null}.
     * @param image The raw png bytes of the image to perform visual validation for.
     */
    public void checkImage(byte[] image) {
        checkImage(image, null);
    }

    /**
     * Matches the input image with the next expected image.
     * See {@link #checkImage(BufferedImage, String)}.
     * @param image The raw png bytes of the image to perform visual validation for.
     * @param tag   An optional tag to be associated with the validation checkpoint.
     */
    public void checkImage(byte[] image, String tag) {
        checkImage(ImageUtils.imageFromBytes(image), tag);
    }

    /**
     * Perform visual validation for a region in a given image.
     * @param image  The image to perform visual validation for.
     * @param region The region to validate within the image.
     * @param tag    An optional tag to be associated with the validation checkpoint.
     */
    public void checkRegion(BufferedImage image, final Region region, String tag) {
        if (getIsDisabled()) {
            return;
        }
        ArgumentGuard.notNull(image, "image");
        ArgumentGuard.notNull(region, "region");

        title = (tag != null) ? tag : "";
        CheckSettingsDto checkSettingsDto = ImagesCheckSettingsMapper.
                toCheckSettingsDto(Target.region(image, region).withName(tag), configure());
        ImageTargetDto imageTargetDto = ImageTargetMapper.toImageTargetDto(image, tag);

        checkDto(checkSettingsDto, imageTargetDto);
    }

    /**
     * Perform visual validation of a region for a given image. Tag is empty and mismatches are not ignored.
     * @param image  The image to perform visual validation for.
     * @param region The region to validate within the image.
     */
    public void checkRegion(BufferedImage image, Region region) {
        checkRegion(image, region, null);
    }

    private Configuration configure() {
        return this.configuration;
    }

    // backward compatibility
    protected ScreenshotProvider getScreenshotProvider() {
        return null;
    }

    // backward compatibility
    public String tryCaptureDom() {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public RectangleSize getViewportSize() {
        return configure().getViewportSize();
    }

    public SessionType getSessionType() {
        return configure().getSessionType();
    }

    public FailureReports getFailureReports() {
        return configure().getFailureReports();
    }

    /**
     * Set the viewport size.
     * @param size The required viewport size.
     */
    public Configuration setViewportSize(RectangleSize size) {
        ArgumentGuard.notNull(size, "size");
        return configure().setViewportSize(new RectangleSize(size.getWidth(), size.getHeight()));
    }

    public Configuration setSessionType(SessionType sessionType) {
        return configure().setSessionType(sessionType);
    }

    public Configuration setFailureReports(FailureReports failureReports) {
        return configure().setFailureReports(failureReports);
    }

    /**
     * Get the screenshot.
     * @return The screenshot.
     */
    public EyesScreenshot getScreenshot(Region targetRegion, ICheckSettingsInternal checkSettingsInternal) {
        return null;
    }

    /**
     * Get the inferred environment.
     * @return Inferred environment.
     */
    protected String getInferredEnvironment() {
        return inferred != null ? inferred : "";
    }

    /**
     * Sets the inferred environment for the test.
     * @param inferred The inferred environment string.
     */
    public void setInferredEnvironment(String inferred) {
        this.inferred = inferred;
    }

    /**
     * Get the title.
     * @return The title.
     */
    protected String getTitle() {
        return title;
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
        final int MIN_MATCH_TIMEOUT = 500;
        if (getIsDisabled()) {
            return configure();
        }

        if ((ms != 0) && (MIN_MATCH_TIMEOUT > ms)) {
            throw new IllegalArgumentException("Match timeout must be set in milliseconds, and must be > " +
                    MIN_MATCH_TIMEOUT);
        }

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

    public Configuration getConfigurationInstance() { return configure(); }

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
        }
    }

    @Override
    public TestResults abort() {
        if (!isClosed && getIsOpen()) {
            List<CommandCloseResponseDto> abortResponse = commandExecutor.abort(eyesRef, true);
            List<TestResults> testResults = TestResultsMapper.toTestResultsList(abortResponse);
            this.eyesRef = null;
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

    private void checkDto(CheckSettingsDto checkSettingsDto, ImageTargetDto imageTargetDto) throws EyesException {
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
        commandExecutor.eyesCheck(eyesRef, imageTargetDto, checkSettingsDto, configurationDto);
    }

//    private TestResults checkAndCloseDto(ImageTargetDto imageTargetDto, CheckSettingsDto checkSettingsDto, CloseSettingsDto closeSettingsDto, Boolean shouldThrowException) {
//        return commandExecutor.eyesCheckAndClose(eyesRef, imageTargetDto, checkSettingsDto, closeSettingsDto, configuration, shouldThrowException)
//    }

    private List<String> extractTextDto(ITargetDto target, List<OCRExtractSettingsDto> settings, ConfigurationDto config) throws EyesException {
        return commandExecutor.extractText(eyesRef, target, settings, config);
    }

    private Map<String, List<TextRegion>> locateTextDto(ITargetDto target, OCRSearchSettingsDto settings, ConfigurationDto config) {
        return commandExecutor.locateText(eyesRef, target, settings, config);
    }

    private Map<String, List<Region>> locateDto(ImageTargetDto target, VisualLocatorSettingsDto settings, ConfigurationDto config) {
        return commandExecutor.locate(target, settings, config);
    }
}
