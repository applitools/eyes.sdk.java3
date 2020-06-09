package com.applitools.eyes.visualgrid.model;

import java.util.List;

public class CdtData {
    
    public int nodeType;

    public List<Integer> childNodeIndexes;

    public String nodeName;

    public String nodeValue;

    public List<AttributeData> attributes;

    public Integer shadowRootIndex;

    @Override
    public String toString() {
        return "CdtData{" +
                "nodeType=" + nodeType +
                ", childNodeIndexes=" + childNodeIndexes +
                ", nodeName='" + nodeName + '\'' +
                ", nodeValue='" + nodeValue + '\'' +
                ", attributes=" + attributes +
                ", shadowRootIndex=" + shadowRootIndex +
                '}';
    }
}
