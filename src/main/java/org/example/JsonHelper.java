package org.example;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Entity;


public class JsonHelper {

    private JsonHelper() {
    }

    private static final Logger LOGGER = LogManager.getLogger(JsonHelper.class);

    // instance a json mapper
    private static final ObjectMapper mapper = new ObjectMapper();

    public static ObjectMapper getInstance() {
        return mapper;
    }

    public static byte[] generateJson(Entity entity) {
        try {
            return mapper.writeValueAsBytes(entity);
        } catch (JsonProcessingException e) {
            LOGGER.error("Could not parse given entity", e);
            return new byte[0];
        }
    }
}
