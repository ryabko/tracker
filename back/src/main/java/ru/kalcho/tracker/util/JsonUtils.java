package ru.kalcho.tracker.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import java.io.IOException;
import java.io.StringWriter;

/**
 *
 */
public class JsonUtils {

    public static <T> T jsonToObject(String json, Class<T> type) throws JsonProcessingException {
        ObjectMapper mapper = createObjectMapper();
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            // i think it is impossible for String source
            throw new RuntimeException(e);
        }
    }

    public static <T> String objectToJSON(T object) throws JsonProcessingException {
        ObjectMapper mapper = createObjectMapper();
        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, object);
        } catch (IOException e) {
            // i think it is impossible for StringWriter
            throw new RuntimeException(e);
        }
        return sw.toString();
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JSR310Module()); // for supporting Java 8 dates
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // for pretty print
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // for presenting dates as String, not array
        return mapper;
    }

}
