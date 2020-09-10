package com.applitools.eyes.visualgrid.services;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class VisualGridRunner extends EyesRunner {
    static class TestConcurrency {
        final int userConcurrency;
        final int actualConcurrency;
        final boolean isLegacy;
        boolean isDefault = false;

        TestConcurrency() {
            isDefault = true;
            isLegacy = false;
            userConcurrency = DEFAULT_CONCURRENCY;
            actualConcurrency = DEFAULT_CONCURRENCY;
        }

        TestConcurrency(int userConcurrency, boolean isLegacy) {
            this.userConcurrency = userConcurrency;
            this.actualConcurrency = isLegacy ? userConcurrency * CONCURRENCY_FACTOR : userConcurrency;
            this.isLegacy = isLegacy;
        }
    }

    private static final int CONCURRENCY_FACTOR = 5;
    static final int DEFAULT_CONCURRENCY = 5;

    final TestConcurrency testConcurrency;
    private boolean wasConcurrencyLogSent = false;

    private OpenerService eyesOpenerService;
    private EyesService eyesCloserService;
    private EyesService eyesCheckerService;
    private EyesService resourceCollectionService;
    private RenderingGridService renderingGridService;
    private final ThreadGroup servicesGroup = new ThreadGroup("Services Group");
    private final List<IRenderingEyes> eyesToOpenList = Collections.synchronizedList(new ArrayList<IRenderingEyes>(200));
    final Set<IRenderingEyes> allEyes = Collections.synchronizedSet(new HashSet<IRenderingEyes>());
    private final Map<String, RGridResource> resourcesCacheMap = Collections.synchronizedMap(new HashMap<String, RGridResource>());
    private final Map<String, SyncTaskListener<Void>> uploadedResourcesCache = Collections.synchronizedMap(new HashMap<String, SyncTaskListener<Void>>());

    private final Object openerServiceConcurrencyLock = new Object();
    private final Object openerServiceLock = new Object();
    private final Object checkerServiceLock = new Object();
    private final Object closerServiceLock = new Object();
    private final Object resourceCollectionServiceLock = new Object();
    private final Object renderingServiceLock = new Object();
    private final List<ResourceCollectionTask> resourceCollectionTaskList = Collections.synchronizedList(new ArrayList<ResourceCollectionTask>());
    private final List<RenderingTask> renderingTaskList = Collections.synchronizedList(new ArrayList<RenderingTask>());

    private RenderingInfo renderingInfo;
    private IDebugResourceWriter debugResourceWriter;

    private String serverUrl = GeneralUtils.getEnvString("APPLITOOLS_SERVER_URL");
    private static final String DEFAULT_API_KEY = GeneralUtils.getEnvString("APPLITOOLS_API_KEY");
    private String apiKey = DEFAULT_API_KEY;
    private boolean isDisabled;
    private boolean isServicesOn = false;

    private String suiteName;

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServerUrl() {
        return this.serverUrl;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey != null ? apiKey : DEFAULT_API_KEY;
    }

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public boolean getIsDisabled() {
        return this.isDisabled;
    }

    public boolean isServicesOn() {
        return isServicesOn;
    }

    private void setServicesOn(boolean servicesOn) {
        isServicesOn = servicesOn;
    }

    public String getSuiteName() {
        return suiteName;
    }

    public void setSuiteName(String suiteName) {
        this.suiteName = suiteName;
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    private FutureTask<TestResultContainer> getOrWaitForTask(Object lock, EyesService.Tasker tasker) {
        FutureTask<TestResultContainer> nextTestToOpen = tasker.getNextTask();
        if (nextTestToOpen == null) {
            try {
                synchronized (lock) {
                    lock.wait(500);
                }
                nextTestToOpen = tasker.getNextTask();
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
        return nextTestToOpen;
    }

    private final IRenderingEyes.EyesListener eyesListener = new IRenderingEyes.EyesListener() {
        @Override
        public void onTaskComplete(VisualGridTask visualGridTask, IRenderingEyes eyes) {
            logger.verbose("Enter with: " + visualGridTask.getType());
            VisualGridTask.TaskType type = visualGridTask.getType();
            try {
                switch (type) {
                    case OPEN:
                        logger.verbose("locking eyesToOpenList");
                        synchronized (eyesToOpenList) {
                            logger.verbose("removing visualGridTask " + visualGridTask.toString());
                            eyesToOpenList.remove(eyes);
                        }
                        logger.verbose("releasing eyesToOpenList");
                        break;
                    case ABORT:
                        logger.verbose("VisualGridTask Abort.");
                    case CLOSE:
                        logger.verbose("VisualGridTask Close.");
                        eyesOpenerService.decrementConcurrency();
                        synchronized (openerServiceConcurrencyLock) {
                            openerServiceConcurrencyLock.notify();
                        }
                        logger.verbose("releasing openerServiceConcurrencyLock");
                        logger.verbose("VisualGridTask Close.");
                        break;
                    case CHECK:
                        logger.verbose("Check complete.");
                }
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }

            notifyAllServices();
        }

        @Override
        public void onRenderComplete() {
            notifyAllServices();
        }

    };

    public VisualGridRunner() {
        this(Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public VisualGridRunner(String suiteName) {
        this.testConcurrency = new TestConcurrency();
        init(suiteName);
    }

    public VisualGridRunner(int testConcurrency) {
        this(testConcurrency, Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public VisualGridRunner(int testConcurrency, String suiteName) {
        this.testConcurrency = new TestConcurrency(testConcurrency, true);
        init(suiteName);
    }

    public VisualGridRunner(RunnerOptions runnerOptions) {
        this(runnerOptions, Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public VisualGridRunner(RunnerOptions runnerOptions, String suiteName) {
        int testConcurrency = runnerOptions.getTestConcurrency() == null ? DEFAULT_CONCURRENCY : runnerOptions.getTestConcurrency();
        this.testConcurrency = new TestConcurrency(testConcurrency, false);
        init(suiteName);
    }

    private void init(String suiteName) {
        this.suiteName = suiteName;
        this.logger = new IdPrintingLogger(suiteName);
        logger.log("runner created");
        initServices();
        startServices();
        logger.verbose("rendering grid manager is built");
    }

    public Map<String, RGridResource> getResourcesCacheMap() {
        return resourcesCacheMap;
    }

    public Map<String, SyncTaskListener<Void>> getUploadedResourcesCache() {
        return uploadedResourcesCache;
    }

    public RenderingInfo getRenderingInfo() {
        return renderingInfo;
    }

    private void initServices() {
        this.eyesOpenerService = new OpenerService("eyesOpenerService", servicesGroup,
                logger, this.testConcurrency.actualConcurrency, openerServiceConcurrencyLock, new EyesService.EyesServiceListener() {
            @Override
            public FutureTask<TestResultContainer> getNextTask(EyesService.Tasker tasker) {
                return getOrWaitForTask(openerServiceLock, tasker);
            }

        }, new EyesService.Tasker() {
            @Override
            public FutureTask<TestResultContainer> getNextTask() {
                return getNextTestToOpen();
            }
        });

        this.eyesCloserService = new EyesService("eyesCloserService", servicesGroup, logger, testConcurrency.actualConcurrency, new EyesService.EyesServiceListener() {
            @Override
            public FutureTask<TestResultContainer> getNextTask(EyesService.Tasker tasker) {

                return getOrWaitForTask(closerServiceLock, tasker);
            }

        }, new EyesService.Tasker() {
            @Override
            public FutureTask<TestResultContainer> getNextTask() {
                return getNextTestToClose();
            }
        });

        this.resourceCollectionService = new EyesService("resourceCollectionService", servicesGroup, logger, testConcurrency.actualConcurrency,
                new EyesService.EyesServiceListener() {
                    @Override
                    public FutureTask<TestResultContainer> getNextTask(EyesService.Tasker tasker) {
                        return getOrWaitForTask(resourceCollectionServiceLock, tasker);
                    }
                },
        new EyesService.Tasker() {
            @Override
            public FutureTask<TestResultContainer> getNextTask() {
                return getNextResourceCollectionTask();
            }
        });

        this.renderingGridService = new RenderingGridService("renderingGridService", servicesGroup, logger, this.testConcurrency.actualConcurrency, new RenderingGridService.RGServiceListener() {
            @Override
            public RenderingTask getNextTask() {
                RenderingTask nextTestToRender = getNextRenderingTask();
                if (nextTestToRender == null) {
                    synchronized (renderingServiceLock) {
                        try {
                            nextTestToRender = getNextRenderingTask();
                            if (nextTestToRender == null) {
                                renderingServiceLock.wait(500);
                                nextTestToRender = getNextRenderingTask();
                            }

                        } catch (Exception e) {
                            GeneralUtils.logExceptionStackTrace(logger, e);
                        }
                    }
                }
                return nextTestToRender;
            }
        });

        this.eyesCheckerService = new EyesService("eyesCheckerService", servicesGroup, logger, this.testConcurrency.actualConcurrency, new EyesService.EyesServiceListener() {
            @Override
            public FutureTask<TestResultContainer> getNextTask(EyesService.Tasker tasker) {

                return getOrWaitForTask(checkerServiceLock, tasker);
            }

        }, new EyesService.Tasker() {
            @Override
            public FutureTask<TestResultContainer> getNextTask() {
                return getNextCheckTask();
            }
        });

    }

    private FutureTask<TestResultContainer> getNextCheckTask() {
        VisualGridTask visualGridTask;
        try {
            ScoreTask bestScoreTask = null;
            int bestScore = -1;
            synchronized (allEyes) {
                for (IRenderingEyes eyes : allEyes) {
                    ScoreTask currentScoreTask = eyes.getBestScoreTaskForCheck();
                    if (currentScoreTask == null) {
                        continue;
                    }

                    int currentTestMark = currentScoreTask.getScore();
                    if (bestScore < currentTestMark) {
                        bestScoreTask = currentScoreTask;
                        bestScore = currentTestMark;
                    }
                }
            }

            if (bestScoreTask == null) {
                return null;
            }

            visualGridTask = bestScoreTask.getVisualGridTask();
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
            return null;
        }
        return new FutureTask<>(visualGridTask);
    }

    private FutureTask<TestResultContainer> getNextResourceCollectionTask() {
        synchronized (resourceCollectionTaskList) {
            if (resourceCollectionTaskList.isEmpty()) {
                return null;
            }

            ResourceCollectionTask resourceCollectionTask = resourceCollectionTaskList.get(0);
            resourceCollectionTaskList.remove(resourceCollectionTask);
            return new FutureTask<>(resourceCollectionTask);
        }
    }

    private RenderingTask getNextRenderingTask() {
        synchronized (this.renderingTaskList) {
            if (this.renderingTaskList.isEmpty()) {
                return null;
            }

            RenderingTask finalRenderingTask = null;
            List<RenderingTask> chosenTasks = new ArrayList<>();
            for (RenderingTask renderingTask : this.renderingTaskList) {
                if (!renderingTask.isReady()) {
                    continue;
                }

                if (finalRenderingTask == null) {
                    finalRenderingTask = renderingTask;
                } else {
                    finalRenderingTask.merge(renderingTask);
                }

                chosenTasks.add(renderingTask);
            }

            finalRenderingTask =  finalRenderingTask != null && finalRenderingTask.isReady() ? finalRenderingTask : null;

            if (finalRenderingTask != null) {
                logger.verbose(String.format("Next rendering task contains %d render requests", chosenTasks.size()));
                this.renderingTaskList.removeAll(chosenTasks);
            }

            return finalRenderingTask;
        }
    }

    private FutureTask<TestResultContainer> getNextTestToClose() {
        RunningTest runningTest;
        synchronized (allEyes) {
            for (IRenderingEyes eyes : allEyes) {
                runningTest = eyes.getNextTestToClose();
                if (runningTest != null) {
                    return runningTest.getNextCloseTask();
                }
            }
        }
        return null;
    }

    private synchronized FutureTask<TestResultContainer> getNextTestToOpen() {
        ScoreTask bestScoreTask = null;
        int bestMark = -1;
        synchronized (allEyes) {
            for (IRenderingEyes eyes : allEyes) {
                if (eyes.isServerConcurrencyLimitReached()) {
                    return null;
                }
            }

            for (IRenderingEyes eyes : allEyes) {
                ScoreTask currentTestMark = null;
                try {
                    currentTestMark = eyes.getBestScoreTaskForOpen();
                } catch (Exception e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
                if (currentTestMark == null) {
                    continue;
                }
                int currentScore = currentTestMark.getScore();
                if (bestMark < currentScore) {
                    bestMark = currentScore;
                    bestScoreTask = currentTestMark;
                }
            }
        }

        if (bestScoreTask == null) {
            return null;
        }

        logger.verbose("found test with mark " + bestMark);
        logger.verbose("calling getNextOpenTaskAndRemove on " + bestScoreTask.toString());
        VisualGridTask nextOpenVisualGridTask = bestScoreTask.getVisualGridTask();
        logger.verbose(String.format("Found open task for test %s", nextOpenVisualGridTask.getRunningTest().getTestName()));
        return new FutureTask<>(nextOpenVisualGridTask);
    }

    public void open(IRenderingEyes eyes, RenderingInfo renderingInfo) {
        logger.verbose("enter");

        if (this.renderingInfo == null) {
            this.renderingInfo = renderingInfo;
        }

        synchronized (eyesToOpenList) {
            eyesToOpenList.add(eyes);
        }

        if (allEyes.isEmpty()) {
            this.setLogger(eyes.getLogger());
        }
        synchronized (allEyes) {
            allEyes.add(eyes);
        }
        logger.verbose("releasing allEyes");
        eyes.setListener(eyesListener);
        logger.verbose("concurrencyLock.notify()");
        this.addBatch(eyes.getBatchId(), eyes.getBatchCloser());
    }

    private void startServices() {
        logger.verbose("enter");
        setServicesOn(true);
        this.servicesGroup.setDaemon(true);
        this.eyesOpenerService.start();
        this.eyesCloserService.start();
        this.resourceCollectionService.start();
        this.renderingGridService.start();
        this.eyesCheckerService.start();
        logger.verbose("exit");
    }

    private void stopServices() {
        logger.verbose("enter");
        setServicesOn(false);
        this.eyesOpenerService.stopService();
        this.eyesCloserService.stopService();
        this.resourceCollectionService.stopService();
        this.renderingGridService.stopService();
        this.eyesCheckerService.stopService();
        logger.verbose("exit");
    }


    public TestResultsSummary getAllTestResultsImpl() {
        return getAllTestResults(true);
    }

    public TestResultsSummary getAllTestResultsImpl(boolean throwException) {
        logger.log("enter");
        Map<IRenderingEyes, Collection<Future<TestResultContainer>>> allFutures = new HashMap<>();
        for (IRenderingEyes eyes : allEyes) {
            Collection<Future<TestResultContainer>> futureList = eyes.close();
            Collection<Future<TestResultContainer>> futures = allFutures.get(eyes);
            if (futures != null && !futures.isEmpty()) {
                futureList.addAll(futures);
            }
            allFutures.put(eyes, futureList);
        }
        Throwable exception = null;
        notifyAllServices();
        List<TestResultContainer> allResults = new ArrayList<>();
        logger.verbose("trying to call future.get on " + allFutures.size() + " future lists.");
        for (Map.Entry<IRenderingEyes, Collection<Future<TestResultContainer>>> entry : allFutures.entrySet()) {

            Collection<Future<TestResultContainer>> value = entry.getValue();
            IRenderingEyes key = entry.getKey();
            key.getAllTestResults().clear();
            logger.verbose("trying to call future.get on " + value.size() + " futures of " + key);
            for (Future<TestResultContainer> future : value) {
                logger.log("calling future.get on " + key);
                TestResultContainer obj = null;
                try {
                    obj = future.get(10, TimeUnit.MINUTES);
                    if (obj.getException() != null && exception == null) {
                        exception = obj.getException();
                    }
                } catch (Throwable e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                    if (exception == null) {
                        exception = e;
                    }
                }
                logger.log("got TestResultContainer: " + obj);
                allResults.add(obj);
                key.getAllTestResults().add(obj);
            }

        }

        stopServices();
        notifyAllServices();
        logger.log("exit");
        if (throwException && exception != null) {
            throw new Error(exception);
        }
        return new TestResultsSummary(allResults);
    }

    public void close(IRenderingEyes eyes) {
        logger.verbose("adding eyes to close list: " + eyes);
        notifyAllServices();
    }

    public synchronized void check(ICheckSettings settings, IDebugResourceWriter debugResourceWriter, FrameData domData,
                                   IEyesConnector connector, final List<VisualGridTask> checkVisualGridTasks,
                                   List<VisualGridSelector[]> selectors, UserAgent userAgent) {

        if (debugResourceWriter == null) {
            debugResourceWriter = this.debugResourceWriter;
        }
        if (debugResourceWriter == null) {
            debugResourceWriter = new NullDebugResourceWriter();
        }

        TaskListener<List<RenderingTask>> listener = new TaskListener<List<RenderingTask>>() {
            @Override
            public void onComplete(List<RenderingTask> renderingTasks) {
                logger.verbose("locking renderingTaskList");
                synchronized (renderingTaskList) {
                    renderingTaskList.addAll(renderingTasks);
                }

                logger.verbose("releasing renderingTaskList");
                notifyAllServices();
            }

            @Override
            public void onFail() {
                notifyAllServices();
            }
        };

        ResourceCollectionTask resourceCollectionTask = new ResourceCollectionTask(this, connector, domData,
                userAgent, selectors, settings, checkVisualGridTasks, debugResourceWriter, listener,
                new RenderingTask.RenderTaskListener() {
            @Override
            public void onRenderSuccess() {
                logger.verbose("enter");
                notifyAllServices();
                logger.verbose("exit");
            }

            @Override
            public void onRenderFailed(Exception e) {
                notifyAllServices();
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        });

        synchronized (this.resourceCollectionTaskList) {
            resourceCollectionTaskList.add(resourceCollectionTask);
        }

        notifyAllServices();
    }

    private void notifyAllServices() {
        logger.verbose("enter");
        notifyOpenerService();
        notifyCloserService();
        notifyCheckerService();
        notifyRenderingService();
        logger.verbose("exit");
    }

    private void notifyRenderingService() {
        logger.verbose("trying to notify rendering service");
        synchronized (renderingServiceLock) {
            renderingServiceLock.notify();
        }
        logger.verbose("renderingLockFree");
    }

    private void notifyCloserService() {
        logger.verbose("trying to notify closer service");
        synchronized (closerServiceLock) {
            closerServiceLock.notifyAll();
        }
        logger.verbose("closerLockFree");
    }

    private void notifyOpenerService() {
        logger.verbose("trying to notify opener service");
        synchronized (openerServiceLock) {
            openerServiceLock.notifyAll();
            logger.verbose("openerLockFree");
        }
    }

    private void notifyCheckerService() {
        logger.verbose("trying to notify checker service");
        synchronized (checkerServiceLock) {
            checkerServiceLock.notifyAll();
            logger.verbose("checkerLockFree");
        }
    }

    public void setDebugResourceWriter(IDebugResourceWriter debugResourceWriter) {
        this.debugResourceWriter = debugResourceWriter;
    }

    public IDebugResourceWriter getDebugResourceWriter() {
        return this.debugResourceWriter;
    }

    public void setLogger(Logger logger) {
        eyesCheckerService.setLogger(logger);
        eyesCloserService.setLogger(logger);
        eyesOpenerService.setLogger(logger);
        resourceCollectionService.setLogger(logger);
        renderingGridService.setLogger(logger);
        if (this.logger == null) {
            this.logger = logger;
        } else {
            this.logger.setLogHandler(logger.getLogHandler());
        }
    }

    public String getConcurrencyLog() throws JsonProcessingException {
        if (wasConcurrencyLogSent) {
            return null;
        }

        wasConcurrencyLogSent = true;
        String key = testConcurrency.isDefault ? "defaultConcurrency" : testConcurrency.isLegacy ? "concurrency" : "testConcurrency";
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type", "runnerStarted");
        objectNode.put(key, testConcurrency.userConcurrency);
        return objectMapper.writeValueAsString(objectNode);
    }
}
