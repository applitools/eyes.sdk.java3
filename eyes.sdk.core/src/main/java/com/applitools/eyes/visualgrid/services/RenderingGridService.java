package com.applitools.eyes.visualgrid.services;

import com.applitools.eyes.Logger;
import com.applitools.eyes.visualgrid.model.RenderingTask;
import com.applitools.utils.GeneralUtils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RenderingGridService extends Thread {

    private static final int FACTOR = 5;
    private final Object debugLock;
    private final RGServiceListener listener;
    private final int maximumPoolSize;
    private boolean isServiceOn = true;
    private ExecutorService executor;
    protected Logger logger;
    private boolean isPaused;
    private AtomicInteger concurrentSession = new AtomicInteger(0);
    private final Object concurrencyLock;

    public void setLogger(Logger logger) {
        if (this.logger == null) {
            this.logger = logger;
        } else {
            this.logger.setLogHandler(logger.getLogHandler());
        }
    }

    public interface RGServiceListener {
        RenderingTask getNextTask();
    }

    RenderingGridService(String serviceName, ThreadGroup servicesGroup, Logger logger, int threadPoolSize, Object debugLock, RGServiceListener listener, Object concurrencyLock) {
        super(servicesGroup, serviceName);
        maximumPoolSize = threadPoolSize * FACTOR;
        this.executor = new ThreadPoolExecutor(threadPoolSize, maximumPoolSize, 1, TimeUnit.DAYS, new ArrayBlockingQueue<Runnable>(20));
        this.debugLock = debugLock;
        this.listener = listener;
        this.logger = logger;
        this.isPaused = debugLock != null;
        this.concurrencyLock = concurrencyLock;
    }

    @Override
    public void run() {
        try {
            logger.log("Service '" + this.getName() + "' had started");
            while (isServiceOn) {
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
                runNextTask();
            }
            if (this.executor != null) {
                this.executor.shutdown();
            }
            logger.log("Service '" + this.getName() + "' is finished");
        } catch (Throwable e) {
            logger.verbose("Rendering Service Error : "+e);
        }
    }

    void runNextTask() {
        if (!isServiceOn) return;
        if (this.maximumPoolSize > concurrentSession.get()) {
            final RenderingTask task = this.listener.getNextTask();
            if (task != null) {
                task.addListener(new RenderingTask.RenderTaskListener() {
                    @Override
                    public void onRenderSuccess() {
                        debugNotify();
                        onRenderFinish();

                    }

                    @Override
                    public void onRenderFailed(Exception e) {
                        debugNotify();
                        onRenderFinish();
                    }
                });
                try {
                    concurrentSession.incrementAndGet();
                    this.executor.submit(task);
                } catch (Exception e) {
                    logger.verbose("Exception in - this.executor.submit(task); ");
                    if(e.getMessage().contains("Read timed out")){
                        logger.verbose("Read timed out");
                    }
                    e.printStackTrace();
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
            }
        }
        else{
            logger.verbose("trying to sync lock");
            synchronized (concurrencyLock){
                try {
                    logger.verbose("Waiting for concurrency to be free");
                    concurrencyLock.wait();
                    logger.verbose("concurrency free");
                } catch (InterruptedException e) {
                    GeneralUtils.logExceptionStackTrace(logger ,e);
                }
            }
            logger.verbose("releasing lock");
        }
    }

    private void onRenderFinish() {
        concurrentSession.decrementAndGet();
        logger.verbose("trying to sync lock");
        synchronized (concurrencyLock) {
            concurrencyLock.notify();

        }
        logger.verbose("releasing lock");
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

    public void stopService() {
        this.isServiceOn = false;
    }
}
