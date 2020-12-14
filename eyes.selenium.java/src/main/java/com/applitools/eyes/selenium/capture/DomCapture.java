package com.applitools.eyes.selenium.capture;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.EyesSeleniumUtils;
import com.applitools.eyes.selenium.SeleniumEyes;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.rendering.VisualGridEyes;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.eyes.selenium.wrappers.EyesTargetLocator;
import com.applitools.eyes.visualgrid.model.RGridResource;
import com.applitools.utils.EfficientStringReplace;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DomCapture {
    private final String CAPTURE_DOM;
    private final String CAPTURE_DOM_FOR_IE;
    private final String POLL_RESULT;
    private final String POLL_RESULT_FOR_IE;

    private final Phaser cssPhaser = new Phaser(); // Phaser for syncing all callbacks on a single Frame

    private static ServerConnector serverConnector = null;
    private final EyesSeleniumDriver driver;
    private final Logger logger;
    private String cssStartToken;
    private String cssEndToken;
    private final Map<String, CssTreeNode> cssNodesToReplace = Collections.synchronizedMap(new HashMap<String, CssTreeNode>());
    private boolean shouldWaitForPhaser = false;

    private final UserAgent userAgent;

    public DomCapture(SeleniumEyes eyes) {
        serverConnector = eyes.getServerConnector();
        logger = eyes.getLogger();
        driver = (EyesSeleniumDriver) eyes.getDriver();
        userAgent = eyes.getUserAgent();

        try {
            CAPTURE_DOM = GeneralUtils.readToEnd(DomCapture.class.getResourceAsStream("/dom-capture/dist/captureDomAndPoll.js"));
            CAPTURE_DOM_FOR_IE = GeneralUtils.readToEnd(DomCapture.class.getResourceAsStream("/dom-capture/dist/captureDomAndPollForIE.js"));
            POLL_RESULT = GeneralUtils.readToEnd(VisualGridEyes.class.getResourceAsStream("/dom-capture/dist/pollResult.js"));
            POLL_RESULT_FOR_IE = GeneralUtils.readToEnd(VisualGridEyes.class.getResourceAsStream("/dom-capture/dist/pollResultForIE.js"));
        } catch (IOException e) {
            throw new EyesException("Failed getting resources for dom scripts", e);
        }
    }

    public String getPageDom(PositionProvider positionProvider) {
        PositionMemento originalPosition = positionProvider.getState();
        positionProvider.setPosition(Location.ZERO);
        FrameChain originalFC = driver.getFrameChain().clone();
        EyesTargetLocator switchTo = (EyesTargetLocator) driver.switchTo();
        switchTo.defaultContent();
        String baseUrl = (String) driver.executeScript("return document.location.href");
        String dom = getFrameDom(baseUrl, Collections.singletonList(baseUrl));
        if (originalFC != null) {
            switchTo.frames(originalFC);
        }

        try {
            if (shouldWaitForPhaser) {
                cssPhaser.awaitAdvanceInterruptibly(0, 5, TimeUnit.MINUTES);
            }
        } catch (InterruptedException | TimeoutException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }

        shouldWaitForPhaser = false;
        Map<String, String> cssStringsToReplace = new HashMap<>();
        for (String url : cssNodesToReplace.keySet()) {
            cssStringsToReplace.put(url, cssNodesToReplace.get(url).toString());
        }
        String domJson = EfficientStringReplace.efficientStringReplace(cssStartToken, cssEndToken, dom, cssStringsToReplace);
        positionProvider.restoreState(originalPosition);
        return domJson;
    }

    public String getFrameDom(String baseUrl, List<String> framesPath) {
        logger.verbose("Trying to get DOM from driver");
        String domScript = userAgent.isInternetExplorer() ? CAPTURE_DOM_FOR_IE : CAPTURE_DOM;
        String pollingScript = userAgent.isInternetExplorer() ? POLL_RESULT_FOR_IE : POLL_RESULT;

        List<String> missingCssList = new ArrayList<>();
        List<String> missingFramesList = new ArrayList<>();
        List<String> data = new ArrayList<>();
        Separators separators;
        try {
            String scriptResult = EyesSeleniumUtils.runDomScript(logger, driver, userAgent, domScript, null, pollingScript);
            scriptResult = GeneralUtils.parseJsonToObject(scriptResult, String.class);
            separators = parseScriptResult(scriptResult, missingCssList, missingFramesList, data);
        } catch (Exception e) {
            throw new EyesException("Failed running dom capture script", e);
        }

        cssStartToken = separators.cssStartToken;
        cssEndToken = separators.cssEndToken;

        fetchCssFiles(baseUrl, missingCssList, null);

        Map<String, String> framesData = new HashMap<>();
        try {
            framesData = recurseFrames(missingFramesList, framesPath);
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }

        return EfficientStringReplace.efficientStringReplace(separators.iframeStartToken, separators.iframeEndToken, data.get(0), framesData);
    }

    private Separators parseScriptResult(String scriptResult, List<String> missingCssList, List<String> missingFramesList, List<String> data) {
        String[] lines = scriptResult.split("\\r?\\n");
        Separators separators = null;
        try {
            separators = GeneralUtils.parseJsonToObject(lines[0], Separators.class);

            ArrayList<List<String>> blocks = new ArrayList<>();
            blocks.add(missingCssList);
            blocks.add(missingFramesList);
            blocks.add(data);
            int blockIndex = 0;
            int lineIndex = 1;
            do {
                String str = lines[lineIndex++];
                if (separators.separator.equals(str)) {
                    blockIndex++;
                } else {
                    blocks.get(blockIndex).add(str);
                }
            } while (lineIndex < lines.length);
            logger.verbose("missing css count: " + missingCssList.size());
            logger.verbose("missing frames count: " + missingFramesList.size());
        } catch (IOException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        return separators;
    }

    private void fetchCssFiles(final String baseUrl, List<String> cssUrls, final CssTreeNode parentNode) {
        for (final String cssUrl : cssUrls) {
            if (cssUrl == null || cssUrl.isEmpty()) {
                continue;
            }

            final URI uri = resolveUriString(baseUrl, cssUrl);
            if (uri == null) {
                logger.verbose(String.format("Failed resolving url of css %s", cssUrl));
                continue;
            }
            try {
                cssPhaser.register();
                logger.verbose(String.format("Downloading css url %s", uri));
                shouldWaitForPhaser = true;
                serverConnector.downloadResource(uri, userAgent.toString(), baseUrl, new TaskListener<RGridResource>() {
                    @Override
                    public void onComplete(RGridResource resource) {
                        try {
                            logger.verbose(String.format("Css Download Completed. URL: %s", uri));
                            CssTreeNode node = new CssTreeNode(new String(resource.getContent()));
                            node.parse(logger);
                            List<String> importedUrls = node.getImportedUrls();
                            if (!importedUrls.isEmpty()) {
                                logger.verbose(String.format("New urls parsed from %s: %s", uri.toString(), Arrays.toString(importedUrls.toArray())));
                                fetchCssFiles(uri.toString(), importedUrls, node);
                            }

                            if (parentNode != null) {
                                parentNode.addChildNode(cssUrl, node);
                            } else {
                                cssNodesToReplace.put(cssUrl, node);
                            }
                        } catch (Throwable e) {
                            GeneralUtils.logExceptionStackTrace(logger, e);
                        } finally {
                            cssPhaser.arriveAndDeregister();
                            logger.verbose("cssPhaser.arriveAndDeregister(); " + uri);
                            logger.verbose("current missing - " + cssPhaser.getUnarrivedParties());
                        }
                    }

                    @Override
                    public void onFail() {
                        logger.log("This flow can't be reached. Please verify.");
                        cssPhaser.arriveAndDeregister();
                        logger.verbose("cssPhaser.arriveAndDeregister(); " + uri);
                        logger.verbose("current missing - " + cssPhaser.getUnarrivedParties());
                    }
                });
            } catch (Throwable e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
    }

    public Map<String, String> recurseFrames(List<String> missingFramesList, List<String> framesPath) {
        Map<String, String> framesData = new HashMap<>();
        EyesTargetLocator switchTo = (EyesTargetLocator) driver.switchTo();

        FrameChain fc = driver.getFrameChain().clone();
        for (String missingFrameLine : missingFramesList) {
            logger.verbose("Switching to frame line :" + missingFrameLine);
            try {
                String[] missingFrameXpaths = missingFrameLine.split(",");
                for (String missingFrameXpath : missingFrameXpaths) {
                    logger.verbose("switching to specific frame : " + missingFrameXpath);
                    WebElement frame = driver.findElement(By.xpath(missingFrameXpath));
                    logger.verbose("Switched to frame(" + missingFrameXpath + ") with src(" + frame.getAttribute("src") + ")");
                    switchTo.frame(frame);
                }
                String locationAfterSwitch = (String) driver.executeScript("return document.location.href");
                if (framesPath.contains(locationAfterSwitch)) {
                    logger.verbose("Found a bidirectional dependency. not switching to frame");
                    framesData.put(missingFrameLine, "");
                    continue;
                }

                List<String> newFramePath = new ArrayList<>(framesPath);
                newFramePath.add(locationAfterSwitch);
                String result = getFrameDom(locationAfterSwitch, newFramePath);
                framesData.put(missingFrameLine, result);
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
                framesData.put(missingFrameLine, "");
            } finally {
                switchTo.frames(fc);
            }
        }

        return framesData;
    }

    private URI resolveUriString(String baseUrl, String uri) {
        if (uri.toLowerCase().startsWith("data:") || uri.toLowerCase().startsWith("javascript:")) {
            return null;
        }
        try {
            return new URI(baseUrl).resolve(uri);
        } catch (Exception e) {
            logger.log("Error resolving uri:" + uri);
            GeneralUtils.logExceptionStackTrace(logger, e);
            return null;
        }
    }
}