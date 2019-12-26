package com.applitools.utils;

import com.applitools.ICheckSettingsInternal;
import com.applitools.eyes.EyesException;
import com.applitools.eyes.Logger;
import com.applitools.eyes.config.IConfigurationGetter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.GZIPOutputStream;

/**
 * General purpose utilities.
 */
public class GeneralUtils {

    @SuppressWarnings({"SpellCheckingInspection", "unused"})
    private static final String DATE_FORMAT_ISO8601_FOR_OUTPUT =
            "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private static final String DATE_FORMAT_ISO8601_FOR_INPUT =
            "yyyy-MM-dd'T'HH:mm:ssXXX";

    @SuppressWarnings("SpellCheckingInspection")
    private static final String DATE_FORMAT_RFC1123 =
            "E, dd MMM yyyy HH:mm:ss 'GMT'";
    private static final String QUESTION_MARK = "?";

    private GeneralUtils() {
    }

    /**
     * Read to end string.
     * @param inputStream The stream which content we would like to read.
     * @return The entire contents of the input stream as a string.
     * @throws IOException the io exception
     */
    @SuppressWarnings("UnusedDeclaration")
    public static String readToEnd(InputStream inputStream) throws IOException {
        ArgumentGuard.notNull(inputStream, "inputStream");

        //noinspection SpellCheckingInspection
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }

