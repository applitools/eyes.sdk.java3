package com.applitools.eyes.fluent;

import com.applitools.eyes.Region;

/**
 *
 */
public class CheckTarget {

    public static ICheckSettings window()
    {
        return new CheckSettings();
    }

    public static ICheckSettings region(Region rect)
    {
        return new CheckSettings();
    }
}
