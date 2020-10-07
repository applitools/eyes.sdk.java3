package com.applitools.eyes.selenium.capture;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.SeleniumEyes;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.wrappers.EyesTargetLocator;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.eyes.visualgrid.model.RGridResource;
import com.applitools.utils.EfficientStringReplace;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class DomCapture {

    private class TimeoutTask extends TimerTask {
        @Override
        public void run() {
            logger.verbose("Check Timer timeout");
            isCheckTimerTimedOut.set(true);
        }
    }

    private static String CAPTURE_FRAME_SCRIPT;

    private static String CAPTURE_FRAME_SCRIPT_FOR_IE;

    private final Phaser cssPhaser = new Phaser(); // Phaser for syncing all callbacks on a single Frame

    static {
        try {
            CAPTURE_FRAME_SCRIPT = GeneralUtils.readToEnd(DomCapture.class.getResourceAsStream("/captureDomAndPoll.js"));
            CAPTURE_FRAME_SCRIPT += "return __captureDomAndPoll();";
            CAPTURE_FRAME_SCRIPT_FOR_IE = GeneralUtils.readToEnd(DomCapture.class.getResourceAsStream("/captureDomAndPollForIE.js"));
            CAPTURE_FRAME_SCRIPT_FOR_IE += "return __captureDomAndPollForIE();";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final long DOM_EXTRACTION_TIMEOUT = 5 * 60 * 1000;
    private static ServerConnector serverConnector = null;
    private final EyesSeleniumDriver driver;
    private final Logger logger;
    private String cssStartToken;
    private String cssEndToken;
    private final Map<String, CssTreeNode> cssNodesToReplace = Collections.synchronizedMap(new HashMap<String, CssTreeNode>());
    private boolean shouldWaitForPhaser = false;
    private final AtomicBoolean isCheckTimerTimedOut = new AtomicBoolean(false);

    private final UserAgent userAgent;

    public DomCapture(SeleniumEyes eyes) {
        serverConnector = eyes.getServerConnector();
        logger = eyes.getLogger();
        driver = (EyesSeleniumDriver) eyes.getDriver();
        userAgent = eyes.getUserAgent();
    }

    public String getPageDom(PositionProvider positionProvider) {
        PositionMemento originalPosition = positionProvider.getState();
        positionProvider.setPosition(Location.ZERO);
        FrameChain originalFC = driver.getFrameChain().clone();
        String baseUrl = (String) driver.executeScript("return document.location.href");
        String dom = getFrameDom(baseUrl);
        if (originalFC != null) {
            ((EyesTargetLocator) driver.switchTo()).frames(originalFC);
        }

        try {
            if (shouldWaitForPhaser) {
                cssPhaser.awaitAdvanceInterruptibly(0, 30, TimeUnit.SECONDS);
            }
        } catch (InterruptedException | TimeoutException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }


        Map<String, String> cssStringsToReplace = new HashMap<>();
        for (String url : cssNodesToReplace.keySet()) {
            cssStringsToReplace.put(url, cssNodesToReplace.get(url).toString());
        }
        String domJson = EfficientStringReplace.efficientStringReplace(cssStartToken, cssEndToken, dom, cssStringsToReplace);
        positionProvider.restoreState(originalPosition);
        return domJson;
    }

    private String getFrameDom(String baseUrl) {
        logger.verbose("Trying to get DOM from driver");
        Timer timer = new Timer(true);
        timer.schedule(new TimeoutTask(), DOM_EXTRACTION_TIMEOUT);
        try {
            isCheckTimerTimedOut.set(false);
            String resultAsString;
            ScriptResponse.Status status = null;
            ScriptResponse scriptResponse = null;
            do {
                if (userAgent.isInternetExplorer()) {
                    resultAsString = (String) this.driver.executeScript(CAPTURE_FRAME_SCRIPT_FOR_IE);
                } else {
                    resultAsString = (String) this.driver.executeScript(CAPTURE_FRAME_SCRIPT);
                }

                try {
                    scriptResponse = GeneralUtils.parseJsonToObject(resultAsString, ScriptResponse.class);
                    status = scriptResponse.getStatus();
                } catch (IOException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
                Thread.sleep(200);

            } while (status == ScriptResponse.Status.WIP && !isCheckTimerTimedOut.get());
            timer.cancel();

            if (scriptResponse == null || status == ScriptResponse.Status.ERROR) {
                String errorMessage = scriptResponse == null ? "Script response is null" : scriptResponse.getError();
                throw new EyesException("DomCapture Error: " + errorMessage);
            }

            if (isCheckTimerTimedOut.get()) {
                throw new EyesException("DomCapture Timed out");
            }
            String executeScripString = scriptResponse.getValue();

            List<String> missingCssList = new ArrayList<>();
            List<String> missingFramesList = new ArrayList<>();
            List<String> data = new ArrayList<>();

            Separators separators = parseScriptResult(executeScripString, missingCssList, missingFramesList, data);
            cssStartToken = separators.cssStartToken;
            cssEndToken = separators.cssEndToken;

            fetchCssFiles(baseUrl, missingCssList, null);

            Map<String, String> framesData = new HashMap<>();
            try {
                framesData = recurseFrames(missingFramesList);
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }


            return EfficientStringReplace.efficientStringReplace(
                    separators.iframeStartToken, separators.iframeEndToken, data.get(0), framesData);
        } catch (InterruptedException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        finally {
            timer.cancel();
        }
        return "";
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
            e.printStackTrace();
        }
        shouldWaitForPhaser |= !missingCssList.isEmpty();
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
                serverConnector.downloadResource(uri, userAgent.toString(), baseUrl, new TaskListener<RGridResource>() {
                    @Override
                    public void onComplete(RGridResource resource) {
                        try {
                            logger.verbose(String.format("Css Download Completed. URL: %s", uri));
                            CssTreeNode node = new CssTreeNode(new String(resource.getContent()));
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

    private Map<String, String> recurseFrames(List<String> missingFramesList) {
        Map<String, String> framesData = new HashMap<>();
        EyesTargetLocator switchTo = (EyesTargetLocator) driver.switchTo();

        FrameChain fc = driver.getFrameChain().clone();
        for (String missingFrameLine : missingFramesList) {
            logger.verbose("Switching to frame line :" + missingFrameLine);
            String originLocation = (String) driver.executeScript("return document.location.href");
            try {
                String[] missingFrameXpaths = missingFrameLine.split(",");
                for (String missingFrameXpath : missingFrameXpaths) {
                    logger.verbose("switching to specific frame : " + missingFrameXpath);
                    WebElement frame = driver.findElement(By.xpath(missingFrameXpath));
                    logger.verbose("Switched to frame(" + missingFrameXpath + ") with src(" + frame.getAttribute("src") + ")");
                    switchTo.frame(frame);
                }
                String locationAfterSwitch = (String) driver.executeScript("return document.location.href");
                if (locationAfterSwitch.equals(originLocation)) {
                    logger.verbose("Switching to frame failed");
                    framesData.put(missingFrameLine, "");
                    continue;
                }
                String result = getFrameDom(locationAfterSwitch);
                framesData.put(missingFrameLine, result);
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
                framesData.put(missingFrameLine, "");
            }
            switchTo.frames(fc);
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