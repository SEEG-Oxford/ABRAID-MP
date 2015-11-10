package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.DateTimeDeserializer;
import org.joda.time.ReadableDateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * Implements deserialization of Joda DateTime objects, with a custom date/time formatter.
 * This is necessary because this functionality is not implemented in jackson-datatype-joda's default deserializer
 * (which this class extends).
 *
 * Copyright (c) 2014 University of Oxford
 */
public class JodaCustomDateTimeDeserializer extends DateTimeDeserializer {
    private static final long serialVersionUID = 4530909041442821333L;
    private transient DateTimeFormatter dateTimeFormatter;


    public JodaCustomDateTimeDeserializer(Class<? extends ReadableInstant> cls, DateTimeFormatter dateTimeFormatter) {
        super(cls);
        this.dateTimeFormatter = dateTimeFormatter;
    }

    /**
     * Factory method for a JodaCustomDateTimeDeserializer.
     * @param cls The class to deserialize.
     * @param dateTimeFormatter The Joda Time formatter for DateTime objects.
     * @param <T> The type to deserialize.
     * @return A new JodaCustomDateTimeDeserializer.
     */
    @SuppressWarnings("unchecked")
    public static <T extends ReadableInstant> JsonDeserializer<T> forType(Class<T> cls,
                                                                          DateTimeFormatter dateTimeFormatter) {
        return (JsonDeserializer<T>) new JodaCustomDateTimeDeserializer(cls, dateTimeFormatter);
    }

    /**
     * Deserializes a JSON token (string) into a DateTime, using the supplied DateTimeFormatter.
     *
     * Note that not all of the superclass's functionality is implemented, i.e. conversion from integer rather than
     * string and adjustment for DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE.
     *
     * @param jsonParser The JSON parser.
     * @param context The deserialization context.
     * @return The deserialized DateTime.
     * @throws IOException If an error occurs
     */
    @Override
    public ReadableDateTime deserialize(com.fasterxml.jackson.core.JsonParser jsonParser,
                                        DeserializationContext context) throws IOException {
        JsonToken token = jsonParser.getCurrentToken();

        if (token == JsonToken.VALUE_STRING) {
            String str = jsonParser.getText().trim();
            if (str.length() == 0) {
                return null;
            }
            return dateTimeFormatter.parseDateTime(str);
        }
        throw context.mappingException(getValueClass());
    }
}
