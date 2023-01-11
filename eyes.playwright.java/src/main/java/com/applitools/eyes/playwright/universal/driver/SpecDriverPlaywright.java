package com.applitools.eyes.playwright.universal.driver;

import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.playwright.universal.driver.dto.DriverInfoDto;
import com.applitools.eyes.universal.ISpecDriver;
import com.applitools.eyes.universal.Reference;
import com.applitools.eyes.universal.driver.ICookie;
import com.applitools.eyes.universal.dto.*;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.JSHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.Cookie;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpecDriverPlaywright implements ISpecDriver {

    private Refer refer;

    public SpecDriverPlaywright(Refer ref) {
        this.refer = ref;
    }

    @Override
    public Boolean isDriver(Reference driver) {
        return ISpecDriver.super.isDriver(driver);
    }

    @Override
    public Boolean isContext(Reference context) {
        return ISpecDriver.super.isContext(context);
    }

    @Override
    public Boolean isElement(Reference element) {
        return ISpecDriver.super.isElement(element);
    }

    @Override
    public Boolean isSelector(Reference selector) {
        return ISpecDriver.super.isSelector(selector);
    }

    @Override
    public Reference mainContext(Reference context) {
        return ISpecDriver.super.mainContext(context);
    }

    @Override
    public Reference parentContext() {
        return ISpecDriver.super.parentContext();
    }

    @Override
    public Reference childContext() {
        return ISpecDriver.super.childContext();
    }

    @Override
    public Object executeScript(Reference context, String script, Object arg) {
        Object ctx = refer.deref(context.getApplitoolsRefId());
        JSHandle res = null;

        Object args = derefArgsUtil(arg);

        if (ctx instanceof Frame) {
            res = ((Frame) ctx).evaluateHandle(script, args);
        } else if (ctx instanceof Page) {
            res = ((Page) ctx).evaluateHandle(script, args);
        }

        return handlerToObjectUtil(res);
    }

    @Override
    public Element findElement(Reference driver, Reference selector, Reference parent) {
        Object context = refer.deref(driver.getApplitoolsRefId());
        Object root = parent == null? context : refer.deref(parent.getApplitoolsRefId());
        String type = ((Selector)selector).getType();

        ElementHandle elementHandle = null;
        if (root instanceof Frame) {
            elementHandle = ((Frame) root).locator(((Selector)selector).getSelector()).elementHandle();
        } else if (root instanceof Page) {
            elementHandle = ((Page) root).locator(((Selector)selector).getSelector()).elementHandle();
        }

        Element element = new Element();
        element.setApplitoolsRefId(refer.ref(elementHandle));
        return elementHandle == null? null : element;
    }

    @Override
    public Element[] findElements() {
        return (Element[]) ISpecDriver.super.findElements();
    }

    @Override
    public void setElementText() {
        ISpecDriver.super.setElementText();
    }

    @Override
    public String getElementText() {
        return ISpecDriver.super.getElementText();
    }

    @Override
    public void setViewportSize(Reference driver, RectangleSizeDto windowSize) {
        Object page = refer.deref(driver.getApplitoolsRefId());
        ((Page) page).setViewportSize(windowSize.getWidth(), windowSize.getHeight());
    }

    @Override
    public RectangleSizeDto getViewportSize(Reference driver) {
        Object page = refer.deref(driver.getApplitoolsRefId());
        RectangleSizeDto viewportSize = new RectangleSizeDto();
        viewportSize.setWidth(((Page) page).viewportSize().width);
        viewportSize.setHeight(((Page) page).viewportSize().height);
        return viewportSize;
    }

    @Override
    public List<ICookie> getCookies(Reference driver, Reference context) {
        Page ctx = (Page) refer.deref(driver.getApplitoolsRefId());
        List<Cookie> cookies = ctx.context().cookies();

        return cookies.stream()
                .map(cookie -> new TCookie(cookie.name, cookie.value))
                .collect(Collectors.toList());
    }

    @Override
    public DriverInfoDto getDriverInfo(Reference driver) {
        return new DriverInfoDto();
    }

    @Override
    public String getTitle(Reference driver) {
        Page context = (Page) refer.deref(driver.getApplitoolsRefId());
        return context.title();
    }

    @Override
    public String getUrl(Reference driver) {
        Page context = (Page) refer.deref(driver.getApplitoolsRefId());
        return context.url();
    }

    @Override
    public void visit(Reference driver, String url) {
        Page context = (Page) refer.deref(driver.getApplitoolsRefId());
        context.navigate(url);
    }

    @Override
    public byte[] takeScreenshot(Reference driver) {
        Page context = (Page) refer.deref(driver.getApplitoolsRefId());
        return context.screenshot();
    }

    @Override
    public void click() {
        ISpecDriver.super.click();
    }

    static public String[] getMethodNames() {
        Method[] methods = SpecDriverPlaywright.class.getDeclaredMethods();
        return Stream.of(methods)
                .map(Method::getName)
                .filter(name -> !name.contains("getMethodNames") && !name.contains("Util"))
                .toArray(String[]::new);
    }

    private Object handlerToObjectUtil(JSHandle jsHandle) {
        if (jsHandle == null) {
            return null;
        }
        String[] types = jsHandle.toString().split("/(?:.+@)?(\\w*)(?:\\(\\d+\\))?/i");
        String type = types.length > 0? types[0].toLowerCase() : null;

        if (type == null) {
            return null;
        } else if (type.contains("array")) {
            Map<String, JSHandle> map = jsHandle.getProperties();
            List<Object> arrayValues = new ArrayList<>();
            for (JSHandle jsHandle1: map.values()) {
                arrayValues.add(handlerToObjectUtil(jsHandle1));
            }
            return arrayValues;
        } else if (type.equals("object")) {
            Map<String, JSHandle> map = jsHandle.getProperties();
            List<Object> arrayValues = new ArrayList<>();
            for (Map.Entry<String, JSHandle> entry: map.entrySet()) {
                arrayValues.add(handlerToObjectUtil(entry.getValue()));
            }
            return arrayValues;
        } else if (type.equals("jshandle@node")) {
            Element element = new Element();
            element.setApplitoolsRefId(refer.ref(jsHandle.asElement()));
            return element;
        }

        return jsHandle.jsonValue();
    }

    private Object derefArgsUtil(Object arg) {
        if (arg == null) {
            return null;
        }
        ArrayList<Object> derefArg = new ArrayList<>();

        if (arg instanceof ArrayList) {
            for (Object argument: (ArrayList) arg) {
                if (argument instanceof ArrayList) {
                    derefArg.add(derefArgsUtil(argument));
                } else if (argument instanceof LinkedHashMap){
                    Object id = ((LinkedHashMap) argument).get(Refer.APPLITOOLS_REF_ID);
                    Object deref = refer.deref(id.toString());
                    derefArg.add(deref);
                }
            }
            return derefArg;
        } else if (arg instanceof LinkedHashMap){
            LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : ((LinkedHashMap<?, ?>) arg).entrySet()) {
                map.put(entry.getKey(), refer.deref(entry.getValue().toString()));
            }
            return map;
        }

        return derefArg;
    }
}
