package com.applitools.eyes.universal.server;

import com.applitools.utils.GeneralUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UniversalSdkNativeLoaderTests {

    @Test
    public void testUniversalSdkPath() {
        try (MockedStatic<GeneralUtils> utilities = Mockito.mockStatic(GeneralUtils.class, Mockito.CALLS_REAL_METHODS)) {
            utilities.when(() -> GeneralUtils.getEnvString("APPLITOOLS_UNIVERSAL_PATH"))
                    .thenReturn("/Users/danielputerman/test/jpmc-universal");
            utilities.when(() -> GeneralUtils.getPropertyString("os.name")).thenReturn("macos");
            UniversalSdkNativeLoader.start();
            Assertions.assertTrue(Files.exists(Paths.get(GeneralUtils.getEnvString("APPLITOOLS_UNIVERSAL_PATH"))));
        }
    }

}
