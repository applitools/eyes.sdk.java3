package com.applitools.connectivity;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

public class TestCookies {

    @Test
    public void testIsCookieUrlNonExpiredWithDottedCorrectDomain() throws URISyntaxException {
        Cookie cookie = new Cookie("subdir", "1", "/", ".a.com", false, false,
                new Date(System.currentTimeMillis() + Integer.MAX_VALUE));

        Assert.assertTrue(cookie.isCookieForUrl(new URI("http://a.com/")));
    }

    @Test
    public void testNotIsCookieForUrlExpiredWithDottedCorrectDomain() throws URISyntaxException {
        Cookie cookie = new Cookie("subdir", "1", "/", ".a.com", false, false,
                new Date(1));

        Assert.assertFalse(cookie.isCookieForUrl(new URI("http://a.com/")));
    }

    @Test
    public void testIsCookieForUrlWithDottedCorrectDomain() throws URISyntaxException {
        Cookie cookie = new Cookie("subdir", "1", "/", ".a.com", false, false,
                null);

        Assert.assertTrue(cookie.isCookieForUrl(new URI("http://a.com/")));
    }

    @Test
    public void testIsCookieForUrlWithDottedCorrectDomainIgnorePort() throws URISyntaxException {
        Cookie cookie = new Cookie("subdir", "1", "/", ".a.com", false, false,
                null);

        Assert.assertTrue(cookie.isCookieForUrl(new URI("http://a.com:8080/")));
    }

    @Test
    public void testIsCookieForUrlWithDottedCorrectSubdomain() throws URISyntaxException {
        Cookie cookie = new Cookie("subdir", "1", "/", ".a.com", false, false,
                null);

        Assert.assertTrue(cookie.isCookieForUrl(new URI("http://b.a.com/")));
    }

    @Test
    public void testIsCookieForUrlWithNotDottedCorrectDomain() throws URISyntaxException {
        Cookie cookie = new Cookie("subdir", "1", "/", "a.com", false, false,
                null);

        Assert.assertTrue(cookie.isCookieForUrl(new URI("http://a.com/")));
    }

    @Test
    public void testNotIsCookieForUrlWithNotDottedCorrectSubdomain() throws URISyntaxException {
        Cookie cookie = new Cookie("subdir", "1", "/", "a.com", false, false,
                null);

        Assert.assertFalse(cookie.isCookieForUrl(new URI("http://b.a.com/")));
    }

    @Test
    public void testIsCookieForUrlWithDottedIncorrectDomain() throws URISyntaxException {
        Cookie cookie = new Cookie("subdir", "1", "/", ".a.com", false, false,
                null);

        Assert.assertFalse(cookie.isCookieForUrl(new URI("http://b.com/")));
    }

    @Test
    public void testIsCookieForUrlWithNotDottedIncorrectDomain() throws URISyntaxException {
        Cookie cookie = new Cookie("subdir", "1", "/", "a.com", false, false,
                null);

        Assert.assertFalse(cookie.isCookieForUrl(new URI("http://b.com/")));
    }

    @Test
    public void testIsCookieForUrlWithNotDottedIncorrectSuffixedDomain() throws URISyntaxException {
        Cookie cookie = new Cookie("subdir", "1", "/", "a.com", false, false,
                null);

        Assert.assertFalse(cookie.isCookieForUrl(new URI("http://ba.com/")));
    }

    @Test
    public void testIsCookieForUrlWithDottedIncorrectSuffixedDomain() throws URISyntaxException {
        Cookie cookie = new Cookie("subdir", "1", "/", ".a.com", false, false,
                null);

        Assert.assertFalse(cookie.isCookieForUrl(new URI("http://ba.com/")));
    }

    @Test
    public void testIsCookieForUrlWithSecureCookieNonSecureUrl() throws URISyntaxException {
        Cookie cookie = new Cookie("subdir", "1", "/", "a.com", true, false,
                null);

        Assert.assertFalse(cookie.isCookieForUrl(new URI("http://a.com/subdir")));
    }

    @Test
    public void testIsCookieForUrlWithSecureCookieSecureUrl() throws URISyntaxException {
        Cookie cookie = new Cookie("subdir", "1", "/", "a.com", true, false,
                null);

        Assert.assertTrue(cookie.isCookieForUrl(new URI("https://a.com/subdir")));
    }

    @Test
    public void testIsCookieForUrlWithPathCookieIncorrectUrl() throws URISyntaxException {
        Cookie cookie = new Cookie("subdir", "1", "/b", "a.com", false, false,
                null);

        Assert.assertFalse(cookie.isCookieForUrl(new URI("http://a.com/")));
    }

    @Test
    public void testIsCookieForUrlWithPathCookieCorrectSubdirUrl() throws URISyntaxException {
        Cookie cookie = new Cookie("subdir", "1", "/b", "a.com", false, false,
                null);

        Assert.assertTrue(cookie.isCookieForUrl(new URI("http://a.com/b/c")));
    }
}
