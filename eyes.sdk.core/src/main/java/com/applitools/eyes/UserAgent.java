package com.applitools.eyes;

import com.applitools.eyes.exceptions.NotSupportedException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles parsing of a user agent string
 */
public class UserAgent {
    private static final String MAJOR_MINOR = "(?<major>\\d+)(?:[_.](?<minor>\\d+))?";
    private static final String PRODUCT = "(?:(?<product>%s)/" + MAJOR_MINOR + ")";

    private static final Pattern VERSION_REGEX = Pattern.compile(String.format(PRODUCT, "Version"));

    private static final Pattern[] BROWSER_REGEXES = new Pattern[]{
            Pattern.compile(String.format(PRODUCT, "Opera")),
            Pattern.compile(String.format(PRODUCT, "Chrome")),
            Pattern.compile(String.format(PRODUCT, "Safari")),
            Pattern.compile(String.format(PRODUCT, "Firefox")),
            Pattern.compile(String.format(PRODUCT, "Edge")),
            Pattern.compile("(?:MS(?<product>IE) " + MAJOR_MINOR + ")")
    };

    private static final Pattern[] OS_REGEXES = new Pattern[]{
            Pattern.compile("(?:(?<os>Windows NT) " + MAJOR_MINOR + ")"),
            Pattern.compile("(?:(?<os>Windows XP))"),
            Pattern.compile("(?:(?<os>Windows 2000))"),
            Pattern.compile("(?:(?<os>Windows NT))"),
            Pattern.compile("(?:(?<os>Windows))"),
            Pattern.compile("(?:(?<os>Mac OS X) " + MAJOR_MINOR + ")"),
            Pattern.compile("(?:(?<os>Android) " + MAJOR_MINOR + ")"),
            Pattern.compile("(?:(?<os>CPU(?: i[a-zA-Z]+)? OS) " + MAJOR_MINOR + ")"),
            Pattern.compile("(?:(?<os>Mac OS X))"),
            Pattern.compile("(?:(?<os>Mac_PowerPC))"),
            Pattern.compile("(?:(?<os>Linux))"),
            Pattern.compile("(?:(?<os>CrOS))"),
            Pattern.compile("(?:(?<os>SymbOS))")
    };

    private static final Pattern HIDDEN_IE_REGEX = Pattern.compile(
            "(?:(?:rv:" + MAJOR_MINOR + "\\) like Gecko))");

    private static final Pattern EDGE_REGEX = Pattern.compile(String.format(PRODUCT, "Edge"));

    private String originalUserAgentString;
    private String os;
    private String osMajorVersion;
    private String osMinorVersion;
    private String browser;
    private String browserMajorVersion;
    private String browserMinorVersion;

    /**
     * @param userAgent User agent string to parse
     * @return A representation of the user agent string.
     */
    public static UserAgent parseUserAgentString(String userAgent) {
        return parseUserAgentString(userAgent, true);
    }

