package com.applitools.eyes.services;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.EyesException;
import com.applitools.eyes.Logger;
import com.applitools.eyes.SyncTaskListener;
import com.applitools.eyes.TaskListener;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.logging.Type;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.visualgrid.services.ServiceTaskListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class ResourceCollectionService extends EyesService<FrameData, Map<String, RGridResource>> {
    final Map<String, RGridResource> resourcesCacheMap;
    private IDebugResourceWriter debugResourceWriter;

    final Map<String, SyncTaskListener<Void>> uploadedResourcesCache = Collections.synchronizedMap(new HashMap<String, SyncTaskListener<Void>>());

    final Map<String, DomAnalyzer> tasksInDomAnalyzingProcess = Collections.synchronizedMap(new HashMap<String, DomAnalyzer>());
    protected final List<Pair<String, Pair<RGridDom, Map<String, RGridResource>>>> waitingForUploadQueue =
            Collections.synchronizedList(new ArrayList<Pair<String, Pair<RGridDom, Map<String, RGridResource>>>>());

    public ResourceCollectionService(Logger logger, ServerConnector serverConnector, IDebugResourceWriter debugResourceWriter,
                                     Map<String, RGridResource> resourcesCacheMap) {
        super(logger, serverConnector);
        this.debugResourceWriter = debugResourceWriter != null ? debugResourceWriter : new NullDebugResourceWriter();
        this.resourcesCacheMap = resourcesCacheMap;
    }

    public void setDebugResourceWriter(IDebugResourceWriter debugResourceWriter) {
        this.debugResourceWriter = debugResourceWriter != null ? debugResourceWriter : new NullDebugResourceWriter();
    }

    @Override
    public void run() {
        while (!inputQueue.isEmpty()) {
            final Pair<String, FrameData> nextInput = inputQueue.remove(0);
            final FrameData frameData = nextInput.getRight();
            DomAnalyzer domAnalyzer = new DomAnalyzer(logger, serverConnector, debugResourceWriter, frameData, resourcesCacheMap, new TaskListener<Map<String, RGridResource>>() {
                @Override
                public void onComplete(final Map<String, RGridResource> resourceMap) {
                    RGridDom dom = new RGridDom(frameData.getCdt(), resourceMap, frameData.getUrl());
                    dom.setTestIds(frameData.getTestIds());
                    waitingForUploadQueue.add(Pair.of(nextInput.getLeft(), Pair.of(dom, resourceMap)));
                    tasksInDomAnalyzingProcess.remove(nextInput.getLeft());
                }

                @Override
                public void onFail() {
                    errorQueue.add(Pair.<String, Throwable>of(nextInput.getLeft(), new EyesException("Dom analyzer failed")));
                    tasksInDomAnalyzingProcess.remove(nextInput.getLeft());
                }
            });

            tasksInDomAnalyzingProcess.put(nextInput.getLeft(), domAnalyzer);
        }

        List<DomAnalyzer> domAnalyzers;
        synchronized (tasksInDomAnalyzingProcess) {
            domAnalyzers = new ArrayList<>(tasksInDomAnalyzingProcess.values());
        }

        for (DomAnalyzer domAnalyzer : domAnalyzers) {
            domAnalyzer.run();
        }

        while (!waitingForUploadQueue.isEmpty()) {
            final Pair<String, Pair<RGridDom, Map<String, RGridResource>>> nextInput = waitingForUploadQueue.remove(0);
            final Pair<RGridDom, Map<String, RGridResource>> pair = nextInput.getRight();
            ServiceTaskListener<List<RGridResource>> checkResourceListener = new ServiceTaskListener<List<RGridResource>>() {
                @Override
                public void onComplete(List<RGridResource> resources) {
                    try {
                        uploadResources(pair.getLeft().getTestIds(), resources);
                    } catch (Throwable t) {
                        onFail(t);
                        return;
                    }

                    outputQueue.add(Pair.of(nextInput.getLeft(), pair.getRight()));
                }

                @Override
                public void onFail(Throwable t) {
                    errorQueue.add(Pair.of(nextInput.getLeft(), t));
                }
            };

            try {
                checkResourcesStatus(pair.getLeft(), pair.getRight(), checkResourceListener);
            } catch (Throwable t) {
                checkResourceListener.onFail(t);
            }
        }
    }

    /**
     * Checks with the server what resources are missing.
     */
    void checkResourcesStatus(final RGridDom dom, final Map<String, RGridResource> resourceMap,
                              final ServiceTaskListener<List<RGridResource>> listener) throws JsonProcessingException {
        List<HashObject> hashesToCheck = new ArrayList<>();
        final Map<String, String> hashToResourceUrl = new HashMap<>();
        for (RGridResource  resource : resourceMap.values()) {
            String url = resource.getUrl();
            String hash = resource.getSha256();
            String hashFormat = resource.getHashFormat();
            synchronized (uploadedResourcesCache) {
                if (!uploadedResourcesCache.containsKey(hash)) {
                    hashesToCheck.add(new HashObject(hashFormat, hash));
                    hashToResourceUrl.put(hash, url);
                }
            }
        }

        final RGridResource domResource = dom.asResource();
        synchronized (uploadedResourcesCache) {
            if (!uploadedResourcesCache.containsKey(domResource.getSha256())) {
                hashesToCheck.add(new HashObject(domResource.getHashFormat(), domResource.getSha256()));
                hashToResourceUrl.put(domResource.getSha256(), domResource.getUrl());
            }
        }

        if (hashesToCheck.isEmpty()) {
            listener.onComplete(new ArrayList<RGridResource>());
            return;
        }

        final HashObject[] hashesArray = hashesToCheck.toArray(new HashObject[0]);
        serverConnector.checkResourceStatus(new TaskListener<Boolean[]>() {
            @Override
            public void onComplete(Boolean[] result) {
                if (result == null) {
                    onFail();
                    return;
                }

                logger.log(TraceLevel.Info, dom.getTestIds(), Stage.RESOURCE_COLLECTION, Type.CHECK_RESOURCE, Pair.of("result", result));
                // Analyzing the server response and find the missing resources
                List<RGridResource> missingResources = new ArrayList<>();
                for (int i = 0; i < result.length; i++) {
                    String hash = hashesArray[i].getHash();
                    String resourceUrl = hashToResourceUrl.get(hash);
                    if (result[i] != null && result[i]) {
                        synchronized (uploadedResourcesCache) {
                            RGridResource resource;
                            if (resourceUrl.equals(domResource.getUrl()) && hash.equals(domResource.getSha256())) {
                                resource = domResource;
                            } else {
                                resource = resourceMap.get(resourceUrl);
                            }

                            resource.resetContent();
                            uploadedResourcesCache.put(resource.getSha256(), null);
                        }
                        continue;
                    }

                    if (resourceUrl.equals(domResource.getUrl()) && hash.equals(domResource.getSha256())) {
                        missingResources.add(domResource);
                        continue;
                    }

                    missingResources.add(resourceMap.get(resourceUrl));
                }

                listener.onComplete(missingResources);
            }

            @Override
            public void onFail() {
                listener.onFail(new EyesException("Failed checking resources with the server"));
            }
        }, dom.getTestIds(), null, hashesArray);
    }

    void uploadResources(Set<String> testIds, List<RGridResource> resources) {
        logger.log(TraceLevel.Info, testIds, Stage.RESOURCE_COLLECTION, Type.UPLOAD_RESOURCE, Pair.of("resources", resources));
        for (RGridResource resource : resources) {
            synchronized (uploadedResourcesCache) {
                if (uploadedResourcesCache.containsKey(resource.getSha256())) {
                    continue;
                }

                SyncTaskListener<Void> listener = new SyncTaskListener<>(logger, String.format("uploadResource %s %s", resource.getSha256(), resource.getUrl()));
                serverConnector.renderPutResource(testIds, "NONE", resource, listener);
                uploadedResourcesCache.put(resource.getSha256(), listener);
            }
        }

        // Wait for all resources to be uploaded
        for (RGridResource resource : resources) {
            // A blocking call
            SyncTaskListener<Void> listener = uploadedResourcesCache.get(resource.getSha256());
            if (listener != null) {
                listener.get();
            }
        }

        logger.log(TraceLevel.Info, testIds, Stage.RESOURCE_COLLECTION, Type.UPLOAD_RESOURCE, Pair.of("completed", true));
    }
}
