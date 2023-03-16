package com.applitools.eyes.universal.server;

import com.applitools.eyes.universal.utils.SystemInfo;
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
        SystemInfo systemInfo = SystemInfo.getSystemInfo();
        Assert.assertEquals(systemInfo.getOs(), "linux-x64");
        Assert.assertEquals(systemInfo.getSuffix(), "alpine");

        UniversalSdkNativeLoader.start();
        Integer port = UniversalSdkNativeLoader.getPort();
        Assert.assertNotNull(port);
        System.out.println("Universal port for alpine container: " + port);
    }

}