package com.applitools.eyes.selenium.capture;

import com.applitools.connectivity.Cookie;
import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.dom.DomScriptUtils;
import com.applitools.eyes.dom.ScriptExecutor;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.Type;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.SeleniumEyes;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.eyes.selenium.wrappers.EyesTargetLocator;
import com.applitools.eyes.visualgrid.model.RGridResource;
import com.applitools.utils.EfficientStringReplace;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DomCapture {
    private static final String APPLITOOLS_DEBUG_RCA = "APPLITOOLS_DEBUG_RCA";
    private final Phaser cssPhaser = new Phaser(); // Phaser for syncing all callbacks on a single Frame

    private static ServerConnector serverConnector = null;
    private final String testId;
    private final EyesSeleniumDriver driver;
    private final Logger logger;
    String cssStartToken;
    String cssEndToken;
    final Map<String, CssTreeNode> cssNodesToReplace = Collections.synchronizedMap(new HashMap<String, CssTreeNode>());
    private boolean shouldWaitForPhaser = false;

    private final UserAgent userAgent;

    public DomCapture(SeleniumEyes eyes) {
        serverConnector = eyes.getServerConnector();
        logger = eyes.getLogger();
        driver = (EyesSeleniumDriver) eyes.getDriver();
        userAgent = eyes.getUserAgent();
        testId = eyes.getTestId();
    }

    public String getPageDom(PositionProvider positionProvider) {
        PositionMemento originalPosition = positionProvider.getState();
        positionProvider.setPosition(Location.ZERO);
        try {
            FrameChain originalFC = driver.getFrameChain().clone();
            EyesTargetLocator switchTo = (EyesTargetLocator) driver.switchTo();
            switchTo.defaultContent();
            String baseUrl = (String) driver.executeScript("return document.location.href");
            String dom = getFrameDom(baseUrl, Collections.singletonList(baseUrl));

            if (originalFC != null) {
                switchTo.frames(originalFC);
            }

            // At this point we have all the frame DOM data (hopefully), but CSS files might still be downloading...
            try {
                if (shouldWaitForPhaser) {
                    cssPhaser.awaitAdvanceInterruptibly(0, 5, TimeUnit.MINUTES);
                }
            } catch (InterruptedException | TimeoutException e) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, Type.DOM_SCRIPT, e, testId);
            }

            shouldWaitForPhaser = false;
            Map<String, String> cssStringsToReplace = new HashMap<>();
            for (String url : cssNodesToReplace.keySet()) {
                try {
                    String escapedCss = new ObjectMapper().writeValueAsString(cssNodesToReplace.get(url).toString());
                    if ("true".equalsIgnoreCase(GeneralUtils.getEnvString(APPLITOOLS_DEBUG_RCA))) {
                        logger.log(testId, Stage.CHECK, Type.DOM_SCRIPT,
                                Pair.of("cssNodeToReplace", url),
                                Pair.of("escapedCSS", escapedCss));
                    }
                    if (escapedCss.startsWith("\"") && escapedCss.endsWith("\"")) {
                        escapedCss = escapedCss.substring(1, escapedCss.length() - 1); // remove quotes
                        if ("true".equalsIgnoreCase(GeneralUtils.getEnvString(APPLITOOLS_DEBUG_RCA))) {
                            logger.log(testId, Stage.CHECK, Type.DOM_SCRIPT,
                                    Pair.of("cssNodeToReplace", url),
                                    Pair.of("escapedCSSAfterRemoveQuotes", escapedCss));
                        }
                    }
                    cssStringsToReplace.put(url, escapedCss);
                } catch (JsonProcessingException e) {
                    GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, Type.DOM_SCRIPT, e);
                }
            }

            String domWithReplacedCss = EfficientStringReplace.efficientStringReplace(cssStartToken, cssEndToken, dom, cssStringsToReplace);
            if ("true".equalsIgnoreCase(GeneralUtils.getEnvString(APPLITOOLS_DEBUG_RCA))) {
                logger.log(testId, Stage.CHECK, Type.DOM_SCRIPT,
                        Pair.of("domBeforeCssReplace", dom),
                        Pair.of("domAfterCssReplace", domWithReplacedCss));
            }
            return domWithReplacedCss;
        } finally {
            positionProvider.restoreState(originalPosition);
        }
    }

    public String getFrameDom(String baseUrl, List<String> framesPath) {
        ScriptExecutor executor = new ScriptExecutor() {
            @Override
            public Object execute(String script) {
                return driver.executeScript(script);
            }
        };

        List<String> missingCssList = new ArrayList<>();
        List<String> missingFramesList = new ArrayList<>();
        List<String> data = new ArrayList<>();
        Separators separators;
        try {
            String scriptResult = DomScriptUtils.runDomCapture(logger, executor, Collections.singleton(testId), userAgent);
            if ("true".equalsIgnoreCase(GeneralUtils.getEnvString(APPLITOOLS_DEBUG_RCA))) {
                logger.log(testId, Stage.CHECK, Type.DOM_SCRIPT, Pair.of("domCaptureScriptResult", scriptResult));
            }
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
            GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, Type.DOM_SCRIPT, e, testId);
        }

        // This pastes the relevant frames DOM data based on the placeholders. IMPORTANT: this does not place missing CSS yet.
        return EfficientStringReplace.efficientStringReplace(separators.iframeStartToken, separators.iframeEndToken, data.get(0), framesData);
    }

    /**
     * This method parses the string returned by the DOM Capture script. The string is internally divided into
     * "blocks", starting with the separators section, followed by missing CSS or frames, followed by the actual DOM.
     *
     * @param scriptResult The string result of running the DOM Capture script.
     * @param missingCssList A container *to be updated in place* for CSS files that could not be retrieved by the script.
     * @param missingFramesList A container *to be updated in place* for frames that could not be retrieved by the script.
     * @param data A container *to be updated in place* for frames that could not be retrieved by the script.
     * @return The separators for the file are the direct returned value. However {@code missingCSSList},
     *  {@code missingFramesList} and {@code data} are filled as a side effect of this method.
     */
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
                // If we encountered a block separator, move on to the next block
                if (separators.separator.equals(str)) {
                    blockIndex++;
                } else {
                    // Add the current line to the current block. Notice that this line updates the missingCss/Frames
                    // list *in place*.
                    blocks.get(blockIndex).add(str);
                }
            } while (lineIndex < lines.length);
            logger.log(testId, Stage.CHECK, Type.DOM_SCRIPT,
                    Pair.of("missingCssCount", missingCssList.size()),
                    Pair.of("missingFramesCount", missingFramesList.size()));
        } catch (IOException e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, Type.DOM_SCRIPT, e, testId);
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
                continue;
            }
            try {
                cssPhaser.register();
                shouldWaitForPhaser = true;
                serverConnector.downloadResource(uri, userAgent.toString(), baseUrl, Collections.<Cookie>emptySet(), new TaskListener<RGridResource>() {
                    @Override
                    public void onComplete(RGridResource resource) {
                        try {
                            String cssString;
                            if (resource != null) { // We only parse the resource if it's not null...
                                if ("true".equalsIgnoreCase(GeneralUtils.getEnvString(APPLITOOLS_DEBUG_RCA))) {
                                    logger.log(testId, Stage.CHECK, Type.PARSE_RESOURCE, Pair.of("downloadUri", uri), Pair.of("resource", resource));
                                }
                                cssString = new String(resource.getContent());
                            } else {
                                if ("true".equalsIgnoreCase(GeneralUtils.getEnvString(APPLITOOLS_DEBUG_RCA))) {
                                    logger.log(testId, Stage.CHECK, Type.PARSE_RESOURCE, Pair.of("downloadUri", uri), Pair.of("resource", "resource is null"));
                                }
                                cssString = "/** CSS resource for: '" +  uri + "' is 'null'. This comment is for debugging purposes **/";
                            }
                            CssTreeNode node = new CssTreeNode(cssString);
                            node.parse(logger);
                            List<String> importedUrls = node.getImportedUrls();
                            if (!importedUrls.isEmpty()) {
                                fetchCssFiles(uri.toString(), importedUrls, node);
                            }

                            if (parentNode != null) {
                                parentNode.addChildNode(cssUrl, node);
                            } else {
                                cssNodesToReplace.put(cssUrl, node);
                            }
                        } catch (Throwable e) {
                            GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, Type.DOM_SCRIPT, e, testId);
                        } finally {
                            cssPhaser.arriveAndDeregister();
                        }
                    }

                    @Override
                    public void onFail() { // We couldn't download the resource
                        cssPhaser.arriveAndDeregister();
                    }
                });
            } catch (Throwable e) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, Type.DOM_SCRIPT, e, testId);
            }
        }
    }

    public Map<String, String> recurseFrames(List<String> missingFramesList, List<String> framesPath) {
        Map<String, String> framesData = new HashMap<>();
        EyesTargetLocator switchTo = (EyesTargetLocator) driver.switchTo();

        FrameChain fc = driver.getFrameChain().clone();
        for (String missingFrameLine : missingFramesList) {
            try {
                // Each frame path is specified in a single line, levels separated by commas
                String[] missingFrameXpaths = missingFrameLine.split(",");
                for (String missingFrameXpath : missingFrameXpaths) {
                    WebElement frame = driver.findElement(By.xpath(missingFrameXpath));
                    switchTo.frame(frame);
                }
                String locationAfterSwitch = (String) driver.executeScript("return document.location.href");
                if (framesPath.contains(locationAfterSwitch)) {
                    framesData.put(missingFrameLine, "");
                    continue;
                }

                List<String> newFramePath = new ArrayList<>(framesPath);
                newFramePath.add(locationAfterSwitch);

                // Starting the DOM capture process for the relevant frame
                String result = getFrameDom(locationAfterSwitch, newFramePath);
                framesData.put(missingFrameLine, result);

            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, Type.DOM_SCRIPT, e, testId);
                framesData.put(missingFrameLine, "");
            } finally {
                // Go back to where we started
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
            GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, Type.DOM_SCRIPT, e, testId);
            return null;
        }
    }
}
