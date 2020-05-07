package com.applitools.eyes.visualgrid;

import com.applitools.eyes.IPutFuture;
import com.applitools.eyes.IServerConnector;
import com.applitools.eyes.Logger;
import com.applitools.eyes.visualgrid.model.RGridResource;
import com.applitools.eyes.visualgrid.model.RunningRender;
import com.applitools.utils.GeneralUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PutFuture implements IPutFuture {

    private Future putFuture;
    private RGridResource resource;
    private RunningRender runningRender;
    private IServerConnector serverConnector;
    private Logger logger;

    private boolean isSentAlready = false;
    private int retryCount = 5;
    private String userAgent;

    public PutFuture(RGridResource resource, RunningRender runningRender, IServerConnector serverConnector, Logger logger, String userAgent) {
        this.resource = resource;
        this.runningRender = runningRender;
        this.serverConnector = serverConnector;
        this.logger = logger;
        this.userAgent = userAgent;
    }

    public PutFuture(Future putFuture, RGridResource resource, RunningRender runningRender, IServerConnector serverConnector, Logger logger, String userAgent) {
        this(resource, runningRender, serverConnector, logger, userAgent);
        this.putFuture = putFuture;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Boolean get() {
        return get(1, TimeUnit.MINUTES);
    }

    @Override
    public Boolean get(long timeout, TimeUnit unit) {
        if (!this.isSentAlready) {
            while (retryCount != 0) {
                try {
                    logger.verbose("Response open. - "+this.resource.getUrl());
                    this.putFuture.get(timeout, unit);

                    logger.verbose("Got PutFuture Response .- "+this.resource.getUrl());
                    break;
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    logger.verbose("Entering retry");
                    GeneralUtils.logExceptionStackTrace(logger, e);
                    logger.verbose(e.getMessage() + " on hash: " + resource.getSha256());
                    this.putFuture.cancel(true);
                    retryCount--;
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e1) {
                        GeneralUtils.logExceptionStackTrace(logger, e1);
                    }
                    IPutFuture newFuture = serverConnector.renderPutResource(runningRender, resource, userAgent,null);
                    logger.log("fired retry");
                    this.putFuture = newFuture.getPutFuture();
                }
            }
        }
        if(retryCount == 0){
            if(!isSentAlready){
                throw new Error("Error trying to PUT Resource - "+resource.getUrl());
            }
        }
        this.isSentAlready = true;
        return true;
    }

    @Override
    public Future getPutFuture() {
        return this.putFuture;
    }

    @Override
    public RGridResource getResource() {
        return this.resource;
    }

    @Override
    public String toString() {
        return this.resource.getUrl();
    }
}
