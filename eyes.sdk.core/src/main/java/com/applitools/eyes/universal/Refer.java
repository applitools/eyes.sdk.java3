package com.applitools.eyes.universal;

import java.util.HashMap;
import java.util.Map;

public abstract class Refer {

    public static final String APPLITOOLS_REF_ID = "applitools-ref-id";

    protected static Map<String, Object> references;
    protected static Map<Object, Object> relations;

    public Refer() {
        references = new HashMap<>();
        relations = new HashMap<>();
    }

    protected abstract void destroy();
}
