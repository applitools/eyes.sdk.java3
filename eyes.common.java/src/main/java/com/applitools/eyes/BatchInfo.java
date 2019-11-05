package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.applitools.utils.Iso8610CalendarSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("id")
    private String id;
    @JsonProperty("batchSequenceName")
    private String sequenceName;
    @JsonProperty("name")
    private final String name;
    @JsonProperty("startedAt")
    private String startedAt;
    @JsonProperty("notifyOnCompletion")
    private boolean notifyOnCompletion;

    @JsonProperty("isCompleted")
    private boolean isCompleted = false;

    /**
     * Creates a new BatchInfo instance.
     *
     * @param name      Name of batch or {@code null} if anonymous.
     * @param startedAt Batch start time
     */
    public BatchInfo(String name, Calendar startedAt) {
        ArgumentGuard.notNull(startedAt, "startedAt");
        String envVarBatchId = GeneralUtils.getEnvString("APPLITOOLS_BATCH_ID");
        String envSequenceName = GeneralUtils.getEnvString("APPLITOOLS_BATCH_SEQUENCE");
        this.id = envVarBatchId != null ? envVarBatchId : UUID.randomUUID().toString();
        this.name = name != null ? name : GeneralUtils.getEnvString("APPLITOOLS_BATCH_NAME");
        String env_batch_notify = GeneralUtils.getEnvString("APPLITOOLS_BATCH_NOTIFY");
        this.notifyOnCompletion = Boolean.parseBoolean(env_batch_notify);
        this.sequenceName = envSequenceName;
        this.startedAt = GeneralUtils.toISO8601DateTime(startedAt);
    }

    public BatchInfo(String id, String batchSequenceName, String name, String startedAt) {
        this.id = id;
        this.sequenceName = batchSequenceName;
        this.name = name;
        this.startedAt = startedAt;
    }

    public BatchInfo() {
        this(null);
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
    @JsonProperty("id")
    public void setId (String id) {
        ArgumentGuard.notNullOrEmpty(id, "id");
        this.id = id;
    }

    /**
     * Sets a unique identifier for the batch and allows chaining of the id
     * with the instance then returns that instance. Sessions with batch
     * info which includes the same ID will be grouped together.
     *
     * @param id The batch's ID
     * @return The updated {@link BatchInfo} instance.
     */
    public BatchInfo withBatchId(String id) {
        ArgumentGuard.notNullOrEmpty(id, "id");
        this.id = id;
        return this;
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

    @JsonProperty("startedAt")
    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    @Override
    public String toString() {
        return "'" + name + "' - " + startedAt;
    }

    @JsonProperty("batchSequenceName")
    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof BatchInfo)) return false;
        BatchInfo other = (BatchInfo) obj;
        return this.id.equals(other.id) && this.name.equals(other.name) && this.sequenceName.equals(other.sequenceName) && this.startedAt. equals(other.startedAt);
    }

    @JsonProperty("notifyOnCompletion")
    public boolean isNotifyOnCompletion() {
        return notifyOnCompletion;
    }

    @JsonProperty("notifyOnCompletion")
    public void setNotifyOnCompletion(boolean notifyOnCompletion) {
        this.notifyOnCompletion = notifyOnCompletion;
    }

    @JsonIgnore
    public boolean isCompleted() {
        return isCompleted;
    }

    @JsonInclude
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}