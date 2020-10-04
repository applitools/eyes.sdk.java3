package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.Logger;
import com.applitools.utils.GeneralUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

public class TestResourceParsing {

    @Test
    public void testParseCss() throws IOException {
        String cssContent = GeneralUtils.readToEnd(getClass().getResourceAsStream("/clientlibs_all.default.css"));
        RGridResource resource = new RGridResource(
                "https://fvdev-5531.shopci.intdigital.ee.co.uk:18083/etc/designs/ee-web-2015/clientlibs_all.default.css",
                "text/css", cssContent.getBytes());
        Set<URI> newResources = resource.parse(new Logger(), "");
        Assert.assertEquals(newResources.size(), 36);
    }
}
