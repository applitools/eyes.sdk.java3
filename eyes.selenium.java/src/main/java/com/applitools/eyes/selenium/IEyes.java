package com.applitools.eyes.selenium;

import com.applitools.ICheckSettings;
import com.applitools.eyes.IEyesBase;

public interface IEyes extends IEyesBase {
    void check(ICheckSettings checkSettings);

    void check(ICheckSettings... checkSettings);

    void check(String testName, ICheckSettings checkSettings);
}
