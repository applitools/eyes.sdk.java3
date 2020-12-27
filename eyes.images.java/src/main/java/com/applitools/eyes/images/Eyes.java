/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes.images;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.config.IConfiguration;
import com.applitools.eyes.events.ValidationResult;
import com.applitools.eyes.exceptions.TestFailedException;
import com.applitools.eyes.fluent.CheckSettings;
import com.applitools.eyes.fluent.ICheckSettingsInternal;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.logging.Type;
import com.applitools.eyes.positioning.RegionProvider;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.triggers.MouseAction;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.ClassVersionGetter;
import com.applitools.utils.ImageUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.image.BufferedImage;
import java.util.Collections;

public class Eyes extends EyesBase implements IConfiguration {

    private String title;
    private EyesImagesScreenshot screenshot;
    private String inferred;
    private Configuration config = new Configuration();

    public Eyes() {
        super(new ClassicRunner());
    }

    /**
     * Get the base agent id.
     * @return Base agent id.
     */
    public String getBaseAgentId() {
        return "eyes.images.java/" + ClassVersionGetter.CURRENT_VERSION;
    }

    public String tryCaptureDom() {
        return null;
    }

    /**
     * Starts a test.
     * @param appName    The name of the application under test.
     * @param testName   The test name.
     * @param dimensions Determines the resolution used for the baseline.
     *                   {@code null} will automatically grab the resolution from the image.
     */
    public void open(String appName, String testName,
                     RectangleSize dimensions) throws EyesException {
        config.setAppName(appName);
        config.setTestName(testName);
        config.setViewportSize(dimensions);
    }

    /**
     * Starts a new test without setting the viewport size of the AUT.
     * @param appName  The name of the application under test.
     * @param testName The test name.
     * @see #open(String, String, RectangleSize)
     */
    public void open(String appName, String testName) {
        config.setAppName(appName);
        config.setTestName(testName);
    }

    public boolean check(String name, ICheckSettings checkSettings) {
        IImagesCheckTarget imagesCheckTarget = (checkSettings instanceof IImagesCheckTarget) ? (IImagesCheckTarget) checkSettings : null;

        if (imagesCheckTarget == null) {
            return false;
        }

        BufferedImage image = imagesCheckTarget.getImage();
        return checkImage_(RegionProvider.NULL_INSTANCE, image, name, checkSettings);
    }

    /**
     * Superseded by {@link #checkImage(java.awt.image.BufferedImage)}.
     */
    @Deprecated
    public boolean checkWindow(BufferedImage image) {
        return checkImage(image);
    }

    /** Superseded by {@link #checkImage(java.awt.image.BufferedImage, String)}.
     */
    @Deprecated
    public boolean checkWindow(BufferedImage image, String tag) {
        return checkImage(image, tag);
    }

    /**
     * Superseded by {@link #checkImage(java.awt.image.BufferedImage, String,
     * boolean)}.
     */
    @Deprecated
    public boolean checkWindow(BufferedImage image, String tag,
                               boolean ignoreMismatch) {
        return checkImage(image, tag, ignoreMismatch);
    }

    /**
     * See {@link #checkImage(BufferedImage, String)}.
     * {@code tag} defaults to {@code null}.
     */
    public boolean checkImage(BufferedImage image) {
        return checkImage(image, null);
    }

    /**
     * See {@link #checkImage(BufferedImage, String, boolean)}.
     * {@code ignoreMismatch} defaults to {@code false}.
     */
    public boolean checkImage(BufferedImage image, String tag) {
        return checkImage(image, tag, false);
    }

    /**
     * Matches the input image with the next expected image.
     * @param image          The image to perform visual validation for.
     * @param tag            An optional tag to be associated with the validation checkpoint.
     * @param ignoreMismatch True if the server should ignore a negative result for the visual validation.
     * @return True if the image matched the expected output, false otherwise.
     * @throws TestFailedException Thrown if a mismatch is detected and immediate failure reports are enabled.
     */
    public boolean checkImage(BufferedImage image, String tag,
                              boolean ignoreMismatch) {
        if (getIsDisabled()) {
            return false;
        }
        ArgumentGuard.notNull(image, "image cannot be null!");
        return checkImage_(RegionProvider.NULL_INSTANCE, image, tag, new CheckSettings(USE_DEFAULT_TIMEOUT));
    }

