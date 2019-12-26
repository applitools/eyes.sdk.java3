package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.*;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.SeleniumEyes;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.rendering.VisualGridEyes;
import com.applitools.eyes.selenium.wrappers.EyesTargetLocator;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.utils.EfficientStringReplace;
import com.applitools.utils.GeneralUtils;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.css.ECSSVersion;
import com.helger.css.decl.CSSImportRule;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.ICSSTopLevelRule;
import com.helger.css.reader.CSSReader;
import com.helger.css.writer.CSSWriterSettings;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class DomCapture {
    private static String CAPTURE_FRAME_SCRIPT;

    private final Phaser cssPhaser = new Phaser(); // Phaser for syncing all callbacks on a single Frame

    static {
        try {
            CAPTURE_FRAME_SCRIPT = GeneralUtils.readToEnd(DomCapture.class.getResourceAsStream("/captureDomAndPoll.js"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long DOM_EXTRACTION_TIMEOUT = 5 * 60 * 1000;

    private static IServerConnector mServerConnector = null;
    private EyesWebDriver driver;
    private final Logger logger;
    private String cssStartToken;
    private String cssEndToken;
    private Map<String, String> cssData = Collections.synchronizedMap(new HashMap<String, String>());
    private boolean shouldWaitForPhaser = false;
    private AtomicBoolean isCheckTimerTimedOut = new AtomicBoolean(false);
    private Timer timer;

    public DomCapture(SeleniumEyes eyes) {
        mServerConnector = eyes.getServerConnector();
        logger = eyes.getLogger();
        driver = (EyesWebDriver) eyes.getDriver();
    }


    public String getFullWindowDom() {
        long currentTimeMillis = System.currentTimeMillis();
        String domJson = getDom();
        String timeDiff = String.valueOf(System.currentTimeMillis() - currentTimeMillis);
        logger.verbose("getting th DOM took " + timeDiff + " ms");
        return domJson;
    }

    public String getFullWindowDom(PositionProvider positionProvider) {
        PositionMemento originalPosition = positionProvider.getState();
        positionProvider.setPosition(Location.ZERO);
        String domJson = getDom();
        positionProvider.restoreState(originalPosition);
        return domJson;
    }

    private String getDom() {
        FrameChain originalFC = driver.getFrameChain().clone();

        String dom = getFrameDom();

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
        String inlaidString = EfficientStringReplace.efficientStringReplace(cssStartToken, cssEndToken, dom, cssData);
        return inlaidString;
    }

    private String getFrameDom() {
        logger.verbose("Trying to get DOM from driver");
        timer = new Timer(true);
        timer.schedule(new TimeoutTask(), DOM_EXTRACTION_TIMEOUT);
        try {
            isCheckTimerTimedOut.set(false);
            String resultAsString;
            ScriptResponse.Status status = null;
            ScriptResponse scriptResponse = null;
            do {
                resultAsString = (String) this.driver.executeScript(CAPTURE_FRAME_SCRIPT + "return __captureDomAndPoll();");
                try {
                    scriptResponse = GeneralUtils.parseJsonToObject(resultAsString, ScriptResponse.class);
                    status = scriptResponse.getStatus();
                } catch (IOException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
                Thread.sleep(200);

            } while (status == ScriptResponse.Status.WIP && !isCheckTimerTimedOut.get());

            if (status == ScriptResponse.Status.ERROR) {
                throw new EyesException("DomCapture Error: " + scriptResponse.getError());
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

            fetchCssFiles(missingCssList);

            Map<String, String> framesData = null;
            try {
                framesData = recurseFrames(missingFramesList);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //noinspection UnnecessaryLocalVariable
            String inlaidString = EfficientStringReplace.efficientStringReplace(
                    separators.iframeStartToken, separators.iframeEndToken, data.get(0), framesData);
            return inlaidString;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            timer.cancel();
        }
        return "";
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
                String result = getFrameDom();
                framesData.put(missingFrameLine, result);
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
                framesData.put(missingFrameLine, "");
            }
            switchTo.frames(fc);
        }

        return framesData;
    }

    private void fetchCssFiles(List<String> missingCssList) {
        for (final String missingCssUrl : missingCssList) {
            if (missingCssUrl.startsWith("blob:") || missingCssUrl.startsWith("data:")) {
                logger.log("Found blob url continuing - " + missingCssUrl);
                continue;
            }
            if (missingCssUrl.isEmpty()) continue;
            try {
                logger.verbose("DomCapture.fetchCssFiles() downloading");
                final CssTreeNode cssTreeNode = new CssTreeNode(new URL(missingCssUrl));
                downloadCss(cssTreeNode, new IDownloadListener<String>() {
                    @Override
                    public void onDownloadComplete(String downloadedString, String contentType) {
                        try {
                            logger.verbose("DomCapture.onDownloadComplete  - finished");
                            parseCSS(cssTreeNode, downloadedString);
                            if (cssTreeNode.allImportRules != null && !cssTreeNode.allImportRules.isEmpty()) {
                                cssTreeNode.downloadNodeCss();
                            }
                            cssData.put(missingCssUrl, EfficientStringReplace.CleanForJSON(cssTreeNode.calcCss()));
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onDownloadFailed() {
                        logger.verbose("DomCapture.onDownloadFailed");
                        cssData.put(missingCssUrl, "");
                    }
                });

            } catch (Throwable e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
    }

    private Separators parseScriptResult(String scriptResult, List<String> missingCssList, List<String> missingFramesList, List<String> data) {
        String[] lines = scriptResult.split("\\r?\\n");
        Separators separators = null;
        try {
            separators = GeneralUtils.parseJsonToObject(lines[0], Separators.class);

            ArrayList<List<String>> blocks = new ArrayList<List<String>>();
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

    class CssTreeNode {
        URL url;
        String css = "";
        StringBuilder sb = new StringBuilder();
        List<CssTreeNode> decedents = new ArrayList<>();
        ICommonsList<CSSImportRule> allImportRules;
        ICommonsList<CSSStyleRule> styleRules;


        public CssTreeNode(URL url) {
            this.url = url;
        }

        String calcCss() {
            sb.append(css);
            if (decedents != null) {
                for (CssTreeNode decedent : decedents) {
                    sb.append(decedent.calcCss());
                }
            }

            if (styleRules != null) {
                for (CSSStyleRule styleRule : styleRules) {
                    sb.append(styleRule.getAsCSSString(new CSSWriterSettings()));
                }
            }

            return sb.toString();
        }

        public void setCss(String css) {
            this.css = css;
        }

        void downloadNodeCss() {
            if (allImportRules != null) {
                for (CSSImportRule importRule : allImportRules) {
                    final CssTreeNode cssTreeNode;
                    try {
                        cssTreeNode = new CssTreeNode(new URL(importRule.getLocation().getURI()));
                        String uri = importRule.getLocation().getURI();
                        cssTreeNode.setUrl(uri);
                        downloadCss(cssTreeNode, new IDownloadListener<String>() {
                            @Override
                            public void onDownloadComplete(String downloadedString, String contentType) {
                                parseCSS(cssTreeNode, EfficientStringReplace.CleanForJSON(downloadedString));
                                if (!cssTreeNode.allImportRules.isEmpty()) {
                                    cssTreeNode.downloadNodeCss();

                                }
                            }

                            @Override
                            public void onDownloadFailed() {
                                logger.verbose("Download Failed");
                            }
                        });
                        decedents.add(cssTreeNode);
                    } catch (Throwable e) {
                        GeneralUtils.logExceptionStackTrace(logger, e);
                    }

                }
            }
        }

        public void setUrl(String url) {

            boolean absolute = false;
            try {
                url = URLEncoder.encode(url, "UTF-8");
                absolute = new URI(url).isAbsolute();
                this.url = absolute ? new URL(url) : new URL(url);
            } catch (UnsupportedEncodingException | URISyntaxException | MalformedURLException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }


        public void setAllImportRules(ICommonsList<CSSImportRule> allImportRules) {
            this.allImportRules = allImportRules;
        }

        public void setAllStyleRules(ICommonsList<CSSStyleRule> allStyleRules) {
            this.styleRules = allStyleRules;
        }


    }

    private void downloadCss(final CssTreeNode node, final IDownloadListener<String> listener) {
        cssPhaser.register();
        logger.verbose("Given URL to download: " + node.url);
        mServerConnector.downloadString(node.url, false, new IDownloadListener<String>() {
            @Override
            public void onDownloadComplete(String downloadedString, String contentType) {
                try {
                    logger.verbose("Download Complete");
                    node.setCss(downloadedString);
                    listener.onDownloadComplete(downloadedString, "String");

                } catch (Throwable e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                } finally {
                    cssPhaser.arriveAndDeregister();
                    logger.verbose("cssPhaser.arriveAndDeregister(); " + node.url);
                    logger.verbose("current missing - " + cssPhaser.getUnarrivedParties());
                }
            }

            @Override
            public void onDownloadFailed() {
                listener.onDownloadComplete("", "String");
                cssPhaser.arriveAndDeregister();
                logger.verbose("Download Failed");
                logger.verbose("cssPhaser.arriveAndDeregister(); " + node.url);
                logger.verbose("current missing  - " + cssPhaser.getUnarrivedParties());
            }
        });
    }

    private void parseCSS(CssTreeNode node, String css) {
        if (css == null) {
            return;
        }

        final CascadingStyleSheet cascadingStyleSheet = CSSReader.readFromString(css, ECSSVersion.CSS30);
        if (cascadingStyleSheet == null) {
            return;
        }

        ICommonsList<ICSSTopLevelRule> allRules = cascadingStyleSheet.getAllRules();
        if (allRules.isEmpty()) {
            return;
        }

        ICommonsList<CSSImportRule> allImportRules = cascadingStyleSheet.getAllImportRules();
        node.setAllImportRules(allImportRules);
        node.setAllStyleRules(cascadingStyleSheet.getAllStyleRules());
    }

    private class TimeoutTask extends TimerTask {
        @Override
        public void run() {
            logger.verbose("Check Timer timeout.");
            isCheckTimerTimedOut.set(true);
        }
    }

}



