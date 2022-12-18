package com.applitools.eyes.playwright.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.microsoft.playwright.Frame;

import java.io.IOException;

public class FrameSerializer extends StdSerializer<Frame> {

    public FrameSerializer() {
        this(null);
    }

    protected FrameSerializer(Class<Frame> t) {
        super(t);
    }

    @Override
    public void serialize(Frame frame, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(frame.toString());
    }

}
