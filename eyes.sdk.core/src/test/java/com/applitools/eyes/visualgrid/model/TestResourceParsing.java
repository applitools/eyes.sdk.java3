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
import java.util.HashSet;
import java.util.Set;

public class TestResourceParsing {
    @Test
    public void testCssTokenizer() throws IOException {
        String cssContent = GeneralUtils.readToEnd(getClass().getResourceAsStream("/clientlibs_all.default.css"));
        CssTokenizer tokenizer = new CssTokenizer(cssContent);
        Token token;
        int urlCounter = 0;
        Set<String> uniqueUrls = new HashSet<>();
        while ((token = tokenizer.nextToken()) != null) {
            if (token.kind == SACParserCSS3Constants.URI) {
                urlCounter++;
                uniqueUrls.add(token.image);
            }
        }

        // There are exactly 54 imported urls in the resources file and 38 are unique urls
        Assert.assertEquals(urlCounter, 54);
        Assert.assertEquals(uniqueUrls.size(), 38);
    }

    @Test
    public void testParseCss() throws IOException {
        String cssContent = GeneralUtils.readToEnd(getClass().getResourceAsStream("/css_file_with_urls.css"));
        RGridResource resource = new RGridResource( "https://test.com", "text/css", cssContent.getBytes());
        Set<URI> newResources = resource.parse(new Logger(), "");
        Assert.assertEquals(newResources.size(), 3);
    }

    @Test
    public void testParseCssBadFile() throws IOException {
        String cssContent = GeneralUtils.readToEnd(getClass().getResourceAsStream("/clientlibs_all.default.css"));
        RGridResource resource = new RGridResource( "https://test.com", "text/css", cssContent.getBytes());
        Set<URI> newResources = resource.parse(new Logger(), "");

        // There are exactly 54 imported urls in the resources file but only 38 unique urls
        Assert.assertEquals(newResources.size(), 38);
    }

    @Test
    public void testCssTokenizerStyleRules() throws IOException {
        String cssContent = GeneralUtils.readToEnd(getClass().getResourceAsStream("/css_file_with_urls.css"));
        CSSRuleList cssRuleList = CssTokenizer.getCssRules(cssContent);
        Assert.assertEquals(cssRuleList.getLength(), 4);
    }
}