    /**
     * See {@link #checkImage(String, String)}.
     * {@code tag} defaults to {@code null}.
     */
    public boolean checkImage(String path) {
        return checkImage(path, null);
    }

    /**
     * See {@link #checkImage(String, String, boolean)}.
     * {@code ignoreMismatch} defaults to {@code false}.
     * @param path The path to the image to check.
     * @param tag  The tag to be associated with the visual checkpoint.
     * @return Whether or not the image matched the baseline.
     */
    public boolean checkImage(String path, String tag) {
        return checkImage(path, tag, false);
    }

    /**
     * Matches the image stored in the input file with the next expected image.
     * <p>
     * See {@link #checkImage(BufferedImage, String, boolean)}.
     * @param path           The base64 representation of the image's raw bytes.
     * @param tag            An optional tag to be associated with the validation checkpoint.
     * @param ignoreMismatch True if the server should ignore a negative result for the visual validation.
     * @return Whether or not the image matched the baseline.
     */
    public boolean checkImage(String path, String tag, boolean ignoreMismatch) {
        return checkImage(ImageUtils.imageFromFile(path), tag, ignoreMismatch);
    }

    /**
     * See {@link #checkImage(byte[], String)}.
     * {@code tag} defaults to {@code null}.
     * @param image The raw png bytes of the image to perform visual validation for.
     * @return Whether or not the image matched the baseline.
     */
    public boolean checkImage(byte[] image) {
        return checkImage(image, null);
    }

    /**
     * See {@link #checkImage(byte[], String, boolean)}.
     * {@code ignoreMismatch} defaults to {@code false}.
     * @param image The raw png bytes of the image to perform visual validation for.
     * @param tag   An optional tag to be associated with the validation checkpoint.
     * @return Whether or not the image matched the baseline.
     */
    public boolean checkImage(byte[] image, String tag) {
        return checkImage(image, tag, false);
    }

    /**
     * Matches the input image with the next expected image.
     * See {@link #checkImage(BufferedImage, String, boolean)}.
     * @param image The raw png bytes of the image to perform visual validation for.
     * @param tag   An optional tag to be associated with the validation checkpoint.
     * @return Whether or not the image matched the baseline.
     */
    public boolean checkImage(byte[] image, String tag, boolean ignoreMismatch) {
        return checkImage(ImageUtils.imageFromBytes(image), tag, ignoreMismatch);
    }

    /**
     * Perform visual validation for the current image.
     * @param image          The image to perform visual validation for.
     * @param region         The region to validate within the image.
     * @param tag            An optional tag to be associated with the validation checkpoint.
     * @param ignoreMismatch True if the server should ignore a negative result for the visual validation.
     * @return Whether or not the image matched the baseline.
     * @throws TestFailedException Thrown if a mismatch is detected and immediate failure reports are enabled.
     */
    public boolean checkRegion(BufferedImage image, final Region region, String tag, boolean ignoreMismatch) {
        if (getIsDisabled()) {
            return false;
        }
        ArgumentGuard.notNull(image, "image cannot be null!");
        ArgumentGuard.notNull(region, "region cannot be null!");
        return checkImage_(new RegionProvider() {
            public Region getRegion() {
                return region;
            }
        }, image, tag, new CheckSettings(USE_DEFAULT_TIMEOUT));
    }

    /**
     * Perform visual validation for a region in a given image. Does not
     * ignore mismatches.
     * @param image  The image to perform visual validation for.
     * @param region The region to validate within the image.
     * @param tag    An optional tag to be associated with the validation checkpoint.
     * @throws TestFailedException Thrown if a mismatch is detected and immediate failure reports are enabled.
     */
    public void checkRegion(BufferedImage image, Region region, String tag) {
        checkRegion(image, region, tag, false);
    }

    /**
     * Perform visual validation of a region for a given image. Tag is empty and mismatches are not ignored.
     * @param image  The image to perform visual validation for.
     * @param region The region to validate within the image.
     * @throws TestFailedException Thrown if a mismatch is detected and immediate failure reports are enabled.
     */
    public void checkRegion(BufferedImage image, Region region) {
        checkRegion(image, region, null, false);
    }

