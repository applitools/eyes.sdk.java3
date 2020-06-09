package com.applitools.eyes.metadata;

import java.util.Calendar;
import java.util.List;

import com.applitools.utils.Iso8610CalendarDeserializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "image",
    "thumbprint",
    "imageMatchSettings",
    "ignoreExpectedOutputSettings",
    "isMatching",
    "areImagesMatching",
    "occurredAt",
    "userInputs",
    "windowTitle",
    "tag",
    "isPrimary"
})
public class ActualAppOutput {

    @JsonProperty("image")
    private Image image;
    @JsonProperty("thumbprint")
    private Image thumbprint;
    @JsonProperty("imageMatchSettings")
    private ImageMatchSettings imageMatchSettings;
    @JsonProperty("ignoreExpectedOutputSettings")
    private Boolean ignoreExpectedOutputSettings;
    @JsonProperty("isMatching")
    private Boolean isMatching;
    @JsonProperty("areImagesMatching")
    private Boolean areImagesMatching;

    @JsonProperty("occurredAt")
    @JsonDeserialize(using = Iso8610CalendarDeserializer.class)
    private Calendar occurredAt;

    @JsonProperty("userInputs")
    private List<Object> userInputs = null;
    @JsonProperty("windowTitle")
    private String windowTitle;
    @JsonProperty("tag")
    private String tag;
    @JsonProperty("isPrimary")
    private Boolean isPrimary;

    @JsonProperty("image")
    public Image getImage() {
        return image;
    }

    @JsonProperty("image")
    public void setImage(Image image) {
        this.image = image;
    }

    @JsonProperty("thumbprint")
    public Image getThumbprint() {
        return thumbprint;
    }

    @JsonProperty("thumbprint")
    public void setThumbprint(Image thumbprint) {
        this.thumbprint = thumbprint;
    }

    @JsonProperty("imageMatchSettings")
    public ImageMatchSettings getImageMatchSettings() {
        return imageMatchSettings;
    }

    @JsonProperty("imageMatchSettings")
    public void setImageMatchSettings(ImageMatchSettings imageMatchSettings) {
        this.imageMatchSettings = imageMatchSettings;
    }

    @JsonProperty("ignoreExpectedOutputSettings")
    public Boolean getIgnoreExpectedOutputSettings() {
        return ignoreExpectedOutputSettings;
    }

    @JsonProperty("ignoreExpectedOutputSettings")
    public void setIgnoreExpectedOutputSettings(Boolean ignoreExpectedOutputSettings) {
        this.ignoreExpectedOutputSettings = ignoreExpectedOutputSettings;
    }

    @JsonProperty("isMatching")
    public Boolean getIsMatching() {
        return isMatching;
    }

    @JsonProperty("isMatching")
    public void setIsMatching(Boolean isMatching) {
        this.isMatching = isMatching;
    }

    @JsonProperty("areImagesMatching")
    public Boolean getAreImagesMatching() {
        return areImagesMatching;
    }

    @JsonProperty("areImagesMatching")
    public void setAreImagesMatching(Boolean areImagesMatching) {
        this.areImagesMatching = areImagesMatching;
    }

    @JsonProperty("occurredAt")
    public Calendar getOccurredAt() {
        return occurredAt;
    }

    @JsonProperty("occurredAt")
    public void setOccurredAt(Calendar occurredAt) {
        this.occurredAt = occurredAt;
    }

    @JsonProperty("userInputs")
    public List<Object> getUserInputs() {
        return userInputs;
    }

    @JsonProperty("userInputs")
    public void setUserInputs(List<Object> userInputs) {
        this.userInputs = userInputs;
    }

    @JsonProperty("windowTitle")
    public String getWindowTitle() {
        return windowTitle;
    }

    @JsonProperty("windowTitle")
    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    @JsonProperty("tag")
    public String getTag() {
        return tag;
    }

    @JsonProperty("tag")
    public void setTag(String tag) {
        this.tag = tag;
    }

    @JsonProperty("isPrimary")
    public Boolean getIsPrimary() {
        return isPrimary;
    }

    @JsonProperty("isPrimary")
    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

}
