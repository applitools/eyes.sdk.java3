package com.applitools.eyes.settings;

public interface ICloseSettings {

    Boolean getThrowErr();

    ICloseSettings setThrowErr(Boolean throwErr);

    Boolean getUpdateBaselineIfNew();

    ICloseSettings setUpdateBaselineIfNew(Boolean updateBaselineIfNew);

    Boolean getUpdateBaselineIfDifferent();

    ICloseSettings setUpdateBaselineIfDifferent(Boolean updateBaselineIfDifferent);
}
