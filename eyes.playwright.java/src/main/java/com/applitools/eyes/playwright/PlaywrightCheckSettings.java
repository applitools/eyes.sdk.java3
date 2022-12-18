package com.applitools.eyes.playwright;

import com.applitools.eyes.Region;
import com.applitools.eyes.fluent.CheckSettings;
import com.applitools.eyes.playwright.universal.FrameLocator;
import com.microsoft.playwright.Frame;

import java.util.ArrayList;
import java.util.List;

public class PlaywrightCheckSettings extends CheckSettings implements IPlaywrightCheckSettings {

    private String locator;
    private final List<FrameLocator> frameChain = new ArrayList<>();

    public PlaywrightCheckSettings() {
    }

    public PlaywrightCheckSettings(Region region) {
        super(region);
    }

    public PlaywrightCheckSettings(String locator) {
        this.locator = locator;
    }

    public PlaywrightCheckSettings frame(String locator) {
        PlaywrightCheckSettings clone = this.clone();
        FrameLocator fl = new FrameLocator();
        fl.setFrameLocator(locator);
        clone.frameChain.add(fl);
        return clone;
    }

    public PlaywrightCheckSettings frame(int index) {
        PlaywrightCheckSettings clone = this.clone();
        FrameLocator fl = new FrameLocator();
        fl.setFrameIndex(index);
        clone.frameChain.add(fl);
        return clone;
    }

    public PlaywrightCheckSettings frame(Frame frame) {
        PlaywrightCheckSettings clone = this.clone();
        FrameLocator fl = new FrameLocator();
        fl.setFrameReference(frame);
        clone.frameChain.add(fl);
        return clone;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public PlaywrightCheckSettings clone() {
        PlaywrightCheckSettings clone = new PlaywrightCheckSettings();

        clone.locator = this.locator;

        return clone;
    }
}
