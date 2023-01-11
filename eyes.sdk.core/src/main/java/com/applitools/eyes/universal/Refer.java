package com.applitools.eyes.universal;

import java.util.HashMap;
import java.util.Map;

public class Refer {

    public static final String APPLITOOLS_REF_ID = "applitools-ref-id";

    protected Map<String, Object> references;
    protected Map<Object, Object> relations;

    public Refer() {
        references = new HashMap<>();
        relations = new HashMap<>();
    }
}
