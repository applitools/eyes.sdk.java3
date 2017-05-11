package com.applitools.eyes;

/**
 * A container for a MatchWindowData along with the screenshot used for
 * creating it. (We specifically avoid inheritance so we don't have to deal
 * with serialization issues).
 */
public class MatchWindowDataWithScreenshot {
    private final MatchWindowData matchWindowData;
    private final EyesScreenshot screenshot;

    public MatchWindowDataWithScreenshot(MatchWindowData matchWindowData,
                                         EyesScreenshot screenshot) {
        this.matchWindowData = matchWindowData;
        this.screenshot = screenshot;
    }

    public MatchWindowData getMatchWindowData() {
        return matchWindowData;
    }

    public EyesScreenshot getScreenshot() {
        return screenshot;
    }
}
