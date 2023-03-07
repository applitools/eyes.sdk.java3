package com.applitools.eyes.universal.mapper;

import com.applitools.eyes.CutProvider;
import com.applitools.eyes.FixedCutProvider;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.config.ContentInset;
import com.applitools.eyes.universal.dto.*;

import java.util.ArrayList;
import java.util.List;

public class SettingsMapper {

    public static OpenSettingsDto toOpenSettingsDto(Configuration config, Boolean keepBatchOpen) {
        if (config == null) {
            return null;
        }
        OpenSettingsDto dto = new OpenSettingsDto();

        dto.setServerUrl(config.getServerUrl() == null? null : config.getServerUrl().toString());
        dto.setApiKey(config.getApiKey());
        dto.setProxy(ProxyMapper.toProxyDto(config.getProxy()));
        dto.setAgentId(config.getAgentId());
        dto.setAppName(config.getAppName());
        dto.setTestName(config.getTestName());
        dto.setDisplayName(null);
        dto.setUserTestId(null);
        dto.setSessionType(config.getSessionType() == null ? null : config.getSessionType().name());
        dto.setProperties(CustomPropertyMapper.toCustomPropertyDtoList(config.getProperties()));
        dto.setBatch(BatchMapper.toBatchDto(config.getBatch()));
        dto.setKeepBatchOpen(keepBatchOpen);
        dto.setEnvironmentName(config.getEnvironmentName());
        dto.setEnvironment(AppEnvironmentMapper.toAppEnvironmentMapper(
                config.getHostOS(),
                config.getHostApp(),
                config.getViewportSize(),
                config.getDeviceInfo(),
                config.getOsInfo(),
                config.getHostingAppInfo()));

        dto.setBranchName(config.getBranchName());
        dto.setParentBranchName(config.getParentBranchName());
        dto.setBaselineEnvName(config.getBaselineEnvName());
        dto.setBaselineBranchName(config.getBaselineBranchName());
        dto.setCompareWithParentBranch(null); //TODO - why is this null?
        dto.setIgnoreBaseline(null);    //TODO - why is this null?
        dto.setIgnoreGitBranching(null); //TODO - former "ignoreGitMergeBase" doesn't exist
        dto.setSaveDiffs(config.getSaveDiffs());
        dto.setAbortIdleTestTimeout(config.getAbortIdleTestTimeout());

        return dto;
    }

    public static CloseSettingsDto toCloseSettingsDto(Configuration config) {
        if (config == null) {
            return null;
        }

        CloseSettingsDto closeSettingsDto = new CloseSettingsDto();

        closeSettingsDto.setUpdateBaselineIfNew(config.getSaveNewTests());
        closeSettingsDto.setUpdateBaselineIfDifferent(config.getSaveFailedTests());

        return closeSettingsDto;
    }

    public static NormalizationDto toNormalizationDto(ICut cut, Integer rotation, Double scaleRatio) {
        if (cut == null && rotation == null && scaleRatio == null) {
            return null;
        }

        NormalizationDto normalizationDto = new NormalizationDto();

        normalizationDto.setCut(cut);
        normalizationDto.setRotation(rotation);
        normalizationDto.setScaleRatio(scaleRatio);

        return normalizationDto;
    }

    public static ImageCropRectDto toImageCropRect(CutProvider cutProvider, ContentInset contentInset) {
        ImageCropRectDto imageCropRectDto = null;

        if (cutProvider != null) {
            FixedCutProvider fixed = (FixedCutProvider) cutProvider;
            imageCropRectDto = new ImageCropRectDto(fixed.getHeader(), fixed.getRight(), fixed.getFooter(), fixed.getLeft());
        } else if (contentInset != null) {
            imageCropRectDto =  new ImageCropRectDto(contentInset.getTop(), contentInset.getRight(), contentInset.getBottom(), contentInset.getLeft());
        }

        return imageCropRectDto;
    }

    public static DeleteTestSettingsDto toDeleteTestSettingsDto(TestResults testResults, String apiKey,
                                                                String serverUrl, ProxyDto proxy) {
        if (testResults == null) {
            return null;
        }

        DeleteTestSettingsDto deleteTestSettingsDto = new DeleteTestSettingsDto();

        deleteTestSettingsDto.setTestId(testResults.getId());
        deleteTestSettingsDto.setBatchId(testResults.getBatchId());
        deleteTestSettingsDto.setSecretToken(testResults.getSecretToken());
        deleteTestSettingsDto.setApiKey(apiKey);
        deleteTestSettingsDto.setServerUrl(serverUrl);
        deleteTestSettingsDto.setProxy(proxy);

        return deleteTestSettingsDto;
    }

    public static List<CloseBatchSettingsDto> toCloseBatchSettingsDto(List<String> batchIds, String apiKey, String serverUrl, ProxySettings proxySettings) {
        List<CloseBatchSettingsDto> dto = new ArrayList<>();
        for (String batchId : batchIds) {
            dto.add(new CloseBatchSettingsDto(batchId, apiKey, serverUrl, ProxyMapper.toProxyDto(proxySettings)));
        }

        return dto;
    }
}
