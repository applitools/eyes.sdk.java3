
package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.IEyesJsExecutor;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.exceptions.EyesDriverOperationException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.List;

public class OverflowAwareCssTranslatePositionProvider extends CssTranslatePositionProvider {

    private static final String JS_GET_CONTENT_ENTIRE_SIZE =
            "var documentScrollWidth = document.documentElement.scrollWidth; " +
                    "var bodyScrollWidth = document.body.scrollWidth; " +
                    "var bodyOverflowX = window.getComputedStyle(document.body).overflowX;" +
                    "var documentOverflowX = window.getComputedStyle(document.documentElement).overflowX;" +
                    "var totalWidth = undefined;" +
                    "if (bodyOverflowX !== 'hidden' && documentOverflowX !== 'hidden')" +
                    "{ totalWidth = Math.max(documentScrollWidth, bodyScrollWidth); }" +
                    "else if (bodyOverflowX !== 'hidden' && documentOverflowX === 'hidden')" +
                    "{ totalWidth = bodyScrollWidth; }" +
                    "else if (bodyOverflowX === 'hidden' && documentOverflowX !== 'hidden')" +
                    "{ totalWidth = documentScrollWidth; }" +
                    "else if (bodyOverflowX === 'hidden' && documentOverflowX === 'hidden')" +
                    "{ totalWidth = window.innerWidth; }" +
                    "var clientHeight = document.documentElement.clientHeight; " +
                    "var bodyClientHeight = document.body.clientHeight; " +
                    "var scrollHeight = document.documentElement.scrollHeight; " +
                    "var bodyScrollHeight = document.body.scrollHeight; " +
                    "var maxDocElementHeight = Math.max(clientHeight, scrollHeight); " +
                    "var maxBodyHeight = Math.max(bodyClientHeight, bodyScrollHeight); "
                    + "var totalHeight = Math.max(maxDocElementHeight, maxBodyHeight); "
                    + "return [totalWidth, totalHeight];";


    public OverflowAwareCssTranslatePositionProvider(Logger logger, IEyesJsExecutor executor, WebElement rootElement) {
        super(logger, executor, rootElement);
    }

    @Override
    public RectangleSize getEntireSize() {
        RectangleSize result;
        try {
            //noinspection unchecked
            Object retVal = executor.executeScript(JS_GET_CONTENT_ENTIRE_SIZE);
            List<Long> esAsList = (List<Long>) retVal;
            result = new RectangleSize(esAsList.get(0).intValue(),
                    esAsList.get(1).intValue());
        } catch (WebDriverException e) {
            throw new EyesDriverOperationException(
                    "Failed to extract entire size!", e);
        }
        logger.verbose("OverflowAwareScrollPositionProvider - Entire size: " + result);
        return result;
    }

}