        return new String(baos.toByteArray());
    }

    /**
     * Formats date and time as represented by a calendar instance to an ISO
     * 8601 string.
     * @param calendar The date and time which we would like to format.
     * @return An ISO8601 formatted string representing the input date and time.
     */
    public static String toISO8601DateTime(Calendar calendar) {
        ArgumentGuard.notNull(calendar, "calendar");

        SimpleDateFormat formatter =
                new SimpleDateFormat(DATE_FORMAT_ISO8601_FOR_INPUT, Locale.ENGLISH);

        // For the string to be formatted correctly you MUST also set
        // the time zone in the formatter! See:
        // http://www.coderanch.com/t/376467/java/java/Display-time-timezones
        formatter.setTimeZone(calendar.getTimeZone());

        return formatter.format(calendar.getTime());
    }

    /**
     * Formats date and time as represented by a calendar instance to an TFC
     * 1123 string.
     * @param calendar The date and time which we would like to format.
     * @return An RFC 1123 formatted string representing the input date and time.
     */
    public static String toRfc1123(Calendar calendar) {
        ArgumentGuard.notNull(calendar, "calendar");

        SimpleDateFormat formatter =
                new SimpleDateFormat(DATE_FORMAT_RFC1123, Locale.ENGLISH);

        // For the string to be formatted correctly you MUST also set
        // the time zone in the formatter! See:
        // http://www.coderanch.com/t/376467/java/java/Display-time-timezones
        formatter.setTimeZone(calendar.getTimeZone());
        return formatter.format(calendar.getTime());
    }

    /**
     * Creates {@link java.util.Calendar} instance from an ISO 8601 formatted
     * string.
     * @param dateTime An ISO 8601 formatted string.
     * @return A {@link java.util.Calendar} instance representing the given date and time.
     * @throws ParseException the parse exception
     */
    public static Calendar fromISO8601DateTime(String dateTime)
            throws ParseException {
        ArgumentGuard.notNull(dateTime, "dateTime");
        String timezoneId = "UTC";
        // Remove second fractions
        if (dateTime.contains("T")) {
            if (dateTime.endsWith("Z")) {
                dateTime = dateTime.replaceAll("\\.(\\d+)Z", "Z");
            } else if (dateTime.contains("+")) {
                dateTime = dateTime.replaceAll("\\.(\\d+)\\+", "+");
                timezoneId += "+" + dateTime.split("\\+")[1];
            } else if (dateTime.contains("-")) {
                dateTime = dateTime.replaceAll("\\.(\\d+)\\+", "+");
                timezoneId += "-" + dateTime.split("-")[1];
            }
        }
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_ISO8601_FOR_INPUT);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timezoneId));
        cal.setTime(formatter.parse(dateTime));
        return cal;
    }

    /**
     * Sleeps the input amount of milliseconds.
     * @param milliseconds The number of milliseconds to sleep.
     */
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            throw new RuntimeException("sleep interrupted", ex);
        }
    }

    /**
     * Gets date.
     * @param format The date format parser.
     * @param date   The date string in a format matching {@code format}.
     * @return The {@link java.util.Date} represented by the input string.
     */
    @SuppressWarnings("UnusedDeclaration")
    public static Date getDate(DateFormat format, String date) {
        try {
            return format.parse(date);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Gets full seconds elapsed time millis.
     * @param start The start time. (Milliseconds)
     * @param end   The end time. (Milliseconds).
     * @return The elapsed time between the start and end times, rounded up to a full second, in milliseconds.
     */
    public static long getFullSecondsElapsedTimeMillis(long start, long end) {
        return ((long) Math.ceil((end - start) / 1000.0)) * 1000;
    }

    /**
     * Creates a {@link String} from a file specified by {@code resource}.
     * @param resource The resource path.
     * @return The resource's text.
     * @throws EyesException If there was a problem reading the resource.
     */
    @SuppressWarnings("UnusedDeclaration")
    public static String readTextFromResource(String resource) {
        InputStream is = GeneralUtils.class.getClassLoader()
                .getResourceAsStream(resource);

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        try {
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }

            try {
                br.close();
            } catch (IOException e) {
                // Nothing to do.
            }
        } catch (IOException e) {
            try {
                br.close();
            } catch (IOException e2) {
                // Nothing to do.
            }
            throw new EyesException("Failed to read text from resource: ", e);
        }
        return sb.toString();
    }

    /**
     * Log exception stack trace.
     * @param logger the logger
     * @param ex     the ex
     */
    public static void logExceptionStackTrace(Logger logger, Throwable ex) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(2048);
        PrintWriter writer = new PrintWriter(stream, true);
        ex.printStackTrace(writer);
        logger.log(ex.toString());
        try {
            logger.log(stream.toString("UTF-8"));
            writer.close();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets default server url.
     * @return the default server url
     */
    public static URI geServerUrl() {
        String serverURL;
        try {
            serverURL = GeneralUtils.getEnvString("APPLITOOLS_SERVER_URL");
            if(serverURL == null) serverURL = "https://eyesapi.applitools.com";
            URI uri = new URI(serverURL);
            return uri;
        } catch (URISyntaxException ex) {
            throw new EyesException(ex.getMessage(), ex);
        }
    }

    /**
     * Get gzip byte array output stream byte [ ].
     * @param domJson JSON as string to be gzipped
     * @return byte[] of the gzipped string
     */
    public static byte[] getGzipByteArrayOutputStream(String domJson) {
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();

        try {
            GZIPOutputStream gzip = new GZIPOutputStream(resultStream);
            gzip.write(domJson.getBytes(StandardCharsets.UTF_8));
            gzip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultStream.toByteArray();
    }

    /**
     * Get un gzip byte array output stream byte [ ].
     * @param gZippedString byte array gzipped encoded
     * @return byte[] of the ungzipped byte array
     * @throws IOException the io exception
     */
    public static byte[] getUnGzipByteArrayOutputStream(byte[] gZippedString) throws IOException {
        java.io.ByteArrayInputStream bytein = new java.io.ByteArrayInputStream(gZippedString);
        java.util.zip.GZIPInputStream gzin = new java.util.zip.GZIPInputStream(bytein);
        java.io.ByteArrayOutputStream byteout = new java.io.ByteArrayOutputStream();

        int res = 0;
        byte buf[] = new byte[1024];
        while (res >= 0) {
            res = gzin.read(buf, 0, buf.length);
            if (res > 0) {
                byteout.write(buf, 0, res);
            }
        }
        return byteout.toByteArray();
    }


    /**
     * Parse json to object t.
     * @param <T>                the type parameter
     * @param executeScripString the execute scrip string
     * @return the t
     * @throws IOException the io exception
     */
    public static <T> T parseJsonToObject(String executeScripString, Class<T> tClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        T executeScriptMap;
        if (tClass != null) {
            executeScriptMap = mapper.readValue(executeScripString, tClass);
        } else{
            executeScriptMap = mapper.readValue(executeScripString, new TypeReference<T>(){});
        }
        return executeScriptMap;
    }

    /**
     * Gets sha 256 hash.
     * @param content the content
     * @return the sha 256 hash
     */
    public static String getSha256hash(Byte[] content) {
        byte[] bytes = ArrayUtils.toPrimitive(content);
        return getSha256hash(bytes);
    }

    /**
     * Gets sha 256 hash.
     * @param content the content
     * @return the sha 256 hash
     */
    public static String getSha256hash(byte[] content) {
        if (content == null) return null;
        byte[] buffer = new byte[8192];
        int count;
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(content));
            while ((count = bis.read(buffer)) > 0) {
                digest.update(buffer, 0, count);
            }
            bis.close();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest.digest()) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean configureSendDom(ICheckSettingsInternal checkSettingsInternal, IConfigurationGetter configGetter) {
        Boolean sendDomFormCheckSettings = checkSettingsInternal.isSendDom();
        Boolean sendDomFromConfig = configGetter.isSendDom()  == null ? true : configGetter.isSendDom();
        if ((sendDomFormCheckSettings != null && sendDomFormCheckSettings) || (sendDomFormCheckSettings == null && sendDomFromConfig) ) {
            return true;
        }
        return false;
    }

    public static String sanitizeURL(String urlToSanitize, Logger logger) {
        String encoded = urlToSanitize;
        try {
            URL url = new URL(urlToSanitize);
            encoded = url.getProtocol() + "://" + url.getAuthority() + url.getPath() + (url.getQuery() != null && !url.getQuery().isEmpty() ? QUESTION_MARK + URLEncoder.encode(url.getQuery(), "UTF-8") : "");
        } catch (UnsupportedEncodingException | MalformedURLException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        return encoded;
    }

    public static String getEnvString(String applitools_env) {
        return System.getenv(applitools_env) == null ? System.getenv("bamboo_" + applitools_env) : System.getenv(applitools_env);
    }

    public static boolean getDontCloseBatches() {
        return "true".equalsIgnoreCase(GeneralUtils.getEnvString("APPLITOOLS_DONT_CLOSE_BATCHES"));
    }
}
