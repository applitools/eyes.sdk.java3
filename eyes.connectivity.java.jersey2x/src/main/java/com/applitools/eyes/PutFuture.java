package com.applitools.eyes;

import com.applitools.eyes.visualgrid.model.RGridResource;
import com.applitools.eyes.visualgrid.model.RunningRender;
import com.applitools.utils.GeneralUtils;

import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PutFuture implements IPutFuture {

    private Future<Response> putFuture;
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
        if (this.putFuture == null) {
            IPutFuture newFuture = serverConnector.renderPutResource(runningRender, resource, useraAgent,null);
            this.putFuture = newFuture.getPutFuture();
        }
        if (!this.isSentAlready) {
            while (retryCount != 0) {
                try {
                    Response response = this.putFuture.get(20, TimeUnit.SECONDS);
                    response.close();
                    break;
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    logger.verbose(e.getMessage() + " on hash: " + resource.getSha256());
                    retryCount--;
                    logger.verbose("Entering retry");
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e1) {
                        GeneralUtils.logExceptionStackTrace(logger, e1);
                    }
                    IPutFuture newFuture = serverConnector.renderPutResource(runningRender, resource, useraAgent,null);
                    logger.log("fired retry");
                    this.putFuture = newFuture.getPutFuture();
                }
            }
        }
        this.isSentAlready = true;
        return true;
    }

    @Override
    public Boolean get(long timeout, TimeUnit unit) {
        if (this.putFuture == null) {
            IPutFuture newFuture = serverConnector.renderPutResource(runningRender, resource, useraAgent, null);
            this.putFuture = newFuture.getPutFuture();
        }
        if (!this.isSentAlready) {
            while (retryCount != 0) {
                try {
                    Response response = this.putFuture.get(timeout, unit);
                    response.close();
                    break;
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    logger.verbose(e.getMessage() + " on hash: " + resource.getSha256());
                    retryCount--;
                    logger.verbose("Entering retry");
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e1) {
                        GeneralUtils.logExceptionStackTrace(logger, e1);
                    }
                    IPutFuture newFuture = serverConnector.renderPutResource(runningRender, resource, useraAgent,null);
                    logger.log("fired retry");
                    this.putFuture = newFuture.getPutFuture();
                }
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
