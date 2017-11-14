package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;

import javax.ws.rs.NotSupportedException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles parsing of a user agent string
 */
public class UserAgent {
    private static final String MAJOR_MINOR = "(?<major>[^ .;_)]+)[_.](?<minor>[^ .;_)]+)";
    private static final String PRODUCT = "(?:(?<product>%s)/" + MAJOR_MINOR + ")";

    // Browser Regexes
    private static final String[] VALUES_FOR_BROWSER_REGEX_EXCEPT_IE =
            new String[]{"Opera", "Chrome", "Safari", "Firefox", "Edge"};

    private static final Pattern IE_BROWSER_REGEX = Pattern.compile("(?:MS(?<product>IE) " + MAJOR_MINOR + ")");

    private String OS;
    private String OSMajorVersion;
    private String OSMinorVersion;
    private String Browser;
    private String BrowserMajorVersion;
    private String BrowserMinorVersion;

    private static Pattern[] getBrowserRegexes() {
        Pattern[] browserRegexes = new Pattern[VALUES_FOR_BROWSER_REGEX_EXCEPT_IE.length + 1];

        Pattern currentRegex;
        for (int i = 0; i < VALUES_FOR_BROWSER_REGEX_EXCEPT_IE.length; ++i) {
            String browser = VALUES_FOR_BROWSER_REGEX_EXCEPT_IE[i];
            browserRegexes[i] = Pattern.compile(String.format(PRODUCT, browser));
        }

        // Last pattern is IE
        browserRegexes[browserRegexes.length - 1] = IE_BROWSER_REGEX;

        return browserRegexes;
    }

    private static List<HashMap.Entry<String, String>> noHeaders = new LinkedList<>();

    private static final Pattern VERSION_REGEX = Pattern.compile(String.format(PRODUCT, "Version"));


    private static Pattern[] OS_REGEXES = new Pattern[]{
            Pattern.compile("(?:(?<os>Windows) NT " + MAJOR_MINOR + ")"),
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

    private static final Pattern HIDDEN_IE_REGEX = Pattern.compile("(?:(?:rv:" + MAJOR_MINOR + "\\) like Gecko))");

    private static final Pattern EDGE_REGEX = Pattern.compile(String.format(PRODUCT, "Edge"));

    /**
     * @param userAgent User agent string to parse
     * @param unknowns  Whether to treat unknown products as {@code UNKNOWN} or throw an exception.
     * @return A representation of the user agent string.
     */
    public static UserAgent ParseUserAgentString(String userAgent, boolean unknowns) {
        ArgumentGuard.notNull(userAgent, "userAgent");

        userAgent = userAgent.trim();
        UserAgent result = new UserAgent();

        // OS
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

        Matcher osmatch = null;
        if (matchers.size() == 0) {
            if (unknowns) {
                result.OS = OSNames.Unknown;
            } else {
                throw new NotSupportedException("Unknown OS: " + userAgent);
            }
        } else {
            if (oss.size() > 1 && oss.containsKey("android")) {
                osmatch = oss.get("android");
            } else {
                osmatch = oss.values().toArray(new Matcher[0])[0];
            }

            result.OS = osmatch.group("os");
            if (osmatch.groupCount() > 1) {
                result.OSMajorVersion = osmatch.group("major");
                result.OSMinorVersion = osmatch.group("minor");
            }
        }

        // OS Normalization
        if (result.OS.startsWith("CPU")) {
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
            result.OSMajorVersion = "4";
            result.OSMinorVersion = "0";
        } else if (result.OS.equals("Mac_PowerPC")) {
            result.OS = OSNames.Macintosh;
        } else if (result.OS.equals("CrOS")) {
            result.OS = OSNames.ChromeOS;
        }

        // Browser
        boolean browserOK = false;

        for (Pattern browserRegex : getBrowserRegexes()) {
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
            Matcher edgeMatch = EDGE_REGEX.matcher(userAgent);
            if (edgeMatch.find()) {
                result.Browser = BrowserNames.Edge;
                result.BrowserMajorVersion = edgeMatch.group("major");
                result.BrowserMinorVersion = edgeMatch.group("minor");
            }

            // IE11 and later is "hidden" on purpose.
            // http://blogs.msdn.com/b/ieinternals/archive/2013/09/21/
            //   internet-explorer-11-user-agent-string-ua-string-sniffing-
            //   compatibility-with-gecko-webkit.aspx
            Matcher iematch = HIDDEN_IE_REGEX.matcher(userAgent);
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
                throw new NotSupportedException("Unknown browser: " + userAgent);
            }
        }

        // Explicit browser version (if available)
        Matcher versionMatch = VERSION_REGEX.matcher(userAgent);
        if (versionMatch.find()) {
            result.BrowserMajorVersion = versionMatch.group("major");
            result.BrowserMinorVersion = versionMatch.group("minor");
        }

        return result;
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

    public String getOS() {
        return OS;
    }

    public String getOSMajorVersion() {
        return OSMajorVersion;
    }

    public String getOSMinorVersion() {
        return OSMinorVersion;
    }


}
