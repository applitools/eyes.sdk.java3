package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.Logger;
import com.applitools.utils.GeneralUtils;
import com.steadystate.css.parser.SACParserCSS3Constants;
import com.steadystate.css.parser.Token;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.css.CSSRuleList;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

public class TestResourceParsing {
    @Test
    public void testCssTokenizer() throws IOException {
        String cssContent = GeneralUtils.readToEnd(getClass().getResourceAsStream("/clientlibs_all.default.css"));
        CssTokenizer tokenizer = new CssTokenizer(cssContent);
        Token token;
        int urlCounter = 0;
        while ((token = tokenizer.nextToken()) != null) {
            if (token.kind == SACParserCSS3Constants.URI) {
                urlCounter++;
            }
        }

        // There are exactly 54 imported urls in the resources file
        Assert.assertEquals(urlCounter, 54);
    }

    @Test
    public void testParseCss() throws IOException {
        String cssContent = GeneralUtils.readToEnd(getClass().getResourceAsStream("/css_file_with_urls.css"));
        RGridResource resource = new RGridResource(
                "https://test.com",
                "text/css", cssContent.getBytes());
        Set<URI> newResources = resource.parse(new Logger(), "");

        // There are exactly 54 imported urls in the resources file but only 36 unique urls
        Assert.assertEquals(newResources.size(), 3);
    }

    @Test
    public void testParseCssBadFile() throws IOException {
        String cssContent = GeneralUtils.readToEnd(getClass().getResourceAsStream("/clientlibs_all.default.css"));
        RGridResource resource = new RGridResource(
                "https://fvdev-5531.shopci.intdigital.ee.co.uk:18083/etc/designs/ee-web-2015/clientlibs_all.default.css",
                "text/css", cssContent.getBytes());
        Set<URI> newResources = resource.parse(new Logger(), "");

        // There are exactly 54 imported urls in the resources file but only 36 unique urls
        Assert.assertEquals(newResources.size(), 36);
    }

    @Test
    public void testCssTokenizerStyleRules() throws IOException {
        String cssContent = GeneralUtils.readToEnd(getClass().getResourceAsStream("/css_file_with_urls.css"));
        CSSRuleList cssRuleList = CssTokenizer.getCssRules(cssContent);
        Assert.assertEquals(cssRuleList.getLength(), 4);
    }
}
