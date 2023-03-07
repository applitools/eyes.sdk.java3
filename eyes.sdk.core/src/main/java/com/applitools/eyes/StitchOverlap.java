package com.applitools.eyes;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StitchOverlap {

    /**
     * top stitching overlap.
     */
    private Integer top;

    /**
     * bottom stitching overlap.
     */
    private Integer bottom;

    public StitchOverlap() {}

    public StitchOverlap(StitchOverlap stitchOverlap) {
        this.top = stitchOverlap.getTop();
        this.bottom = stitchOverlap.getBottom();
    }

    /**
     * sets the top and bottom stitching overlap.
     *
     * @param top  the top stitching overlap.
     * @param bottom  the bottom stitching overlap.
     */
    public StitchOverlap(int top, int bottom) {
        this.top = top;
        this.bottom = bottom;
    }

    /**
     * sets the top stitching overlap.
     *
     * @param top  the bottom stitching overlap.
     * @return an updated instance of this object
     */
    public StitchOverlap top(int top) {
        this.top = top;
        return this;
    }

    public Integer getTop() {
        return top;
    }

    /**
     * sets the bottom stitching overlap.
     *
     * @param bottom  the bottom stitching overlap.
     * @return an updated instance of this object
     */
    public StitchOverlap bottom(int bottom) {
        this.bottom = bottom;
        return this;
    }

    public Integer getBottom() {
        return bottom;
    }
}
