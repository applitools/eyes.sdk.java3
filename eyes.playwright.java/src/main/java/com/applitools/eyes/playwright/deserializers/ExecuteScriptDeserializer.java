package com.applitools.eyes.playwright.deserializers;

import com.applitools.eyes.playwright.universal.dto.Element;
import com.applitools.eyes.playwright.universal.dto.Selector;
import com.applitools.eyes.universal.Refer;
import com.applitools.eyes.universal.Reference;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

public class ExecuteScriptDeserializer extends JsonDeserializer<Object> {
    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode jsonNode = jsonParser.readValueAsTree();

        if (jsonNode.isObject()) {
            return handleObject(jsonNode);
        }
        else if(jsonNode.isArray()) {
            return handleArray(jsonNode);
        } else if (jsonNode.isBoolean()) {
            return jsonParser.readValueAs(Boolean.class);
        } else if (jsonNode.isNull()) {
            return null;
        }

        System.out.println("Unsupported class for: " + jsonNode);
        return null;
    }

    private Object handleObject(JsonNode jsonNode) {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, JsonNode> jsonObject = mapper.convertValue(jsonNode, new TypeReference<HashMap<String, JsonNode>>() {});
        if (jsonObject.get(Refer.APPLITOOLS_REF_ID) != null) {
            String type = jsonObject.get("type").asText();
            if (type.equals("element")) {
                return mapper.convertValue(jsonNode, Element.class);
            } else if (type.equals("selector")) {
                return mapper.convertValue(jsonNode, Selector.class);
            } else {
                return mapper.convertValue(jsonNode, Reference.class);
            }
        } else {
            HashMap<Object, Object> object = new HashMap<>();
            for(Map.Entry<String, JsonNode> innerJsonNode : jsonObject.entrySet()) {
                if (innerJsonNode.getValue().isObject()) {
                    object.put(innerJsonNode.getKey(), handleObject(innerJsonNode.getValue()));
                } else if(innerJsonNode.getValue().isArray()) {
                    object.put(innerJsonNode.getKey(), handleArray(innerJsonNode.getValue()));
                } else if (innerJsonNode.getValue().isBoolean()) {
                    object.put(innerJsonNode.getKey(), innerJsonNode.getValue().asBoolean());
                } else if (innerJsonNode.getValue().isInt()) {
                    object.put(innerJsonNode.getKey(), innerJsonNode.getValue().asInt());
                } else if (innerJsonNode.getValue().isDouble()) {
                    object.put(innerJsonNode.getKey(), innerJsonNode.getValue().asDouble());
                } else if (innerJsonNode.getValue().isNull()) {
                    object.put(innerJsonNode.getKey(), null);
                } else {
                    object.put(innerJsonNode.getKey(), innerJsonNode.getValue().asText());
                }
            }
            return object;
        }
    }

    private List<Object> handleArray(JsonNode jsonNode) {
        List<Object> objects = new ArrayList<>();
        for(JsonNode node : jsonNode) {
            if (node.isObject()) {
                objects.add(handleObject(node));
            } else if (node.isBoolean()) {
                objects.add(node.asBoolean());
            } else if (node.isInt()) {
                objects.add(node.asInt());
            } else if (node.isDouble()) {
                objects.add(node.asDouble());
            } else if (node.isNull()) {
                objects.add(null);
            } else if (node.isArray()) {
                objects.add(handleArray(node));
            } else {
                objects.add(node.asText(null));
            }
        }
        return objects;
    }
}
