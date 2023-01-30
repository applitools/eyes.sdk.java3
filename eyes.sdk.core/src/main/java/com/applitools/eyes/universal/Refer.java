package com.applitools.eyes.universal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class Refer {

    public static final String APPLITOOLS_REF_ID = "applitools-ref-id";

    protected Map<String, Object> references;
    protected Map<String, HashSet<Reference>> relations;

    public Refer() {
        references = new HashMap<>();
        relations = new HashMap<>();
    }

    protected abstract void destroy(Reference root);
}
