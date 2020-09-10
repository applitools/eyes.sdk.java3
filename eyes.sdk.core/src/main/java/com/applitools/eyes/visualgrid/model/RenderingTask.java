package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.Logger;
import com.applitools.eyes.visualgrid.services.IEyesConnector;
import com.applitools.eyes.visualgrid.services.VisualGridTask;
import com.applitools.utils.GeneralUtils;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public class RenderingTask implements Callable<RenderStatusResults> {

    public static final int HOUR = 60 * 60 * 1000;

    private final RenderTaskListener listener;
    private final IEyesConnector eyesConnector;
    final List<RenderRequest> renderRequests = new ArrayList<>();
    private final List<VisualGridTask> checkTasks = new ArrayList<>();
    private final Logger logger;
    private final Timer timer = new Timer("VG_StopWatch", true);
    private final AtomicBoolean isTimeElapsed = new AtomicBoolean(false);

    public interface RenderTaskListener {
        void onRenderSuccess();

        void onRenderFailed(Exception e);
    }

    public RenderingTask(Logger logger, IEyesConnector eyesConnector, RenderRequest renderRequest,
                         VisualGridTask checkTask, RenderTaskListener listener) {
        this.logger = logger;
        this.eyesConnector = eyesConnector;
        this.renderRequests.add(renderRequest);
        this.checkTasks.add(checkTask);
        this.listener = listener;
    }

    public void merge(RenderingTask renderingTask) {
        renderRequests.addAll(renderingTask.renderRequests);
        checkTasks.addAll(renderingTask.checkTasks);
    }

    @Override
    public RenderStatusResults call() {
        try {
            logger.verbose("enter");

            logger.verbose("Start rendering");
            List<RunningRender> runningRenders;
            RenderRequest[] asArray = renderRequests.toArray(new RenderRequest[0]);
            try {
                runningRenders = this.eyesConnector.render(asArray);
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
                logger.verbose("/render throws exception... sleeping for 1.5s");
                logger.verbose("ERROR " + e.getMessage());
                Thread.sleep(1500);
                try {
                    runningRenders = this.eyesConnector.render(asArray);
                } catch (Exception e1) {
                    setRenderErrorToTasks();
                    throw new EyesException("Invalid response for render request", e1);
                }
            }

            logger.verbose("Validation render result");
            if (runningRenders == null || runningRenders.size() == 0) {
                setRenderErrorToTasks();
                throw new EyesException("Invalid response for render request");
            }

            for (int i = 0; i < renderRequests.size(); i++) {
                RenderRequest request = renderRequests.get(i);
                request.setRenderId(runningRenders.get(i).getRenderId());
                logger.verbose(String.format("RunningRender: %s", runningRenders.get(i)));
            }

            for (RunningRender runningRender : runningRenders) {
                RenderStatus renderStatus = runningRender.getRenderStatus();
                if (!renderStatus.equals(RenderStatus.RENDERED) && !renderStatus.equals(RenderStatus.RENDERING)) {
                    setRenderErrorToTasks();
                    throw new EyesException(String.format("Invalid response for render request. Status: %s", renderStatus));
                }
            }

            logger.verbose("Poll rendering status");
            Map<RunningRender, RenderRequest> mapping = mapRequestToRunningRender(runningRenders);
            pollRenderingStatus(mapping);
        } catch (Throwable e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
            for (VisualGridTask checkTask : checkTasks) {
                checkTask.setExceptionAndAbort(e);
            }
            listener.onRenderFailed(new EyesException("Failed rendering", e));
        }

        logger.verbose("Finished rendering task - exit");
        return null;
    }

    private void setRenderErrorToTasks() {
        for (RenderRequest renderRequest : renderRequests) {
            renderRequest.getCheckTask().setRenderError(null, "Invalid response for render request");
        }
    }

    private Map<RunningRender, RenderRequest> mapRequestToRunningRender(List<RunningRender> runningRenders) {
        Map<RunningRender, RenderRequest> mapping = new HashMap<>();
        for (int i = 0; i < renderRequests.size(); i++) {
            mapping.put(runningRenders.get(i), renderRequests.get(i));
        }
        return mapping;
    }

    private List<String> getRenderIds(Collection<RunningRender> runningRenders) {
        List<String> ids = new ArrayList<>();
        for (RunningRender runningRender : runningRenders) {
            ids.add(runningRender.getRenderId());
        }
        return ids;
    }

    private void pollRenderingStatus(Map<RunningRender, RenderRequest> runningRenders) {
        logger.verbose("enter");
        List<String> ids = getRenderIds(runningRenders.keySet());
        logger.verbose("render ids : " + ids);
        timer.schedule(new TimeoutTask(), HOUR);
        do {
            List<RenderStatusResults> renderStatusResultsList;
            try {
                renderStatusResultsList = this.eyesConnector.renderStatusById(ids.toArray(new String[0]));
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
                continue;
            }
            if (renderStatusResultsList == null || renderStatusResultsList.isEmpty()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
                continue;
            }

            sampleRenderingStatus(runningRenders, ids, renderStatusResultsList);

            if (ids.size() > 0) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
            }

        } while (!ids.isEmpty() && !isTimeElapsed.get());

        timer.cancel();
        if (!ids.isEmpty()) {
            logger.verbose("Render ids that didn't complete in time : ");
            logger.verbose(ids.toString());
        }

        for (String id : ids) {
            for (Map.Entry<RunningRender,RenderRequest> kvp : runningRenders.entrySet()) {
                RunningRender renderedRender = kvp.getKey();
                RenderRequest renderRequest = kvp.getValue();
                String renderId = renderedRender.getRenderId();
                if (renderId.equalsIgnoreCase(id)) {
                    logger.verbose("removing failed render id: " + id);
                    VisualGridTask checkTask = renderRequest.getCheckTask();
                    checkTask.setRenderError(id, "too long rendering(rendering exceeded 150 sec)");
                    break;
                }
            }
        }

        logger.verbose("marking task as complete");
        listener.onRenderSuccess();
        logger.verbose("exit");
    }

    private void sampleRenderingStatus(Map<RunningRender, RenderRequest> runningRenders, List<String> ids, List<RenderStatusResults> renderStatusResultsList) {
        logger.verbose("enter - renderStatusResultsList.size: " + renderStatusResultsList.size());

        for (int i = 0, j = 0; i < renderStatusResultsList.size(); i++) {
            RenderStatusResults renderStatusResults = renderStatusResultsList.get(i);
            if (renderStatusResults == null) {
                renderStatusResults = new RenderStatusResults();
                renderStatusResults.setStatus(RenderStatus.ERROR);
                renderStatusResults.setError("Render status result was null");
                renderStatusResults.setRenderId(ids.get(j));
            }

            RenderStatus renderStatus = renderStatusResults.getStatus();
            boolean isRenderedStatus = renderStatus == RenderStatus.RENDERED;
            boolean isErrorStatus = renderStatus == RenderStatus.ERROR;
            logger.verbose("renderStatusResults - " + renderStatusResults);
            if (isRenderedStatus || isErrorStatus) {
                String removedId = ids.remove(j);
                for (Map.Entry<RunningRender, RenderRequest> kvp: runningRenders.entrySet()) {
                    RunningRender renderedRender = kvp.getKey();
                    RenderRequest renderRequest = kvp.getValue();
                    String renderId = renderedRender.getRenderId();
                    if (renderId.equalsIgnoreCase(removedId)) {
                        VisualGridTask checkTask = renderRequest.getCheckTask();
                        logger.verbose("setting visualGridTask " + checkTask + " render result: " + renderStatusResults);
                        String error = renderStatusResults.getError();
                        if (error != null) {
                            GeneralUtils.logExceptionStackTrace(logger, new Exception(error));
                            checkTask.setRenderError(renderId, error);
                        } else {
                            checkTask.setRenderResult(renderStatusResults);
                        }
                        break;
                    }
                }
            } else {
                j++;
            }
        }
        logger.verbose("exit");
    }

    public boolean isReady() {
        for (VisualGridTask checkTask : checkTasks) {
            if (!checkTask.isReadyForRender()) {
                return false;
            }
        }

        return true;
    }

    private class TimeoutTask extends TimerTask {
        @Override
        public void run() {
            logger.verbose("VG is Timed out!");
            isTimeElapsed.set(true);
        }
    }
}
