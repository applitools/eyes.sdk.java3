
package com.applitools.eyes.metadata;

import com.applitools.utils.Iso8610CalendarDeserializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Calendar;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "name",
        "startedAt",
        "batchSequenceName"
})
public class BatchInfo {

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;

    @JsonProperty("batchSequenceName")
    private String sequenceName;

    @JsonProperty("startedAt")
    @JsonDeserialize(using = Iso8610CalendarDeserializer.class)
    private Calendar startedAt;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("startedAt")
    public Calendar getStartedAt() {
        return startedAt;
    }

    @JsonProperty("startedAt")
    public void setStartedAt(Calendar startedAt) {
        this.startedAt = startedAt;
    }

    @JsonProperty("batchSequenceName")
    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }
}
