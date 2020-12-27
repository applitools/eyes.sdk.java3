package com.applitools.eyes.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.openqa.selenium.By;

import java.io.IOException;

public class BySerializer extends StdSerializer<By> {
    public BySerializer() {
        this(null);
    }

    protected BySerializer(Class<By> t) {
        super(t);
    }

    @Override
    public void serialize(By value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.toString());
    }
}