    /**
     * @param userAgent User agent string to parse
     * @param unknowns  Whether to treat unknown products as {@code UNKNOWN} or throw an exception.
     * @return A representation of the user agent string.
     */
    public static UserAgent parseUserAgentString(String userAgent, boolean unknowns) {
        if (userAgent == null) return null;

        userAgent = userAgent.trim();
        UserAgent result = new UserAgent();
        result.originalUserAgentString = userAgent;

        // os
        Map<String, Matcher> oss = new HashMap<>();
        List<Matcher> matchers = new ArrayList<>();

        for (Pattern osRegex : OS_REGEXES) {
            Matcher matcher = osRegex.matcher(userAgent);
            if (matcher.find()) {
                matchers.add(matcher);
                break;
            }
        }

        for (Matcher m : matchers) {
            String os = m.group("os");
            if (os != null) {
                oss.put(os.toLowerCase(), m);
            }
        }

        Matcher osMatch;
        if (matchers.size() == 0) {
            if (unknowns) {
                result.os = OSNames.UNKNOWN;
            } else {
                throw new NotSupportedException("Unknown os: " + userAgent);
            }
        } else {
            if (oss.size() > 1 && oss.containsKey("android")) {
                osMatch = oss.get("android");
            } else {
                osMatch = oss.values().toArray(new Matcher[0])[0];
            }

            result.os = osMatch.group("os");
            if (osMatch.groupCount() > 1) {
                result.osMajorVersion = osMatch.group("major");
            }
            if (result.osMajorVersion == null) {
                result.osMajorVersion = "";
            }
            if (osMatch.groupCount() > 2) {
                result.osMinorVersion = osMatch.group("minor");
                if (result.osMinorVersion == null || result.osMinorVersion.length() == 0) {
                    result.osMinorVersion = "0";
                }
            }
            if (result.osMinorVersion == null) {
                result.osMinorVersion = "";
            }
        }

        // os Normalization
        if (result.os.toUpperCase().startsWith("CPU")) {
            result.os = OSNames.IOS;
        } else if (result.os.equals("Windows XP")) {
            result.os = OSNames.WINDOWS;
            result.osMajorVersion = "5";
            result.osMinorVersion = "1";
        } else if (result.os.equals("Windows 2000")) {
            result.os = OSNames.WINDOWS;
            result.osMajorVersion = "5";
            result.osMinorVersion = "0";
        } else if (result.os.equals("Windows NT")) {
            result.os = OSNames.WINDOWS;
            if (result.osMajorVersion.equals("6") && result.osMinorVersion.equals("1")) {
                result.osMajorVersion = "7";
                result.osMinorVersion = "0";
            } else if (result.osMajorVersion.trim().length() == 0) {
                result.osMajorVersion = "4";
                result.osMinorVersion = "0";
            }
        } else if (result.os.equals("Mac_PowerPC")) {
            result.os = OSNames.MACINTOSH;
        } else if (result.os.equals("CrOS")) {
            result.os = OSNames.CHROME_OS;
        }

        // browser
        boolean browserOK = false;

        for (Pattern browserRegex : BROWSER_REGEXES) {
            Matcher matcher = browserRegex.matcher(userAgent);
            if (matcher.find()) {
                result.browser = matcher.group("product");
                result.browserMajorVersion = matcher.group("major");
                result.browserMinorVersion = matcher.group("minor");
                browserOK = true;
                break;
            }
        }

        if (result.os.equals(OSNames.WINDOWS)) {
            Matcher edgeMatch = EDGE_REGEX.matcher(userAgent);
            if (edgeMatch.find()) {
                result.browser = BrowserNames.EDGE;
                result.browserMajorVersion = edgeMatch.group("major");
                result.browserMinorVersion = edgeMatch.group("minor");
            }

            // IE11 and later is "hidden" on purpose.
            // http://blogs.msdn.com/b/ieinternals/archive/2013/09/21/
            //   internet-explorer-11-user-agent-string-ua-string-sniffing-
            //   compatibility-with-gecko-webkit.aspx
            Matcher iematch = HIDDEN_IE_REGEX.matcher(userAgent);
            if (iematch.find()) {
                result.browser = BrowserNames.IE;
                result.browserMajorVersion = iematch.group("major");
                result.browserMinorVersion = iematch.group("minor");

                browserOK = true;
            }
        }

        if (!browserOK) {
            if (unknowns) {
                result.browser = "Unknown";
            } else {
                throw new NotSupportedException("Unknown browser: " + userAgent);
            }
        }

        // Explicit browser version (if available)
        Matcher versionMatch = VERSION_REGEX.matcher(userAgent);
        if (versionMatch.find()) {
            result.browserMajorVersion = versionMatch.group("major");
            result.browserMinorVersion = versionMatch.group("minor");
        }

        return result;
    }

    public String getOriginalUserAgentString() {
        return originalUserAgentString;
    }

    public String getOS() {
        return os;
    }

    public String getOSMajorVersion() {
        return osMajorVersion;
    }

    public String getOSMinorVersion() {
        return osMinorVersion;
    }

    public String getBrowser() {
        return browser;
    }

    public String getBrowserMajorVersion() {
        return browserMajorVersion;
    }

    public String getBrowserMinorVersion() {
        return browserMinorVersion;
    }

    @Override
    public String toString() {
        return String.format("%s %s.%s / %s %s.%s", os, osMajorVersion, osMinorVersion, browser, browserMajorVersion, browserMinorVersion);
    }
}