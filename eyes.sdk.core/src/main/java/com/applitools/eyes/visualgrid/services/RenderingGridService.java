package com.applitools.eyes.visualgrid.services;

import com.applitools.eyes.Logger;
import com.applitools.eyes.visualgrid.model.RenderingTask;
import com.applitools.utils.GeneralUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RenderingGridService extends Thread {

    private static final int FACTOR = 1;
    private final RGServiceListener listener;
    private boolean isServiceOn = true;
    private final ExecutorService executor;
    protected Logger logger;

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

    RenderingGridService(String serviceName, ThreadGroup servicesGroup, Logger logger, int testsPoolSize, RGServiceListener listener) {
        super(servicesGroup, serviceName);
        this.executor = new ThreadPoolExecutor(testsPoolSize, testsPoolSize * FACTOR, 1, TimeUnit.DAYS, new ArrayBlockingQueue<Runnable>(50));
        this.listener = listener;
        this.logger = logger;
    }

    @Override
    public void run() {
        try {
            logger.log("Service '" + this.getName() + "' had started");
            while (isServiceOn) {
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
        if (!isServiceOn) {
            return;
        }

        final RenderingTask task = this.listener.getNextTask();
        if (task == null || !task.isReady()) {
            return;
        }

        try {
            this.executor.submit(task);
        } catch (Exception e) {
            logger.verbose("Exception in - this.executor.submit(task); ");
            if(e.getMessage().contains("Read timed out")){
                logger.verbose("Read timed out");
            }
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
    }

    public void stopService() {
        this.isServiceOn = false;
    }
}
