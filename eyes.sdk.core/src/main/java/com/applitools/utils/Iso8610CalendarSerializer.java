package com.applitools.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Calendar;

/**
 * Serializes {@link Calendar} instances as an ISO8610 formatted string.
 */
public class Iso8610CalendarSerializer extends JsonSerializer<Calendar> {

    @Override
    public void serialize(Calendar value,
              @SuppressWarnings("SpellCheckingInspection") JsonGenerator jgen,
              SerializerProvider provider) throws IOException {
        jgen.writeString(GeneralUtils.toISO8601DateTime(value));
    }
}
