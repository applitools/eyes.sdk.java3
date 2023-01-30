package com.applitools.eyes.playwright.universal;

import com.applitools.eyes.universal.Reference;

import java.util.HashSet;
import java.util.UUID;

public class Refer extends com.applitools.eyes.universal.Refer {

    public Refer() {
        super();
    }

    /**
     * store the driver's ref.
     *
     * @return the UUID
     */
    public String ref(Object value, Reference root) {
        if (value == null) {
            return null;
        }

        String ref = UUID.randomUUID().toString();
        references.put(ref, value);

        if (root != null) {
            HashSet<Reference> childRefs = this.relations.computeIfAbsent(root.getApplitoolsRefId(), k -> new HashSet<>());
            childRefs.add(new Reference(ref));
        }
        return ref;
    }

    /**
     * get a ref from store
     *
     * @param ref  the ref
     * @return the ref
     */
    public Object deref(Object ref) {
        if (isRef(ref)) {
            Reference reference = (Reference) ref;
            return references.get(reference.getApplitoolsRefId());
        }

        return ref;
    }

    public void destroy(Reference root) {
        if (!isRef(root)) {
            return;
        }

        HashSet<Reference> childRefs = relations.get(root.getApplitoolsRefId());
        if (childRefs != null) {
            for (Reference childRef : childRefs) {
                destroy(childRef);
            }

            relations.remove(root.getApplitoolsRefId());
        }

        references.remove(root.getApplitoolsRefId());
    }

    public Boolean isRef(Object ref) {
        if (ref instanceof Reference)
            return ((Reference) ref).getApplitoolsRefId() != null;

        return false;
    }

}
