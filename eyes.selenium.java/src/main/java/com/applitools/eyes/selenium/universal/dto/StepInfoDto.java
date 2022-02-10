package com.applitools.eyes.selenium.universal.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * step info dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StepInfoDto {

  public static class AppUrls {
    private String step;
    private String stepEditor;

    public String getStep() {
      return step;
    }

    public void setStep(String step) {
      this.step = step;
    }

    public String getStepEditor() {
      return stepEditor;
    }

    public void setStepEditor(String stepEditor) {
      this.stepEditor = stepEditor;
    }
  }

  public static class ApiUrls {
    private String baselineImage;
    private String currentImage;
    private String diffImage;
    private String checkpointImage;
    private String checkpointImageThumbnail;

    public String getBaselineImage() {
      return baselineImage;
    }

    public void setBaselineImage(String baselineImage) {
      this.baselineImage = baselineImage;
    }

    public String getCurrentImage() {
      return currentImage;
    }

    public void setCurrentImage(String currentImage) {
      this.currentImage = currentImage;
    }

    public String getDiffImage() {
      return diffImage;
    }

    public void setDiffImage(String diffImage) {
      this.diffImage = diffImage;
    }

    public String getCheckpointImage() {
      return checkpointImage;
    }

    public void setCheckpointImage(String checkpointImage) {
      this.checkpointImage = checkpointImage;
    }

    public String getCheckpointImageThumbnail() {
      return checkpointImageThumbnail;
    }

    public void setCheckpointImageThumbnail(String checkpointImageThumbnail) {
      this.checkpointImageThumbnail = checkpointImageThumbnail;
    }
  }

  private String name;
  private Boolean isDifferent;
  private Boolean hasBaselineImage;
  private Boolean hasCurrentImage;
  private StepInfoDto.ApiUrls apiUrls;
  private StepInfoDto.AppUrls appUrls;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getDifferent() {
    return isDifferent;
  }

  public void setDifferent(Boolean different) {
    isDifferent = different;
  }

  public Boolean getHasBaselineImage() {
    return hasBaselineImage;
  }

  public void setHasBaselineImage(Boolean hasBaselineImage) {
    this.hasBaselineImage = hasBaselineImage;
  }

  public Boolean getHasCurrentImage() {
    return hasCurrentImage;
  }

  public void setHasCurrentImage(Boolean hasCurrentImage) {
    this.hasCurrentImage = hasCurrentImage;
  }

  public StepInfoDto.ApiUrls getApiUrls() {
    return apiUrls;
  }

  public void setApiUrls(StepInfoDto.ApiUrls apiUrls) {
    this.apiUrls = apiUrls;
  }

  public StepInfoDto.AppUrls getAppUrls() {
    return appUrls;
  }

  public void setAppUrls(StepInfoDto.AppUrls appUrls) {
    this.appUrls = appUrls;
  }

}
