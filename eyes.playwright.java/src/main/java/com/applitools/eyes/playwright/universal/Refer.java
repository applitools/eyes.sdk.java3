package com.applitools.eyes.playwright.universal;

import com.applitools.eyes.playwright.universal.driver.Driver;
import com.microsoft.playwright.Page;

import java.util.UUID;

public class Refer extends com.applitools.eyes.universal.Refer {

    public Refer() {
        super();
    }

    public void destroy() {
    }

    /**
     * generate a new UUID.
     *
     * @return the UUID
     */
    public String ref(Page page, Driver target) {
        String ref = UUID.randomUUID().toString();
        references.put(ref, page);
        relations.put(page, target);
        return ref;
    }

    /**
     * store a new ref
     *
     * @param context  the context to ref
     * @return the ref id
     */
    public String ref(Object context) {
        String ref = UUID.randomUUID().toString();
        references.put(ref, context);
        return ref;
    }

    /**
     * get a ref from store
     *
     * @param applitoolsRefId  the ref id
     * @return the ref
     */
    public Object deref(String applitoolsRefId) {
        if (applitoolsRefId == null) {
            return  null;
        }

        return isRef(applitoolsRefId)? references.get(applitoolsRefId) : applitoolsRefId;
    }

    public Boolean isRef(String applitoolsRefId) {
        return references.containsKey(applitoolsRefId);
    }

    public Object getPage(Page page) { return relations.get(page); }
}
