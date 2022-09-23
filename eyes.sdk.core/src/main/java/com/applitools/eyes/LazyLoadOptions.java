package com.applitools.eyes;

/**
 * lazy load options.
 */
public class LazyLoadOptions {
    private Integer scrollLength;
    private Integer waitingTime;
    private Integer maxAmountToScroll;

    public LazyLoadOptions() {
        this.scrollLength = 300;
        this.waitingTime = 2000;
        this.maxAmountToScroll = 15000;
    }

    public LazyLoadOptions(Integer scrollLength, Integer waitingTime, Integer pageHeight) {
        this.scrollLength = scrollLength;
        this.waitingTime = waitingTime;
        this.maxAmountToScroll = pageHeight;
    }

    @Override
    public LazyLoadOptions clone() {
        LazyLoadOptions clone = new LazyLoadOptions();
        clone.scrollLength = this.scrollLength;
        clone.waitingTime = this.waitingTime;
        clone.maxAmountToScroll = this.maxAmountToScroll;
        return clone;
    }

    public Integer getScrollLength() {
        return scrollLength;
    }

    public LazyLoadOptions scrollLength(Integer scrollLength) {
        LazyLoadOptions clone = this.clone();
        clone.scrollLength = scrollLength;
        return clone;
    }

    public Integer getWaitingTime() {
        return waitingTime;
    }

    public LazyLoadOptions waitingTime(Integer waitingTime) {
        LazyLoadOptions clone = this.clone();
        clone.waitingTime = waitingTime;
        return clone;
    }

    public Integer getPageHeight() {
        return maxAmountToScroll;
    }

    public LazyLoadOptions pageHeight(Integer pageHeight) {
        LazyLoadOptions clone = this.clone();
        clone.maxAmountToScroll = pageHeight;
        return clone;
    }
}