    /**
     * Adds a mouse trigger.
     * @param action  Mouse action.
     * @param control The control on which the trigger is activated (context
     *                relative coordinates).
     * @param cursor  The cursor's position relative to the control.
     */
    public void addMouseTrigger(MouseAction action, Region control, Location cursor) {
        addMouseTriggerBase(action, control, cursor);
    }

    /**
     * Adds a keyboard trigger.
     * @param control The control's context-relative region.
     * @param text    The trigger's text.
     */
    public void addTextTrigger(Region control, String text) {
        addTextTriggerBase(control, text);
    }

    /**
     * {@inheritDoc}
     */
    public RectangleSize getViewportSize() {
        return config.getViewportSize();
    }

    public SessionType getSessionType() {
        return config.getSessionType();
    }

    public FailureReports getFailureReports() {
        return config.getFailureReports();
    }

    /**
     * Set the viewport size.
     * @param size The required viewport size.
     */
    public Configuration setViewportSize(RectangleSize size) {
        ArgumentGuard.notNull(size, "size");
        config.setViewportSize(new RectangleSize(size.getWidth(), size.getHeight()));
        return config;
    }

    public Configuration setSessionType(SessionType sessionType) {
        config.setSessionType(sessionType);
        return config;
    }

    public Configuration setFailureReports(FailureReports failureReports) {
        config.setFailureReports(failureReports);
        return config;
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
     * Get the screenshot.
     * @return The screenshot.
     */
    public EyesScreenshot getScreenshot(Region targetRegion, ICheckSettingsInternal checkSettingsInternal) {
        return screenshot;
    }

    /**
     * Get the title.
     * @return The title.
     */
    protected String getTitle() {
        return title;
    }

    /**
     * @param regionProvider The region for which verification will be performed.
     * @param image          The image to perform visual validation for.
     * @param tag            An optional tag to be associated with the validation checkpoint.
     * @param checkSettings  The settings to use when checking the image.
     * @return True if the image matched the expected output, false otherwise.
     */
    private boolean checkImage_(RegionProvider regionProvider,
                                BufferedImage image,
                                String tag,
                                ICheckSettings checkSettings) {
        logger.log(TraceLevel.Info, Collections.singleton(getTestId()), Stage.CHECK, Type.CALLED,
                Pair.of("configuration", getConfiguration()),
                Pair.of("checkSettings", checkSettings));
        if (config.getViewportSize() == null || config.getViewportSize().isEmpty()) {
            setViewportSize(new RectangleSize(image.getWidth(), image.getHeight()));
        }

        if (!isOpen) {
            openBase();
        }

        // We verify that the image is indeed in the correct format.
        image = ImageUtils.normalizeImageType(image);

        CutProvider cutProvider = cutProviderHandler.get();
        if (!(cutProvider instanceof NullCutProvider)) {
            image = cutProvider.cut(image);
            debugScreenshotsProvider.save(image, "cut");
        }

        // Set the screenshot to be verified.
        screenshot = new EyesImagesScreenshot(logger, image);

        // Set the title to be linked to the screenshot.
        title = (tag != null) ? tag : "";

        MatchResult result = checkWindowBase(regionProvider.getRegion((ICheckSettingsInternal)checkSettings), checkSettings.withName(tag), null);
        ValidationResult validationResult = new ValidationResult();
        validationResult.setAsExpected(result.getAsExpected());
        return result.getAsExpected();
    }

    /**
     * @param appName The name of the application under test.
     */
    public Configuration setAppName(String appName) {
        this.config.setAppName(appName);
        return config;
    }

    public Configuration setTestName(String testName) {

        return config.setTestName(testName);
    }

    /**
     * @return The name of the application under test.
     */
    public String getAppName() {
        return config.getAppName();
    }

    public String getTestName() {
        return getConfigurationInstance().getTestName();
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
        this.config.setBranchName(branchName);
        return config;
    }

    public Configuration setAgentId(String agentId) {

        return config;
    }

    /**
     * @return The current branch (see {@link #setBranchName(String)}).
     */
    public String getBranchName() {
        return config.getBranchName();
    }

    public String getAgentId() {
        return null;
    }

    /**
     * Sets the branch under which new branches are created. (see {@link
     * #setBranchName(String)}.
     * @param branchName Branch name or {@code null} to specify the default branch.
     */
    public Configuration setParentBranchName(String branchName) {
        this.config.setParentBranchName(branchName);
        return config;
    }

    /**
     * @return The name of the current parent branch under which new branches
     * will be created. (see {@link #setParentBranchName(String)}).
     */
    public String getParentBranchName() {
        return config.getParentBranchName();
    }

    /**
     * Sets the branch under which new branches are created. (see {@link
     * #setBranchName(String)}.
     * @param branchName Branch name or {@code null} to specify the default branch.
     */
    public Configuration setBaselineBranchName(String branchName) {
        this.config.setBaselineBranchName(branchName);
        return config;
    }

    /**
     * @return The name of the current parent branch under which new branches
     * will be created. (see {@link #setBaselineBranchName(String)}).
     */
    public String getBaselineBranchName() {
        return config.getBaselineBranchName();
    }

    /**
     * Automatically save differences as a baseline.
     * @param saveDiffs Sets whether to automatically save differences as baseline.
     */
    public Configuration setSaveDiffs(Boolean saveDiffs) {
        this.config.setSaveDiffs(saveDiffs);
        return config;
    }

    /**
     * Returns whether to automatically save differences as a baseline.
     * @return Whether to automatically save differences as baseline.
     */
    public Boolean getSaveDiffs() {
        return this.config.getSaveDiffs();
    }

    /**
     * Sets the maximum time (in ms) a match operation tries to perform a match.
     * @param ms Total number of ms to wait for a match.
     */
    public Configuration setMatchTimeout(int ms) {
        final int MIN_MATCH_TIMEOUT = 500;
        if (getIsDisabled()) {
            return config;
        }

        if ((ms != 0) && (MIN_MATCH_TIMEOUT > ms)) {
            throw new IllegalArgumentException("Match timeout must be set in milliseconds, and must be > " +
                    MIN_MATCH_TIMEOUT);
        }

        this.config.setMatchTimeout(ms);

        return config;
    }

    /**
     * @return The maximum time in ms {@link #checkWindowBase
     * (RegionProvider, String, boolean, int)} waits for a match.
     */
    public int getMatchTimeout() {
        return this.config.getMatchTimeout();
    }

    /**
     * Set whether or not new tests are saved by default.
     * @param saveNewTests True if new tests should be saved by default. False otherwise.
     */
    public Configuration setSaveNewTests(boolean saveNewTests) {
        this.config.setSaveNewTests(saveNewTests);
        return config;
    }

    /**
     * @return True if new tests are saved by default.
     */
    public boolean getSaveNewTests() {
        return config.getSaveNewTests();
    }

    /**
     * Set whether or not failed tests are saved by default.
     * @param saveFailedTests True if failed tests should be saved by default, false otherwise.
     */
    public Configuration setSaveFailedTests(boolean saveFailedTests) {
        this.config.setSaveFailedTests(saveFailedTests);
        return config;
    }

    /**
     * @return True if failed tests are saved by default.
     */
    public boolean getSaveFailedTests() {
        return config.getSaveFailedTests();
    }

    /**
     * Sets the batch in which context future tests will run or {@code null}
     * if tests are to run standalone.
     * @param batch The batch info to set.
     */
    public Configuration setBatch(BatchInfo batch) {
        if (getIsDisabled()) {
            return config;
        }

        this.config.setBatch(batch);
        return config;
    }

    /**
     * @return The currently set batch info.
     */
    public BatchInfo getBatch() {
        return config.getBatch();
    }


    /**
     * Updates the match settings to be used for the session.
     * @param defaultMatchSettings The match settings to be used for the session.
     */
    public Configuration setDefaultMatchSettings(ImageMatchSettings
                                                defaultMatchSettings) {
        ArgumentGuard.notNull(defaultMatchSettings, "defaultMatchSettings");
        config.setDefaultMatchSettings(defaultMatchSettings);
        return config;
    }

    /**
     * @return The match settings used for the session.
     */
    public ImageMatchSettings getDefaultMatchSettings() {
        return config.getDefaultMatchSettings();
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
        config.getDefaultMatchSettings().setMatchLevel(matchLevel);
        return config;
    }

    public Configuration setIgnoreDisplacements(boolean isIgnoreDisplacements) {
        this.config.setIgnoreDisplacements(isIgnoreDisplacements);
        return config;
    }

    public Configuration setAccessibilityValidation(AccessibilitySettings accessibilityValidation) {
        return this.config.setAccessibilityValidation(accessibilityValidation);
    }

    public Configuration setUseDom(boolean useDom) {
        return config.setUseDom(useDom);
    }

    public Configuration setEnablePatterns(boolean enablePatterns) {
        return config.setEnablePatterns(enablePatterns);
    }

    /**
     * @return The test-wide match level.
     * @deprecated Please use{@link #getDefaultMatchSettings} instead.
     */
    public MatchLevel getMatchLevel() {
        return config.getDefaultMatchSettings().getMatchLevel();
    }

    public boolean getIgnoreDisplacements() {
        return config.getIgnoreDisplacements();
    }

    public AccessibilitySettings getAccessibilityValidation() {
        return config.getAccessibilityValidation();
    }

    public boolean getUseDom() {
        return config.getUseDom();
    }

    public boolean getEnablePatterns() {
        return config.getEnablePatterns();
    }

    /**
     * @return Whether to ignore or the blinking caret or not when comparing images.
     */
    public boolean getIgnoreCaret() {
        Boolean ignoreCaret = config.getDefaultMatchSettings().getIgnoreCaret();
        return ignoreCaret == null ? true : ignoreCaret;
    }

    /**
     * Sets the ignore blinking caret value.
     * @param value The ignore value.
     */
    public Configuration setIgnoreCaret(boolean value) {
        config.getDefaultMatchSettings().setIgnoreCaret(value);
        return config;
    }

    /**
     * Returns the stitching overlap in pixels.
     */
    public int getStitchOverlap() {
        return config.getStitchOverlap();
    }

    /**
     * Sets the stitching overlap in pixels.
     * @param pixels The width (in pixels) of the overlap.
     */
    public Configuration setStitchOverlap(int pixels) {
        this.config.setStitchOverlap(pixels);
        return config;
    }

    /**
     * @param hostOS The host OS running the AUT.
     */
    public Configuration setHostOS(String hostOS) {
        if (hostOS == null || hostOS.isEmpty()) {
            this.config.setHostOS(null);
        } else {
            config.setHostOS(hostOS.trim());
        }
        return config;
    }

    /**
     * @return get the host OS running the AUT.
     */
    public String getHostOS() {
        return config.getHostOS();
    }

    /**
     * @param hostApp The application running the AUT (e.g., Chrome).
     */
    public Configuration setHostApp(String hostApp) {
        if (hostApp == null || hostApp.isEmpty()) {
            this.config.setHostApp(null);
        } else {
            this.config.setHostApp(hostApp.trim());
        }
        return config;
    }

    /**
     * @return The application name running the AUT.
     */
    public String getHostApp() {
        return config.getHostApp();
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
        if (baselineEnvName == null || baselineEnvName.isEmpty()) {
            this.config.setBaselineEnvName(null);
        } else {
            this.config.setBaselineEnvName(baselineEnvName.trim());
        }
        return config;
    }

    public Configuration setEnvironmentName(String environmentName) {
        return config;
    }

    /**
     * If not {@code null}, determines the name of the environment of the baseline.
     * @return The name of the baseline's environment, or {@code null} if no such name was set.
     */
    public String getBaselineEnvName() {
        return config.getBaselineEnvName();
    }

    public String getEnvironmentName() {
        return null;
    }


    /**
     * If not {@code null} specifies a name for the environment in which the application under test is running.
     * @param envName The name of the environment of the baseline.
     */
    public void setEnvName(String envName) {
        if (envName == null || envName.isEmpty()) {
            this.config.setEnvironmentName(null);
        } else {
            this.config.setEnvironmentName(envName.trim());
        }
    }

    /**
     * If not {@code null} specifies a name for the environment in which the application under test is running.
     * @return The name of the environment of the baseline, or {@code null} if no such name was set.
     */
    public String getEnvName() {
        return config.getEnvironmentName();
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

    public String getApiKey() {
        return super.getApiKey();
    }

    @Override
    protected Configuration getConfigurationInstance() {
        return config;
    }

    public void setConfiguration(Configuration config) {
        this.config = new Configuration(config);
    }
}
