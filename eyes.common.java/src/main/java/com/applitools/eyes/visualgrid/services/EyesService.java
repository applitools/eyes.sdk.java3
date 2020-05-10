package com.applitools.eyes.visualgrid.services;

import com.applitools.eyes.Logger;
import com.applitools.eyes.TestResultContainer;
import com.applitools.utils.GeneralUtils;

import java.util.concurrent.*;

public class EyesService extends Thread {

    protected final int threadPoolSize;
    protected ExecutorService executor;
    protected final EyesService.EyesServiceListener listener;
    private final Object debugLock;
    protected final Tasker tasker;
    protected boolean isServiceOn = true;
    private boolean isPaused;


    protected Logger logger;

    public void setLogger(Logger logger) {
        if (this.logger == null) {
            this.logger = logger;
        } else {
            this.logger.setLogHandler(logger.getLogHandler());
        }
    }

    interface Tasker {
        FutureTask<TestResultContainer> getNextTask();
    }

    public interface EyesServiceListener {
        FutureTask<TestResultContainer> getNextTask(Tasker tasker);
    }

    public EyesService(String serviceName, ThreadGroup servicesGroup, Logger logger, int threadPoolSize, Object debugLock, EyesServiceListener listener, Tasker tasker) {
        super(servicesGroup, serviceName);
        this.threadPoolSize = threadPoolSize;
        this.executor = new ThreadPoolExecutor(this.threadPoolSize, threadPoolSize, 1, TimeUnit.DAYS, new ArrayBlockingQueue<Runnable>(20));
        this.listener = listener;
        this.logger = logger;
        this.debugLock = debugLock;
        this.tasker = tasker;
        this.isPaused = debugLock != null;
}

    @Override
    public void run() {
        try {
            logger.log("Service '" + this.getName() + "' had started");
            while (isServiceOn) {
                pauseIfNeeded();
                runNextTask();
            }
            if (this.executor != null) {
                this.executor.shutdown();
            }
            logger.log("Service '" + this.getName() + "' is finished");
        } catch (Throwable e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
    }

    protected void pauseIfNeeded() {
        if (isPaused) {
            synchronized (debugLock) {
                try {
                    debugLock.wait();
                    this.isPaused = false;
                } catch (InterruptedException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
            }
        }
    }

    void runNextTask() {
        if (!isServiceOn) return;
        final FutureTask<TestResultContainer> task = this.listener.getNextTask(tasker);
        if (task != null) {
            pauseIfNeeded();
            this.executor.submit(task);
        }
    }

    private void debugNotify() {
        if (debugLock != null) {
            synchronized (debugLock) {
                debugLock.notify();
            }
        }
    }

    public void debugPauseService() {
        this.isPaused = true;
    }

    void stopService() {
        logger.verbose(this.getName() + " service is Stopped");
        this.isServiceOn = false;
    }
}
