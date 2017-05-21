package com.applitools.utils;

import com.applitools.eyes.EyesException;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * General purpose utilities.
 */
public class GeneralUtils {

    @SuppressWarnings("SpellCheckingInspection")
    private static final String DATE_FORMAT_ISO8601 =
            "yyyy-MM-dd'T'HH:mm:ss'Z'";

    @SuppressWarnings("SpellCheckingInspection")
    private static final String DATE_FORMAT_RFC1123 =
            "E, dd MMM yyyy HH:mm:ss 'GMT'";

    private GeneralUtils() {}

    /**
     * @param inputStream The stream which content we would like to read.
     * @return The entire contents of the input stream as a string.
     * @throws java.io.IOException If there was a problem reading/writing
     * from/to the streams used during the operation.
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
     *
     * @param calendar The date and time which we would like to format.
     * @return An ISO8601 formatted string representing the input date and time.
     */
    public static String toISO8601DateTime(Calendar calendar) {
        ArgumentGuard.notNull(calendar, "calendar");

        SimpleDateFormat formatter =
                new SimpleDateFormat(DATE_FORMAT_ISO8601, Locale.US);

        // For the string to be formatted correctly you MUST also set
        // the time zone in the formatter! See:
        // http://www.coderanch.com/t/376467/java/java/Display-time-timezones
        formatter.setTimeZone(calendar.getTimeZone());

        return formatter.format(calendar.getTime());
    }

    /**
     * Formats date and time as represented by a calendar instance to an TFC
     * 1123 string.
     *
     * @param calendar The date and time which we would like to format.
     * @return An RFC 1123 formatted string representing the input date and
     * time.
     */
    public static String toRfc1123(Calendar calendar) {
        ArgumentGuard.notNull(calendar, "calendar");

        SimpleDateFormat formatter =
                new SimpleDateFormat(DATE_FORMAT_RFC1123, Locale.US);

        // For the string to be formatted correctly you MUST also set
        // the time zone in the formatter! See:
        // http://www.coderanch.com/t/376467/java/java/Display-time-timezones
        formatter.setTimeZone(calendar.getTimeZone());
        return formatter.format(calendar.getTime());
    }

    /**
     * Creates {@link java.util.Calendar} instance from an ISO 8601 formatted
     * string.
     *
     * @param dateTime An ISO 8601 formatted string.
     * @return A {@link java.util.Calendar} instance representing the given
     *          date and time.
     * @throws java.text.ParseException If {@code dateTime} is not in the ISO
     * 8601 format.
     */
    public static Calendar fromISO8601DateTime(String dateTime)
            throws ParseException {
        ArgumentGuard.notNull(dateTime, "dateTime");

        SimpleDateFormat formatter =
                new SimpleDateFormat(DATE_FORMAT_ISO8601);

        Calendar cal = Calendar.getInstance();
        cal.setTime(formatter.parse(dateTime));
        return cal;
    }

    /**
     * Sleeps the input amount of milliseconds.
     *
     * @param milliseconds The number of milliseconds to sleep.
     */
    public static void sleep(long milliseconds)
    {
        try
        {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException ex)
        {
            throw new RuntimeException("sleep interrupted", ex);
        }
    }

    /**
     * @param format The date format parser.
     * @param date The date string in a format matching {@code format}.
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
     *
     * @param start The start time. (Milliseconds)
     * @param end The end time. (Milliseconds).
     * @return The elapsed time between the start and end times, rounded up
     * to a full second, in milliseconds.
     */
    public static long getFullSecondsElapsedTimeMillis(long start, long end) {
        return ((long) Math.ceil((end - start) / 1000.0)) * 1000;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * Creates a {@link String} from a file specified by {@code resource}.
     *
     * @param resource The resource path.
     * @return The resource's text.
     * @throws EyesException If there was a problem reading the resource.
     */
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
}
