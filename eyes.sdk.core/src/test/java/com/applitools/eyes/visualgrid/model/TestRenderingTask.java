package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.UserAgent;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.visualgrid.services.IEyesConnector;
import com.applitools.eyes.visualgrid.services.VisualGridTask;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestRenderingTask extends ReportingTestSuite {

    public TestRenderingTask() {
        super.setGroupName("core");
    }

    @Test
    public void testAsyncDownloadResources() throws Exception {
        ExecutorService service = Executors.newCachedThreadPool();

        // get urls json
        File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("resource_urls.json")).getFile());
        String jsonString = GeneralUtils.readToEnd(new FileInputStream(file));
        ObjectMapper jsonMapper = new ObjectMapper();
        final Map<String, Map> urls = jsonMapper.readValue(jsonString, Map.class);

        // Arguments for fetchAllResources
        Set<URI> resourceUrls = stringsToUris(urls.keySet());
        Map<String, RGridResource> allBlobs = new HashMap<>();
        FrameData frameData = mock(FrameData.class);
        when(frameData.getUrl()).thenReturn("");

        // Mocking for RenderingTask
        VisualGridTask visualGridTask = mock(VisualGridTask.class);
        IEyesConnector eyesConnector = mock(IEyesConnector.class);
        final Future<?> future = mock(Future.class);
        when(future.get()).thenThrow(new IllegalStateException());
        when(future.get(anyLong(), any())).thenThrow(new IllegalStateException());
        when(visualGridTask.getEyesConnector()).thenReturn(eyesConnector);

        AtomicInteger counter = new AtomicInteger();
        final RenderingTask renderingTask = new RenderingTask(eyesConnector, Collections.singletonList(visualGridTask), new UserAgent());

        // When RenderingTask tries to get new resource, this task will be submitted to the ExecutorService
        when(eyesConnector.getResource(any(), any(), any(), any())).thenAnswer(invocationOnMock -> {
            try {
                service.submit(() -> {
                    synchronized (counter) {
                        counter.getAndIncrement();
                        URI url = invocationOnMock.getArgument(0);
                        Map innerUrls = getInnerMap(urls, url.toString());
                        try {
                            // Sleeping so the async tasks will take some time to finish
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new IllegalStateException(e);
                        }
                        if (!Objects.requireNonNull(innerUrls).isEmpty()) {
                            try {
                                renderingTask.fetchAllResources(allBlobs, stringsToUris(innerUrls.keySet()), frameData);
                            } catch (URISyntaxException e) {
                                throw new IllegalStateException(e);
                            }
                        }
                        renderingTask.resourcesPhaser.arriveAndDeregister();
                    }
                });
                return future;
            } catch (Exception e) {
                throw new Throwable(e);
            }
        });

        renderingTask.fetchAllResources(allBlobs, resourceUrls, frameData);
        renderingTask.resourcesPhaser.awaitAdvanceInterruptibly(0, 30, TimeUnit.SECONDS);
        Assert.assertEquals(counter.get(), 8);
    }

    private Map getInnerMap(Map<String, Map> outerMap, String key) {
        if (outerMap.containsKey(key)) {
            return outerMap.get(key);
        }

        for (String k : outerMap.keySet()) {
            if (outerMap.get(k).isEmpty()) {
                continue;
            }

            Map result = getInnerMap(outerMap.get(k), key);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    private Set<URI> stringsToUris(Set<String> urls) throws URISyntaxException {
        Set<URI> uris = new HashSet<>();
        for (String url : urls) {
            uris.add(new URI(url));
        }

        return uris;
    }
}
