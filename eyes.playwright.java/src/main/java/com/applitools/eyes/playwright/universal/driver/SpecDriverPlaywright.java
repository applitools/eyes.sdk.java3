package com.applitools.eyes.playwright.universal.driver;

import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.playwright.universal.driver.dto.DriverInfoDto;
import com.applitools.eyes.playwright.universal.dto.Context;
import com.applitools.eyes.playwright.universal.dto.Element;
import com.applitools.eyes.playwright.universal.dto.Selector;
import com.applitools.eyes.universal.ISpecDriver;
import com.applitools.eyes.universal.Reference;
import com.applitools.eyes.universal.driver.ICookie;
import com.applitools.eyes.universal.dto.*;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.JSHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.impl.PageImpl;
import com.microsoft.playwright.options.Cookie;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpecDriverPlaywright implements ISpecDriver {

    private Refer refer;

    public SpecDriverPlaywright(Refer ref) {
        this.refer = ref;
    }

    @Override
    public Boolean isDriver(Object driver) {
        return driver instanceof Page;
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
    public Context mainContext(Reference context) {
        Frame mainFrame = extractContextUtil(context);
        while (mainFrame.parentFrame() != null) {
            mainFrame = mainFrame.parentFrame();
        }

        Context frameContext = new Context();
        frameContext.setApplitoolsRefId(refer.ref(mainFrame, context));
        return frameContext;
    }

    @Override
    public Context parentContext(Reference context) {
        Frame frame = extractContextUtil(context);

        Context frameContext = new Context();
        frameContext.setApplitoolsRefId(refer.ref(frame.parentFrame(), context));
        return frameContext;
    }

    @Override
    public Context childContext(Reference context, Reference element) {
        Object root = refer.deref(element);

        if (root instanceof ElementHandle) {
            Context frameContext = new Context();
            Frame frame = ((ElementHandle) root).contentFrame();
            frameContext.setApplitoolsRefId(refer.ref(frame, element));

            return frameContext;
        }

        return (Context) ISpecDriver.super.childContext(context, element);
    }

    @Override
    public Object executeScript(Reference context, String script, Object arg) {
        Object ctx = refer.deref(context);
        JSHandle res = null;

        Object args = derefArgsUtil(arg);

        if (ctx instanceof Frame) {
            res = ((Frame) ctx).evaluateHandle(script, args);
        } else if (ctx instanceof Page) {
            res = ((Page) ctx).evaluateHandle(script, args);
        }

        return handlerToObjectUtil(res, context);
    }

    @Override
    public Element findElement(Reference driver, Reference selector, Reference parent) {
        Object context = refer.deref(driver);
        Object root = parent == null? context : refer.deref(parent);

        ElementHandle elementHandle = null;
        if (root instanceof Frame) {
            elementHandle = ((Frame) root).locator(((Selector)selector).getSelector()).elementHandle();
        } else if (root instanceof Page) {
            elementHandle = ((Page) root).locator(((Selector)selector).getSelector()).elementHandle();
        }

        Element element = new Element();
        element.setApplitoolsRefId(refer.ref(elementHandle, driver));
        return elementHandle == null? null : element;
    }

    @Override
    public List<Reference> findElements(Reference context, Reference selector, Reference parent) {
        Object ctx = refer.deref(context);
        Object root = parent == null? ctx : refer.deref(parent);

        List<ElementHandle> elementHandles = new ArrayList<>();
        if (root instanceof Frame) {
            elementHandles = ((Frame) root).locator(((Selector)selector).getSelector()).elementHandles();
        } else if (root instanceof Page) {
            elementHandles = ((Page) root).locator(((Selector)selector).getSelector()).elementHandles();
        }

        return elementHandles.stream()
                .map(elementHandle -> {
                    Element element = new Element(elementHandle);
                    element.setApplitoolsRefId(refer.ref(elementHandle, context));
                    return element;
                })
                .collect(Collectors.toList());
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
        Object page = refer.deref(driver);
        ((Page) page).setViewportSize(windowSize.getWidth(), windowSize.getHeight());
    }

    @Override
    public RectangleSizeDto getViewportSize(Reference driver) {
        Object page = refer.deref(driver);
        RectangleSizeDto viewportSize = new RectangleSizeDto();
        viewportSize.setWidth(((Page) page).viewportSize().width);
        viewportSize.setHeight(((Page) page).viewportSize().height);
        return viewportSize;
    }

    @Override
    public List<ICookie> getCookies(Reference driver, Reference context) {
        Page ctx = (Page) refer.deref(driver);
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
        Page context = (Page) refer.deref(driver);
        return context.title();
    }

    @Override
    public String getUrl(Reference driver) {
        Page context = (Page) refer.deref(driver);
        return context.url();
    }

    @Override
    public void visit(Reference driver, String url) {
        Page context = (Page) refer.deref(driver);
        context.navigate(url);
    }

    @Override
    public byte[] takeScreenshot(Reference driver) {
        Page context = (Page) refer.deref(driver);
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

    private Object handlerToObjectUtil(JSHandle jsHandle, Reference context) {
        if (jsHandle == null) {
            return null;
        }

        String type = "";
        Pattern pattern = Pattern.compile("(?:.+@)?(\\w*)(?:\\(\\d+\\))?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(jsHandle.toString());
        if (matcher.find()) {
            type = matcher.group(0).toLowerCase();
        }

        if (type.contains("array")) {
            Map<String, JSHandle> map = jsHandle.getProperties();
            List<Object> arrayValues = new ArrayList<>();
            for (JSHandle jsHandle1: map.values()) {
                arrayValues.add(handlerToObjectUtil(jsHandle1, context));
            }
            return arrayValues;
        } else if (type.equals("object")) {
            Map<String, JSHandle> map = jsHandle.getProperties();
            Map<String, Object> resultMap = new HashMap<>();
            for (Map.Entry<String, JSHandle> entry: map.entrySet()) {
                resultMap.put(entry.getKey(), handlerToObjectUtil(entry.getValue(), context));
            }
            return resultMap;
        } else if (type.equals("jshandle@node")) {
            Element element = new Element();
            element.setApplitoolsRefId(refer.ref(jsHandle.asElement(), context));
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
                } else {
                    derefArg.add(refer.deref(argument));
                }
            }
            return derefArg;
        } else if (arg instanceof HashMap){
            HashMap<Object, Object> map = new HashMap<>();
            for (Map.Entry<?, ?> entry : ((HashMap<?, ?>) arg).entrySet()) {
                map.put(entry.getKey(), refer.deref(entry.getValue()));
            }
            return map;
        }

        return refer.deref(arg);
    }

    private Frame extractContextUtil(Reference context) {
        Object root = refer.deref(context);
        return isDriver(root) ? ((PageImpl) root).mainFrame() : (Frame) root;
    }
}
