package com.applitools.eyes.visualgrid;

import com.applitools.eyes.IServerConnector;
import com.applitools.eyes.Logger;
import com.applitools.eyes.visualgrid.model.RGridResource;
import com.applitools.eyes.visualgrid.services.IResourceFuture;
import com.applitools.utils.GeneralUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ResourceFuture implements IResourceFuture {

    private Future responseFuture;
    private String url;
    private Logger logger;
    private IServerConnector serverConnector;
    private RGridResource rgResource;
    private String userAgent;

    public ResourceFuture(String url, Logger logger, IServerConnector serverConnector, String userAgent) {
        this.url = url;
        this.logger = logger;
        this.serverConnector = serverConnector;
        this.userAgent = userAgent;
    }

    public ResourceFuture(RGridResource rgResource, Logger logger, IServerConnector serverConnector, String userAgent) {
        this.url = rgResource.getUrl();
        this.rgResource = rgResource;
        this.logger = logger;
        this.serverConnector = serverConnector;
        this.userAgent = userAgent;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return responseFuture.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return responseFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
        return responseFuture.isDone();
    }

    @Override
    public RGridResource get() throws InterruptedException {
        logger.verbose("entering - " + url);
        if (rgResource != null) return rgResource;
        synchronized (url) {
            if (this.responseFuture == null) {
                try {
                    IResourceFuture newFuture = serverConnector.downloadResource(new URL(this.url), userAgent, this);
                    this.responseFuture = ((ResourceFuture) newFuture).responseFuture;
                } catch (MalformedURLException malformedUrlException) {
                    GeneralUtils.logExceptionStackTrace(logger, malformedUrlException);
                }
            }
            int retryCount = 3;
            while (this.rgResource == null && retryCount > 0) {
                try {
                    responseFuture.get();

                } catch (Throwable e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                    logger.verbose("Entering retry for - " + url);
                    try {
                        Thread.sleep(300);
                        IResourceFuture newFuture = serverConnector.downloadResource(new URL(this.url), userAgent, this);
                        this.responseFuture = ((ResourceFuture) newFuture).responseFuture;
                    } catch (MalformedURLException malformedUrlException) {
                        GeneralUtils.logExceptionStackTrace(logger, malformedUrlException);
                    }
                }
                finally {
                    retryCount--;
                }
            }

        }
        logger.verbose("enter -1 this.rgResource: " + this.rgResource);
        logger.verbose("exit");
        return rgResource;
    }

    @Override
    public RGridResource get(long timeout, TimeUnit unit) {
        try {
            return get();
        } catch (InterruptedException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        return null;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public void setResource(RGridResource rgResource) {
        this.rgResource = rgResource;
    }

    @Override
    public void setResponseFuture(Future responseFuture) {
        this.responseFuture = responseFuture;
    }

}
