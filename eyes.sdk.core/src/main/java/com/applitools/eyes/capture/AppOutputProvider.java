package com.applitools.eyes.capture;

import com.applitools.eyes.AppOutput;
import com.applitools.eyes.ImageMatchSettings;
import com.applitools.eyes.Region;
import com.applitools.eyes.fluent.ICheckSettingsInternal;

/**
 * Encapsulates a callback which returns an application output.
 */
public interface AppOutputProvider {
    AppOutput getAppOutput(Region region,
                           ICheckSettingsInternal checkSettingsInternal, ImageMatchSettings imageMatchSettings);
}
