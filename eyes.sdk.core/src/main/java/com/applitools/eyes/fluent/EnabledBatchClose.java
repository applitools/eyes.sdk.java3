package com.applitools.eyes.fluent;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.Logger;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.exceptions.StaleElementReferenceException;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.Type;
import com.applitools.eyes.universal.CommandExecutor;
import com.applitools.eyes.universal.USDKListener;
import com.applitools.eyes.universal.dto.CloseBatchSettingsDto;
import com.applitools.eyes.universal.mapper.SettingsMapper;
import com.applitools.eyes.universal.server.UniversalSdkNativeLoader;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.ClassVersionGetter;
import com.applitools.utils.GeneralUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;

public class EnabledBatchClose extends BatchClose {

    /**
     * name of the client sdk
     */
    protected static String BASE_AGENT_ID = "eyes.sdk.java";

    /**
     * version of the client sdk
     */
    protected static String VERSION = ClassVersionGetter.CURRENT_VERSION;

    private List<String> batchIds;

    EnabledBatchClose(Logger logger, String serverUrl, List<String> batchIds) {
        super(logger);
        this.serverUrl = serverUrl;
        this.batchIds = batchIds;
    }

    @Override
    public EnabledBatchClose setUrl(String url) {
        ArgumentGuard.notNull(url, "url");
        serverUrl = url;
        return this;
    }

    @Override
    public EnabledBatchClose setApiKey(String apiKey) {
        ArgumentGuard.notNull(apiKey, "apiKey");
        this.apiKey = apiKey;
        return this;
    }

    @Override
    public EnabledBatchClose setProxy(ProxySettings proxySettings) {
        ArgumentGuard.notNull(proxySettings, "proxySettings");
        this.proxySettings = proxySettings;
        return this;
    }

    @Override
    public EnabledBatchClose setBatchId(List<String> batchIds) {
        ArgumentGuard.notNull(batchIds, "batchIds");
        ArgumentGuard.notContainsNull(batchIds, "batchIds");
        this.batchIds = batchIds;
        return this;
    }

    /**
     * close an open batch.
     * This
     */
    public void close() {
        logger.log(new HashSet<String>(), Stage.CLOSE, Type.CLOSE_BATCH, Pair.of("batchSize", batchIds.size()));

        if (batchIds == null || batchIds.isEmpty()) {
            return;
        }

        Boolean shouldStopServer = startServer();
        List<CloseBatchSettingsDto> dto = SettingsMapper.toCloseBatchSettingsDto(batchIds, apiKey, serverUrl, proxySettings);
        CommandExecutor.closeBatch(dto);
        stopServer(shouldStopServer);
    }

    private Boolean startServer() {
        boolean shouldStopServer = true;

        // check if server is alive
        try {
            Field nativeProcess = UniversalSdkNativeLoader.class.getDeclaredField("nativeProcess");
            nativeProcess.setAccessible(true);
            Object isNativeProcess = nativeProcess.get(UniversalSdkNativeLoader.class);
            if (isNativeProcess != null) {
                shouldStopServer = false;
            }
            nativeProcess.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // start the server only if it's not alive to avoid runner conflicts
        if (shouldStopServer) {
            new EyesRunner(BASE_AGENT_ID, VERSION, USDKListener.getInstance()) {
                @Override
                public StaleElementReferenceException getStaleElementException() {
                    return null;
                }
            };
        }


        return shouldStopServer;
    }

    private void stopServer(Boolean shouldStopServer) {
        if (shouldStopServer) {
            try {
                Method stopServer = UniversalSdkNativeLoader.class.getDeclaredMethod("destroyProcess");
                stopServer.setAccessible(true);
                stopServer.invoke(UniversalSdkNativeLoader.class);
                stopServer.setAccessible(false);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                String errorMessage = GeneralUtils.createErrorMessageFromExceptionWithText(e, "Failed to stop server!");
                System.out.println(errorMessage);
                throw new EyesException(errorMessage, e);
            }
        }
    }
}
