package org.example;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Entity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


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
            throw new NullPointerException();
        }
    }

    public static String generateJsonStringFromFile (String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public static String generateJsonString(Entity entity) {
        try {
            return mapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            LOGGER.error("Could not parse given entity", e);
            throw new NullPointerException();
        }
    }
}
