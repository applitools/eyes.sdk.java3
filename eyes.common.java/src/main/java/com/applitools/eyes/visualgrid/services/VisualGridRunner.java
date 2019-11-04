package com.applitools.eyes.visualgrid.services;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.utils.GeneralUtils;

import java.util.*;
import java.util.concurrent.*;

public class VisualGridRunner extends EyesRunner {

    private int concurrentOpenSessions;

    //For Testing...
    private final Object openerServiceDebugLock;
    private final Object checkerServiceDebugLock;
    private final Object closerServiceDebugLock;
    private final Object renderServiceDebugLock;

    private OpenerService eyesOpenerService;
    private EyesService eyesCloserService;
    private EyesService eyesCheckerService;
    private RenderingGridService renderingGridService;
    private ThreadGroup servicesGroup = new ThreadGroup("Services Group");
    private final List<IRenderingEyes> eyesToOpenList = Collections.synchronizedList(new ArrayList<IRenderingEyes>(200));
    private final Set<IRenderingEyes> allEyes = Collections.synchronizedSet(new HashSet<IRenderingEyes>());
    private Map<String, IResourceFuture> cachedResources = Collections.synchronizedMap(new HashMap<String, IResourceFuture>());
    private Map<String, IPutFuture> putResourceCache = Collections.synchronizedMap(new HashMap<String, IPutFuture>());

    private final Object openerServiceConcurrencyLock = new Object();
    private final Object openerServiceLock = new Object();
    private final Object checkerServiceLock = new Object();
    private final Object closerServiceLock = new Object();
    private final Object renderingServiceLock = new Object();
    private final List<RenderingTask> renderingTaskList = Collections.synchronizedList(new ArrayList<RenderingTask>());

    private RenderingInfo renderingInfo;
    private IDebugResourceWriter debugResourceWriter;

    private RateLimiter rateLimiter;
    private String serverUrl = GeneralUtils.getEnvString("APPLITOOLS_SERVER_URL");;
    private static final String DEFAULT_API_KEY = GeneralUtils.getEnvString("APPLITOOLS_API_KEY");
    private String apiKey = DEFAULT_API_KEY;
    private boolean isDisabled;
    private boolean isServicesOn = false;

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

    public interface RenderListener {

        void onRenderSuccess();

