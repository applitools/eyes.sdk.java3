package com.applitools.eyes.images;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.exceptions.StaleElementReferenceException;
import com.applitools.eyes.images.universal.mapper.ImageStaleElementReferenceException;
import com.applitools.eyes.universal.ManagerType;
import com.applitools.utils.ClassVersionGetter;

public class ImageRunner extends EyesRunner {
    /**
     * name of the client sdk
     */
    protected static String BASE_AGENT_ID = "eyes.sdk.images";

    /**
     * version of the client sdk
     */
    protected static String VERSION = ClassVersionGetter.CURRENT_VERSION;

    /**
     * used for instantiating Image Runner
     */
    public ImageRunner() {
        this(BASE_AGENT_ID, VERSION);
    }

    /**
     * used for instantiating Image Runner
     */
    public ImageRunner(String baseAgentId, String version) {
        super(baseAgentId, version);
        //TODO - check if baseAgentId is the correct agentId here
        managerRef = commandExecutor.coreMakeManager(ManagerType.CLASSIC.value, null, null, baseAgentId);
    }

    @Override
    public StaleElementReferenceException getStaleElementException() {
        return new ImageStaleElementReferenceException();
    }

}
