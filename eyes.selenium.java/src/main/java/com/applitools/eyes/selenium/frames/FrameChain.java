/*
 * Applitools software.
 */
package com.applitools.eyes.selenium.frames;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.exceptions.NoFramesException;
import com.applitools.utils.ArgumentGuard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a path to a frame, including their location and scroll.
 */
public class FrameChain implements Iterable<Frame> {
    private final Logger logger;
    private final List<Frame> frames;

    /**
     * Compares two frame chains.
     * @param c1 Frame chain to be compared against c2.
     * @param c2 Frame chain to be compared against c1.
     * @return True if both frame chains represent the same frame, false otherwise.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isSameFrameChain(FrameChain c1, FrameChain c2) {
        if (c1 == null) return c2 == null || c2.frames.size() == 0;
        if (c2 == null) return c1.frames.size() == 0;

        int lc1 = c1.frames.size();
        int lc2 = c2.frames.size();

        // different chains size means different frames
        if (lc1 != lc2) {
            return false;
        }

        Iterator<Frame> c1Iterator = c1.iterator();
        Iterator<Frame> c2Iterator = c2.iterator();

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < lc1; ++i) {
            if (!c1Iterator.next().getReference().equals(c2Iterator.next().getReference())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Creates a new frame chain.
     * @param logger A Logger instance.
     */
    public FrameChain(Logger logger) {
        ArgumentGuard.notNull(logger, "logger");
        this.logger = logger;
        frames = new LinkedList<>();
    }

    /**
     * Creates a frame chain which is a copy of the current frame.
     * @param logger A Logger instance.
     * @param other  A frame chain from which the current frame chain will be created.
     */
    public FrameChain(Logger logger, FrameChain other) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(other, "other");
        this.logger = logger;
        frames = new ArrayList<>(other.frames);
    }

    /**
     * Size int.
     * @return The number of frames in the chain.
     */
    public int size() {
        return frames.size();
    }

    /**
     * Removes all current frames in the frame chain.
     */
    public void clear() {
        frames.clear();
    }

    /**
     * Removes the last inserted frame element. Practically means we switched
     * back to the parent of the current frame
     * @return the frame
     */
    public Frame pop() {
        if (frames.size() == 0) return null;
        return frames.remove(frames.size() - 1);
    }

    /**
     * Peek frame.
     * @return Returns the top frame in the chain.
     */
    public Frame peek() {
        if (frames.size() == 0) return null;
        return frames.get(frames.size() - 1);
    }

    /**
     * Appends a frame to the frame chain.
     * @param frame The frame to be added.
     */
    public void push(Frame frame) {
        frames.add(frame);
    }

    /**
     * Gets current frame offset.
     * @return The location of the current frame in the page.
     */
    public Location getCurrentFrameOffset() {
        Location result = new Location(0, 0);

        for (Frame frame : frames) {
            result = result.offset(frame.getLocation());
        }

        return result;
    }

    /**
     * Gets default content scroll position.
     * @return The outermost frame's location, or NoFramesException.
     */
    public Location getDefaultContentScrollPosition() {
        if (frames.size() == 0) {
            throw new NoFramesException("No frames in frame chain");
        }
        return new Location(frames.get(0).getOriginalLocation());
    }

    /**
     * Gets current frame size.
     * @return The size of the current frame.
     */
    public RectangleSize getCurrentFrameSize() {
        RectangleSize result = frames.get(frames.size() - 1).getOuterSize();
        return result;
    }

    /**
     * Gets current frame inner size.
     * @return The inner size of the current frame.
     */
    public RectangleSize getCurrentFrameInnerSize() {
        RectangleSize result = frames.get(frames.size() - 1).getInnerSize();
        return result;
    }

    public Frame getAt(int index) {
        return frames.get(index);
    }

    /**
     * @return An iterator to go over the frames in the chain.
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public Iterator<Frame> iterator() {
        return new Iterator<Frame>() {
            Iterator<Frame> framesIterator = frames.iterator();

            public boolean hasNext() {
                return framesIterator.hasNext();
            }

            public Frame next() {
                return framesIterator.next();
            }

            public void remove() {
                throw new EyesException("Remove is forbidden using the iterator!");
            }
        };
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public FrameChain clone() {
        return new FrameChain(this.logger, this);
    }
}
