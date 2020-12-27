package com.applitools.eyes.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.openqa.selenium.WebElement;

import java.io.IOException;

public class WebElementSerializer extends StdSerializer<WebElement> {
    public WebElementSerializer() {
        this(null);
    }

    protected WebElementSerializer(Class<WebElement> t) {
        super(t);
    }

    @Override
    public void serialize(WebElement value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.toString());
    }
}
