package com.applitools.eyes.capture;

import com.applitools.eyes.ImageMatchSettings;
import com.applitools.eyes.Region;
import com.applitools.eyes.fluent.ICheckSettingsInternal;

/**
 * Encapsulates a callback which returns an application output.
 */
public interface AppOutputProvider {
    AppOutputWithScreenshot getAppOutput(Region region,
                                         ICheckSettingsInternal checkSettingsInternal, ImageMatchSettings imageMatchSettings);
}
