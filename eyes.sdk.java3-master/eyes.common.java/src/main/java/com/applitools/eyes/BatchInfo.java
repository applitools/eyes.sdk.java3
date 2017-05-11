package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.applitools.utils.Iso8610CalendarSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

@SuppressWarnings({"unused", "WeakerAccess"})
/**
 * A batch of tests.
 */
public class BatchInfo {

    private static final String BATCH_TIMEZONE = "UTC";
    private String id;
    private final String name;
    private final String startedAt;

    /**
     * Creates a new BatchInfo instance.
     *
     * @param name      Name of batch or {@code null} if anonymous.
     * @param startedAt Batch start time
     */
    public BatchInfo(String name, Calendar startedAt) {
        ArgumentGuard.notNull(startedAt, "startedAt");
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.startedAt = GeneralUtils.toISO8601DateTime(startedAt);
    }

    /**
     * See {@link #BatchInfo(String, Calendar)}.
     * {@code startedAt} defaults to the current time.
     *
     * @param name The name of the batch.
     */
    public BatchInfo(String name) {
        this(name, Calendar.getInstance(TimeZone.getTimeZone(BATCH_TIMEZONE)));
    }

    /**
     * @return The name of the batch or {@code null} if anonymous.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The id of the current batch.
     */
    public String getId () {
        return id;
    }

    /**
     * Sets a unique identifier for the batch. Sessions with batch info which
     * includes the same ID will be grouped together.
     * @param id The batch's ID
     */
    public void setId (String id) {
        ArgumentGuard.notNullOrEmpty(id, "id");
        this.id = id;
    }

    /**
     * @return The batch start date and time in ISO 8601 format.
     */
    @SuppressWarnings("UnusedDeclaration")
    @JsonSerialize(using = Iso8610CalendarSerializer.class)
    public Calendar getStartedAt() {
        try {
            return GeneralUtils.fromISO8601DateTime(startedAt);
        } catch (ParseException ex) {
            throw new EyesException("Failed to parse batch start time", ex);
        }
    }

    @Override
    public String toString() {
        return "'" + name + "' - " + startedAt;
    }
}