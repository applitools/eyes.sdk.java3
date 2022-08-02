package com.applitools.eyes.selenium.universal.server;

import com.applitools.utils.GeneralUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class UniversalSdkNativeLoaderTests {

    @Test
    public void testUniversalSdkPath() {
        try (MockedStatic<GeneralUtils> utilities = Mockito.mockStatic(GeneralUtils.class)) {
            utilities.when(() -> GeneralUtils.getEnvString("APPLITOOLS_UNIVERSAL_PATH"))
                    .thenReturn("path");
            utilities.when(() -> GeneralUtils.getPropertyString("os.name")).thenReturn("macos");
            UniversalSdkNativeLoader.start();
            assertEquals(21077, (int)UniversalSdkNativeLoader.getPort());
            assertEquals("path", GeneralUtils.getEnvString("APPLITOOLS_UNIVERSAL_PATH"));
        }
    }

}
