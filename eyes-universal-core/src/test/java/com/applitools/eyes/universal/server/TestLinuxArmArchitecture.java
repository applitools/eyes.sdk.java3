package com.applitools.eyes.universal.server;

import com.applitools.utils.GeneralUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestLinuxArmArchitecture {

    /**
     * used in GitHub actions in arm64 tests.
     */
    @Test
    public void testArmArchitecture() {
        UniversalSdkNativeLoader.start();
        Integer port = UniversalSdkNativeLoader.getPort();
        Assert.assertNotNull(port);
        System.out.println("Universal port for arm64 architecture: " + port);

        String osVersion = GeneralUtils.getPropertyString("os.name").toLowerCase();
        String osArchitecture = GeneralUtils.getPropertyString("os.arch").toLowerCase();
        Assert.assertTrue(osVersion.contains("linux"));
        Assert.assertEquals(osArchitecture, "arm64");
    }
}
