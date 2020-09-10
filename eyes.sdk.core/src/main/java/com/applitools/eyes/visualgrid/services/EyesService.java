package com.applitools.eyes.visualgrid.services;

import com.applitools.eyes.Logger;
import com.applitools.eyes.TestResultContainer;
import com.applitools.utils.GeneralUtils;

import java.util.concurrent.*;

public class EyesService extends Thread {

    protected final int eyesConcurrency;
    protected ExecutorService executor;
    protected final EyesService.EyesServiceListener listener;
    protected final Tasker tasker;
    protected boolean isServiceOn = true;


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

    public EyesService(String serviceName, ThreadGroup servicesGroup, Logger logger, int eyesConcurrency, EyesServiceListener listener, Tasker tasker) {
        super(servicesGroup, serviceName);
        this.eyesConcurrency = eyesConcurrency;
        this.executor = new ThreadPoolExecutor(this.eyesConcurrency, this.eyesConcurrency, 1, TimeUnit.DAYS, new ArrayBlockingQueue<Runnable>(50));
        this.listener = listener;
        this.logger = logger;
        this.tasker = tasker;
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
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
    }

    void runNextTask() {
        if (!isServiceOn) {
            return;
        }
        final FutureTask<TestResultContainer> task = this.listener.getNextTask(tasker);
        if (task != null) {
            this.executor.submit(task);
        }
    }

    void stopService() {
        logger.verbose(this.getName() + " service is Stopped");
        this.isServiceOn = false;
    }
}
