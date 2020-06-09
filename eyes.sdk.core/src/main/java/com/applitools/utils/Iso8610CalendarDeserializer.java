package com.applitools.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

/**
 * Deserialize ISO8610 formatted strings to {@link Calendar}.
 */
public class Iso8610CalendarDeserializer extends JsonDeserializer<Calendar> {
    public Calendar deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        try {
            return GeneralUtils.fromISO8601DateTime(jsonParser.getValueAsString());
        } catch (ParseException e) {
            throw new JsonParseException("Failed to parse time string to Calendar instance",
                    jsonParser.getCurrentLocation(), e);
        }
    }
}
