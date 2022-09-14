package com.applitools.eyes;

public enum ApplitoolsLibMode {
    ANDROID_APP,
    IOS_APP_BUILT_IN, // If you statically linked the NMG lib to your app, use this.
    IOS_APP_INSTRUMENTED_SIMULATOR,
    IOS_APP_INSTRUMENTED_REAL_DEVICE;
}