        void onRenderFailed(Exception e);

    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    private FutureTask<TestResultContainer> getOrWaitForTask(Object lock, @SuppressWarnings("SpellCheckingInspection") EyesService.Tasker tasker,
                                                             String serviceName) {
        FutureTask<TestResultContainer> nextTestToOpen = tasker.getNextTask();
        if (nextTestToOpen == null) {
            try {
//                logger.verbose("locking " + serviceName);
                synchronized (lock) {
                    lock.wait(500);
                }
//                logger.verbose("releasing " + serviceName);
                nextTestToOpen = tasker.getNextTask();
//                logger.verbose(serviceName + " tasker returned " + nextTestToOpen);
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
        return nextTestToOpen;
    }

    public void pauseAllService() {
        eyesOpenerService.debugPauseService();
        eyesCloserService.debugPauseService();
        eyesCheckerService.debugPauseService();
        renderingGridService.debugPauseService();
    }

    private IRenderingEyes.EyesListener eyesListener = new IRenderingEyes.EyesListener() {
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

    public VisualGridRunner(int concurrentOpenSessions) {
        this(concurrentOpenSessions, null, null, null, null);

    }

    public VisualGridRunner(int concurrentOpenSessions,
                            Object openerServiceDebugLock,
                            Object checkerServiceDebugLock,
                            Object closerServiceDebugLock,
                            Object renderServiceDebugLock) {

        this.concurrentOpenSessions = concurrentOpenSessions;
        this.openerServiceDebugLock = openerServiceDebugLock;
        this.checkerServiceDebugLock = checkerServiceDebugLock;
        this.closerServiceDebugLock = closerServiceDebugLock;
        this.renderServiceDebugLock = renderServiceDebugLock;
        this.rateLimiter = new RateLimiter(logger, 20);
        init();
        startServices();
        logger.verbose("rendering grid manager is built");
    }

    public Map<String, IResourceFuture> getCachedResources() {
        return cachedResources;
    }

    public Map<String, IPutFuture> getPutResourceCache() {
        return putResourceCache;
    }

    public RenderingInfo getRenderingInfo() {
        return renderingInfo;
    }

    private void init() {
        this.eyesOpenerService = new OpenerService("eyesOpenerService", servicesGroup,
                logger, this.concurrentOpenSessions, openerServiceConcurrencyLock, new EyesService.EyesServiceListener() {
            @Override
            public FutureTask<TestResultContainer> getNextTask(@SuppressWarnings("SpellCheckingInspection") EyesService.Tasker tasker) {
                return getOrWaitForTask(openerServiceLock, tasker, "eyesOpenerService");
            }

        }, openerServiceDebugLock, new EyesService.Tasker() {
            @Override
            public FutureTask<TestResultContainer> getNextTask() {
                return getNextTestToOpen();
            }
        });

        this.eyesCloserService = new EyesService("eyesCloserService", servicesGroup, logger, concurrentOpenSessions, closerServiceDebugLock, new EyesService.EyesServiceListener() {
            @Override
            public FutureTask<TestResultContainer> getNextTask(@SuppressWarnings("SpellCheckingInspection") EyesService.Tasker tasker) {

                return getOrWaitForTask(closerServiceLock, tasker, "eyesCloserService");
            }

        }, new EyesService.Tasker() {
            @Override
            public FutureTask<TestResultContainer> getNextTask() {
                return getNextTestToClose();
            }
        });

        this.renderingGridService = new RenderingGridService("renderingGridService", servicesGroup, logger, this.concurrentOpenSessions, renderServiceDebugLock, new RenderingGridService.RGServiceListener() {
            @Override
            public RenderingTask getNextTask() {
                RenderingTask nextTestToRender = getNextRenderingTask();
                if (nextTestToRender == null) {
                    synchronized (renderingServiceLock) {
                        try {
                            nextTestToRender = getNextRenderingTask();
                            if (nextTestToRender == null) {
                                renderingServiceLock.wait(500);
//                                logger.verbose("Rendering service woke up");
                                nextTestToRender = getNextRenderingTask();
                            }

                        } catch (Exception e) {
                            GeneralUtils.logExceptionStackTrace(logger, e);
                        }
                    }
                }
                return nextTestToRender;
            }
        }, renderingServiceLock);

        this.eyesCheckerService = new EyesService("eyesCheckerService", servicesGroup, logger, this.concurrentOpenSessions, checkerServiceDebugLock, new EyesService.EyesServiceListener() {
            @Override
            public FutureTask<TestResultContainer> getNextTask(@SuppressWarnings("SpellCheckingInspection") EyesService.Tasker tasker) {

                return getOrWaitForTask(checkerServiceLock, tasker, "eyesCheckerService");
            }

        }, new EyesService.Tasker() {
            @Override
            public FutureTask<TestResultContainer> getNextTask() {
                return getNextCheckTask();
            }
        });

    }

    private FutureTask<TestResultContainer> getNextCheckTask() {
        VisualGridTask visualGridTask = null;
        try {
            ScoreTask bestScoreTask = null;
            int bestScore = -1;
            synchronized (allEyes) {
                for (IRenderingEyes eyes : allEyes) {
                    ScoreTask currentScoreTask = eyes.getBestScoreTaskForCheck();

                    if (currentScoreTask == null) continue;
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
        }
        return new FutureTask<>(visualGridTask);
    }

    private RenderingTask getNextRenderingTask() {
        if (this.renderingTaskList.isEmpty()) {
            return null;
        }
        RenderingTask renderingTask = null;
        synchronized (this.renderingTaskList) {
            if (!this.renderingTaskList.isEmpty()) {
                renderingTask = this.renderingTaskList.get(0);
                this.renderingTaskList.remove(renderingTask);
            }
        }
//        logger.verbose("Starting to renderTask - " + renderingTask);
        return renderingTask;
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
//        logger.verbose("looking for best test in a list of " + allEyes.size());
        synchronized (allEyes) {
            for (IRenderingEyes eyes : allEyes) {
                ScoreTask currentTestMark = null;
                try {
                    currentTestMark = eyes.getBestScoreTaskForOpen();
                } catch (Exception e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
                if (currentTestMark == null) continue;
                int currentScore = currentTestMark.getScore();
                if (bestMark < currentScore) {
                    bestMark = currentScore;
                    bestScoreTask = currentTestMark;
                }
            }
        }

        if (bestScoreTask == null) {
//            logger.verbose("no test found.");
            return null;
        }

        logger.verbose("found test with mark " + bestMark);
        logger.verbose("calling getNextOpenTaskAndRemove on " + bestScoreTask.toString());
        VisualGridTask nextOpenVisualGridTask = bestScoreTask.getVisualGridTask();
        return new FutureTask<>(nextOpenVisualGridTask);
    }

    public void open(IRenderingEyes eyes, RenderingInfo renderingInfo) {
        logger.verbose("enter");

        if (this.renderingInfo == null) {
            this.renderingInfo = renderingInfo;
        }
//        logger.verbose("locking eyesToOpenList");
        synchronized (eyesToOpenList) {
            eyesToOpenList.add(eyes);
        }
//        logger.verbose("releasing eyesToOpenList");
//        logger.verbose("locking allEyes");

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
        this.renderingGridService.start();
        this.eyesCheckerService.start();
        logger.verbose("exit");
    }

    private void stopServices() {
        logger.verbose("enter");
        setServicesOn(false);
        this.eyesOpenerService.stopService();
        this.eyesCloserService.stopService();
        this.renderingGridService.stopService();
        this.eyesCheckerService.stopService();
        logger.verbose("exit");
    }


    public TestResultsSummary getAllTestResultsImpl() {
        return getAllTestResults(true);
    }

    public TestResultsSummary getAllTestResultsImpl(boolean throwException) {
        logger.verbose("enter");
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
                logger.verbose("calling future.get on " + key);
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
                logger.verbose("got TestResultContainer: " + obj);
                allResults.add(obj);
                key.getAllTestResults().add(obj);
            }

        }

        stopServices();
        notifyAllServices();
        logger.verbose("exit");
        if (throwException && exception != null) {
            throw new Error(exception);
        }
        return new TestResultsSummary(allResults);
    }

    public void close(IRenderingEyes eyes) {
        logger.verbose("adding eyes to close list: " + eyes);
        notifyAllServices();
    }

    public synchronized void check(ICheckSettings settings, IDebugResourceWriter debugResourceWriter, FrameData script,
                                   IEyesConnector connector, List<VisualGridTask> visualGridTaskList,
                                   List<VisualGridTask> openVisualGridTasks, String resultAsString, final RenderListener listener,
                                   List<VisualGridSelector[]> selectors, UserAgent userAgent) {

        if (debugResourceWriter == null) {
            debugResourceWriter = this.debugResourceWriter;
        }
        if (debugResourceWriter == null) {
            debugResourceWriter = new NullDebugResourceWriter();
        }

        RenderingTask renderingTask = new RenderingTask(connector, script, settings, visualGridTaskList,
                openVisualGridTasks, this, debugResourceWriter, new RenderingTask.RenderTaskListener() {
            @Override
            public void onRenderSuccess() {
                logger.verbose("enter");
                listener.onRenderSuccess();
                notifyAllServices();
                logger.verbose("exit");
            }

            @Override
            public void onRenderFailed(Exception e) {
                notifyAllServices();
                listener.onRenderFailed(e);
            }
        }, userAgent, selectors);
        logger.verbose("locking renderingTaskList");
        synchronized (renderingTaskList) {
            this.renderingTaskList.add(renderingTask);
        }

        //Write all DomSnapshot data
        debugResourceWriter.write(new RGridResource("http://THE_DOM_SNAPSHOT_RESULT.com", "DOM_SNAPSHOT", resultAsString.getBytes(), logger, ""));

        logger.verbose("releasing renderingTaskList");
        notifyAllServices();
//        logger.verbose("exit");
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

    public List<CompletableTask> getAllTasksByType(VisualGridTask.TaskType type) {
        List<CompletableTask> allTasks = new ArrayList<>();
        for (IRenderingEyes eyes : allEyes) {
            for (RunningTest runningTest : eyes.getAllRunningTests()) {
                for (VisualGridTask visualGridTask : runningTest.getVisualGridTaskList()) {
                    if (visualGridTask.getType() == type) {
                        allTasks.add(visualGridTask);
                    }
                }
            }
        }
        return allTasks;
    }

    public List<? extends CompletableTask> getAllRenderingTasks() {
        return this.renderingTaskList;
    }

    public void setDebugResourceWriter(IDebugResourceWriter debugResourceWriter) {
        this.debugResourceWriter = debugResourceWriter;
    }

    public IDebugResourceWriter getDebugResourceWriter() {
        return this.debugResourceWriter;
    }

    public RateLimiter getRateLimiter() {
        return rateLimiter;
    }

    public void setLogger(Logger logger) {
        eyesCheckerService.setLogger(logger);
        eyesCloserService.setLogger(logger);
        eyesOpenerService.setLogger(logger);
        renderingGridService.setLogger(logger);
        this.logger = logger;
    }
}
