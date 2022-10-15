package com.applitools.eyes.settings;

public class CloseSettings implements ICloseSettings {

    private Boolean throwErr;
    private Boolean updateBaselineIfNew;
    private Boolean updateBaselineIfDifferent;

    @Override
    public ICloseSettings setThrowErr(Boolean throwErr) {
        CloseSettings clone = this.clone();
        clone.throwErr = throwErr;
        return clone;
    }

    @Override
    public ICloseSettings setUpdateBaselineIfNew(Boolean updateBaselineIfNew) {
        CloseSettings clone = this.clone();
        clone.updateBaselineIfNew = updateBaselineIfNew;
        return clone;
    }

    @Override
    public ICloseSettings setUpdateBaselineIfDifferent(Boolean updateBaselineIfDifferent) {
        CloseSettings clone = this.clone();
        clone.updateBaselineIfDifferent = updateBaselineIfDifferent;
        return clone;
    }

    public CloseSettings clone() {
        CloseSettings clone = new CloseSettings();

        clone.throwErr = this.throwErr;
        clone.updateBaselineIfNew = this.updateBaselineIfNew;
        clone.updateBaselineIfDifferent = this.updateBaselineIfDifferent;

        return clone;
    }

    @Override
    public Boolean getThrowErr() {
        return this.throwErr;
    }

    @Override
    public Boolean getUpdateBaselineIfNew() {
        return null;
    }

    @Override
    public Boolean getUpdateBaselineIfDifferent() {
        return null;
    }
}
