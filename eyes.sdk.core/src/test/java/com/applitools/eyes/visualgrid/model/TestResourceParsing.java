package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.Logger;
import com.applitools.eyes.utils.ReportingTestSuite;
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

public class TestResourceParsing extends ReportingTestSuite {

    public TestResourceParsing() {
        super.setGroupName("core");
    }

    @Test
    public void testCssTokenizer() throws IOException {
        String cssContent = GeneralUtils.readInputStreamAsString(getClass().getResourceAsStream("/clientlibs_all.default.css"));
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
        String cssContent = GeneralUtils.readInputStreamAsString(getClass().getResourceAsStream("/css_file_with_urls.css"));
        RGridResource resource = new RGridResource( "https://test.com", "text/css", cssContent.getBytes());
        Set<URI> newResources = resource.parse(new Logger(), "https://test.com/home/#a#b");
        Assert.assertEquals(newResources.size(), 4);
    }

    @Test
    public void testParseCssBadFile() throws IOException {
        String cssContent = GeneralUtils.readInputStreamAsString(getClass().getResourceAsStream("/clientlibs_all.default.css"));
        RGridResource resource = new RGridResource( "https://test.com", "text/css", cssContent.getBytes());
        Set<URI> newResources = resource.parse(new Logger(), "");

        // There are exactly 54 imported urls in the resources file but only 38 unique urls
        Assert.assertEquals(newResources.size(), 38);
    }

    @Test
    public void testCssTokenizerStyleRules() throws IOException {
        String cssContent = GeneralUtils.readInputStreamAsString(getClass().getResourceAsStream("/css_file_with_urls.css"));
        CSSRuleList cssRuleList = CssTokenizer.getCssRules(cssContent);
        Assert.assertEquals(cssRuleList.getLength(), 5);
    }

    @Test
    void testGetImportedUrls() throws IOException {
        Set<String> imported = CssTokenizer.getImportedUrls(
                "@import url('innerstyle2.css');\n" +
                        "p {\n" +
                        "    text-shadow: 1px 2px 3px rgba(0,0,0,0.5);\n" +
                        "    font-family: 'Raleway', sans-serif;\n" +
                        "}");
        Assert.assertEquals(imported.size(), 1);
        Assert.assertTrue(imported.contains("innerstyle2.css"));
    }
}
