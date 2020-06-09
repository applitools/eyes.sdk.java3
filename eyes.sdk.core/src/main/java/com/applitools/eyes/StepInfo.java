package com.applitools.eyes;

/**
 * Encapsulates step information model received by the tests results.
 */
public class StepInfo {

    public class AppUrls {
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

    public class ApiUrls {
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
    private boolean isDifferent;
    private boolean hasBaselineImage;
    private boolean hasCurrentImage;
    private boolean hasCheckpointImage;
    private ApiUrls apiUrls;
    private AppUrls appUrls;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsDifferent() {
        return isDifferent;
    }

    public void setIsDifferent(boolean different) {
        isDifferent = different;
    }

    public boolean getHasBaselineImage() {
        return hasBaselineImage;
    }

    public void setHasBaselineImage(boolean hasBaselineImage) {
        this.hasBaselineImage = hasBaselineImage;
    }

    public boolean getHasCurrentImage() {
        return hasCurrentImage;
    }

    public void setHasCurrentImage(boolean hasCurrentImage) {
        this.hasCurrentImage = hasCurrentImage;
    }

    public boolean isHasCheckpointImage() {
        return hasCheckpointImage;
    }

    public void setHasCheckpointImage(boolean hasCheckpointImage) {
        this.hasCheckpointImage = hasCheckpointImage;
    }

    public ApiUrls getApiUrls() {
        return apiUrls;
    }

    public void setApiUrls(ApiUrls apiUrls) {
        this.apiUrls = apiUrls;
    }

    public AppUrls getAppUrls() {
        return appUrls;
    }

    public void setAppUrls(AppUrls appUrls) {
        this.appUrls = appUrls;
    }
}
