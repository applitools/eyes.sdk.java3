package com.applitools.eyes.fluent;

import com.applitools.eyes.Logger;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.Type;
import com.applitools.eyes.universal.CommandExecutor;
import com.applitools.eyes.universal.dto.CloseBatchSettingsDto;
import com.applitools.eyes.universal.mapper.SettingsMapper;
import com.applitools.eyes.universal.server.UniversalSdkNativeLoader;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.ClassVersionGetter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.List;

public class EnabledBatchClose extends BatchClose {
    private List<String> batchIds;
    private String BASE_AGENT_ID = "eyes.sdk.java";

    EnabledBatchClose(Logger logger, String serverUrl, List<String> batchIds) {
        super(logger);
        UniversalSdkNativeLoader.setLogger(logger);
        UniversalSdkNativeLoader.start();
        CommandExecutor.getInstance(BASE_AGENT_ID, ClassVersionGetter.CURRENT_VERSION, null);
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

    public void close() {
        logger.log(new HashSet<String>(), Stage.CLOSE, Type.CLOSE_BATCH, Pair.of("batchSize", batchIds.size()));

        if (batchIds == null || batchIds.isEmpty()) {
            return;
        }

        List<CloseBatchSettingsDto> dto = SettingsMapper.toCloseBatchSettingsDto(batchIds, apiKey, serverUrl, proxySettings);
        CommandExecutor.closeBatch(dto);
    }
}
