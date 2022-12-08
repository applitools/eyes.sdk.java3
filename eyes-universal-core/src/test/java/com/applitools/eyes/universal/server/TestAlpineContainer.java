package com.applitools.eyes.universal.server;

import com.applitools.utils.GeneralUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TestAlpineContainer {

    /**
     * used in GitHub actions in alpine tests.
     */
    @Test
    public void testAlpineContainer() {
        UniversalSdkNativeLoader.start();
        Integer port = UniversalSdkNativeLoader.getPort();
        Assert.assertNotNull(port);
        System.out.println("Universal port for alpine container: " + port);

        String osVersion = GeneralUtils.getPropertyString("os.name").toLowerCase();
        Assert.assertTrue(osVersion.contains("linux"));
        Assert.assertTrue(Files.exists(Paths.get("/etc/alpine-release")));
    }

}