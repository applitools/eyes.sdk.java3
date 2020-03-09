package com.applitools.eyes;

import com.applitools.eyes.exceptions.NotSupportedException;
import com.applitools.utils.ArgumentGuard;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles parsing of a user agent string
 */
public class UserAgent {
    private static final String MajorMinor = "(?<major>\\d+)(?:[_.](?<minor>\\d+))?";
    private static final String Product = "(?:(?<product>%s)/" + MajorMinor + ")";

    private static final Pattern VerRegex = Pattern.compile(String.format(Product, "Version"));

    private static final Pattern[] BrowserRegex = new Pattern[]{
            Pattern.compile(String.format(Product, "Opera")),
            Pattern.compile(String.format(Product, "Chrome")),
            Pattern.compile(String.format(Product, "Safari")),
            Pattern.compile(String.format(Product, "Firefox")),
            Pattern.compile(String.format(Product, "Edge")),
            Pattern.compile("(?:MS(?<product>IE) " + MajorMinor + ")")
    };

    private static final Pattern[] OSRegex = new Pattern[]{
            Pattern.compile("(?:(?<os>Windows NT) " + MajorMinor + ")"),
            Pattern.compile("(?:(?<os>Windows XP))"),
            Pattern.compile("(?:(?<os>Windows 2000))"),
            Pattern.compile("(?:(?<os>Windows NT))"),
            Pattern.compile("(?:(?<os>Windows))"),
            Pattern.compile("(?:(?<os>Mac OS X) " + MajorMinor + ")"),
            Pattern.compile("(?:(?<os>Android) " + MajorMinor + ")"),
            Pattern.compile("(?:(?<os>CPU(?: i[a-zA-Z]+)? OS) " + MajorMinor + ")"),
            Pattern.compile("(?:(?<os>Mac OS X))"),
            Pattern.compile("(?:(?<os>Mac_PowerPC))"),
            Pattern.compile("(?:(?<os>Linux))"),
            Pattern.compile("(?:(?<os>CrOS))"),
            Pattern.compile("(?:(?<os>SymbOS))")
    };

    private static final Pattern HiddenIE = Pattern.compile(
            "(?:(?:rv:" + MajorMinor + "\\) like Gecko))");

    private static final Pattern Edge = Pattern.compile(String.format(Product, "Edge"));

    private String originalUserAgentString;
    private String OS;
    private String OSMajorVersion;
    private String OSMinorVersion;
    private String Browser;
    private String BrowserMajorVersion;
    private String BrowserMinorVersion;

    public String getOriginalUserAgentString() {
        return originalUserAgentString;
    }

    public String getOS() {
        return OS;
    }

    public String getOSMajorVersion() {
        return OSMajorVersion;
    }

    public String getOSMinorVersion() {
        return OSMinorVersion;
    }

    public String getBrowser() {
        return Browser;
    }

    public String getBrowserMajorVersion() {
        return BrowserMajorVersion;
    }

    public String getBrowserMinorVersion() {
        return BrowserMinorVersion;
    }

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
        ArgumentGuard.notNull(userAgent, "userAgent");

        userAgent = userAgent.trim();
        UserAgent result = new UserAgent();
        result.originalUserAgentString = userAgent;

        // OS
        Map<String, Matcher> oss = new HashMap<>();
        List<Matcher> matchers = new ArrayList<>();

        for (Pattern osRegex : OSRegex) {
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
                result.OS = OSNames.Unknown;
            } else {
                throw new NotSupportedException("Unknown OS: " + userAgent);
            }
        } else {
            if (oss.size() > 1 && oss.containsKey("android")) {
                osMatch = oss.get("android");
            } else {
                osMatch = oss.values().toArray(new Matcher[0])[0];
            }

            result.OS = osMatch.group("os");
            if (osMatch.groupCount() > 1) {
                result.OSMajorVersion = osMatch.group("major");
            }
            if (result.OSMajorVersion == null) {
                result.OSMajorVersion = "";
            }
            if (osMatch.groupCount() > 2) {
                result.OSMinorVersion = osMatch.group("minor");
                if (result.OSMinorVersion == null || result.OSMinorVersion.length() == 0) {
                    result.OSMinorVersion = "0";
                }
            }
            if (result.OSMinorVersion == null) {
                result.OSMinorVersion = "";
            }
        }

        // OS Normalization
        if (result.OS.toUpperCase().startsWith("CPU")) {
            result.OS = OSNames.IOS;
        } else if (result.OS.equals("Windows XP")) {
            result.OS = OSNames.Windows;
            result.OSMajorVersion = "5";
            result.OSMinorVersion = "1";
        } else if (result.OS.equals("Windows 2000")) {
            result.OS = OSNames.Windows;
            result.OSMajorVersion = "5";
            result.OSMinorVersion = "0";
        } else if (result.OS.equals("Windows NT")) {
            result.OS = OSNames.Windows;
            if (result.OSMajorVersion.equals("6") && result.OSMinorVersion.equals("1")) {
                result.OSMajorVersion = "7";
                result.OSMinorVersion = "0";
            } else if (result.OSMajorVersion.trim().length() == 0) {
                result.OSMajorVersion = "4";
                result.OSMinorVersion = "0";
            }
        } else if (result.OS.equals("Mac_PowerPC")) {
            result.OS = OSNames.Macintosh;
        } else if (result.OS.equals("CrOS")) {
            result.OS = OSNames.ChromeOS;
        }

        // Browser
        boolean browserOK = false;

        for (Pattern browserRegex : BrowserRegex) {
            Matcher matcher = browserRegex.matcher(userAgent);
            if (matcher.find()) {
                result.Browser = matcher.group("product");
                result.BrowserMajorVersion = matcher.group("major");
                result.BrowserMinorVersion = matcher.group("minor");
                browserOK = true;
                break;
            }
        }

        if (result.OS.equals(OSNames.Windows)) {
            Matcher edgeMatch = Edge.matcher(userAgent);
            if (edgeMatch.find()) {
                result.Browser = BrowserNames.Edge;
                result.BrowserMajorVersion = edgeMatch.group("major");
                result.BrowserMinorVersion = edgeMatch.group("minor");
            }

            // IE11 and later is "hidden" on purpose.
            // http://blogs.msdn.com/b/ieinternals/archive/2013/09/21/
            //   internet-explorer-11-user-agent-string-ua-string-sniffing-
            //   compatibility-with-gecko-webkit.aspx
            Matcher iematch = HiddenIE.matcher(userAgent);
            if (iematch.find()) {
                result.Browser = BrowserNames.IE;
                result.BrowserMajorVersion = iematch.group("major");
                result.BrowserMinorVersion = iematch.group("minor");

                browserOK = true;
            }
        }

        if (!browserOK) {
            if (unknowns) {
                result.Browser = "Unknown";
            } else {
                throw new NotSupportedException("Unknown Browser: " + userAgent);
            }
        }

        // Explicit Browser version (if available)
        Matcher versionMatch = VerRegex.matcher(userAgent);
        if (versionMatch.find()) {
            result.BrowserMajorVersion = versionMatch.group("major");
            result.BrowserMinorVersion = versionMatch.group("minor");
        }

        return result;
    }
}