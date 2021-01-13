package com.applitools.eyes.services;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.EyesException;
import com.applitools.eyes.Logger;
import com.applitools.eyes.SyncTaskListener;
import com.applitools.eyes.TaskListener;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.logging.Type;
import com.applitools.eyes.visualgrid.model.HashObject;
import com.applitools.eyes.visualgrid.model.RGridDom;
import com.applitools.eyes.visualgrid.model.RGridResource;
import com.applitools.eyes.visualgrid.services.ServiceTaskListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class PutResourceService extends EyesService<RGridDom, RGridDom> {

    final Map<String, SyncTaskListener<Void>> uploadedResourcesCache = Collections.synchronizedMap(new HashMap<String, SyncTaskListener<Void>>());

    public PutResourceService(Logger logger, ServerConnector serverConnector) {
        super(logger, serverConnector);
    }

    @Override
    public void run() {
        while (!inputQueue.isEmpty()) {
            final Pair<String, RGridDom> nextInput = inputQueue.remove(0);
            final RGridDom dom = nextInput.getRight();
            ServiceTaskListener<List<RGridResource>> checkResourceListener = new ServiceTaskListener<List<RGridResource>>() {
                @Override
                public void onComplete(List<RGridResource> resources) {
                    try {
                        uploadResources(dom.getTestIds(), resources);
                    } catch (Throwable t) {
                        onFail(t);
                        return;
                    }

                    outputQueue.add(Pair.of(nextInput.getLeft(), dom));
                }

                @Override
                public void onFail(Throwable t) {
                    errorQueue.add(Pair.of(nextInput.getLeft(), t));
                }
            };

            try {
                checkResourcesStatus(dom, checkResourceListener);
            } catch (Throwable t) {
                checkResourceListener.onFail(t);
            }
        }
    }

    /**
     * Checks with the server what resources are missing.
     */
    void checkResourcesStatus(final RGridDom dom, final ServiceTaskListener<List<RGridResource>> listener) throws JsonProcessingException {
        List<HashObject> hashesToCheck = new ArrayList<>();
        final Map<String, String> hashToResourceUrl = new HashMap<>();
        final Map<String, RGridResource> resourceMap = dom.getResources();
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
                            if ((resourceUrl == null || resourceUrl.equals(domResource.getUrl())) && hash.equals(domResource.getSha256())) {
                                resource = domResource;
                            } else {
                                resource = resourceMap.get(resourceUrl);
                            }

                            resource.resetContent();
                            uploadedResourcesCache.put(resource.getSha256(), null);
                        }
                        continue;
                    }

                    if ((resourceUrl == null || resourceUrl.equals(domResource.getUrl())) && hash.equals(domResource.getSha256())) {
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
