package com.applitools.eyes.selenium;

import com.applitools.eyes.Location;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.frames.FrameChain;

public class PositionProviderAndMemento {

    private Location currentScrollPosition;
    private PositionProvider provider;
    private PositionMemento memento;
    private FrameChain frames;


    public PositionProviderAndMemento(PositionProvider positionProvider, PositionMemento positionMemento, FrameChain frames, Location currentScrollPosition) {
        provider = positionProvider;
        memento = positionMemento;
        this.frames = frames;
        this.currentScrollPosition = currentScrollPosition;
    }


    public void restoreState() {
        provider.restoreState(memento);
    }

    public PositionProvider getProvider() {
        return provider;
    }

    public void setProvider(PositionProvider provider) {
        this.provider = provider;
    }

    public PositionMemento getMemento() {
        return memento;
    }

    public void setMemento(PositionMemento memento) {
        this.memento = memento;
    }

    public FrameChain getFrames() {
        return frames;
    }

    public void setFrames(FrameChain frames) {
        this.frames = frames;
    }

    public Location getCurrentScrollPosition() {
        return currentScrollPosition;
    }

    public void setCurrentScrollPosition(Location currentScrollPosition) {
        this.currentScrollPosition = currentScrollPosition;
    }
}
