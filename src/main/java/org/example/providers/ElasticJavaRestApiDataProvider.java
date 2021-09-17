package org.example.providers;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.example.JsonHelper;
import org.example.model.ElasticResponseDto;
import org.example.model.Entity;
import org.example.model.Event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ElasticJavaRestApiDataProvider {

    private static final int QUERY_MAX_RESULTS_SIZE = 10000;
    private final RestClient client;

    public ElasticJavaRestApiDataProvider() {
        client = RestClient.builder(
                new HttpHost("localhost", 9200, "http")).build();
    }

    public Response createIndex(String indexName) throws IOException {
        Request request = new Request("PUT", "/" + indexName);

        return client.performRequest(request);
    }

    public Response setIndexMapping(String indexName, String mappingJson) throws IOException {
        Request request = new Request("PUT", "/" + indexName + "/_mapping");
        request.setJsonEntity(mappingJson);

        return client.performRequest(request);
    }

    public Response insertDocumentIntoIndex(String indexName, Entity entity, String id) throws IOException {
        Request request = new Request("PUT", "/" + indexName + "/_doc/" + id);
        request.setJsonEntity(JsonHelper.generateJsonString(entity));

        return client.performRequest(request);
    }

    public List<Event> getAllEvents(String indexName) {
        Request request = new Request("GET", "/" + indexName + "/_search");

        request.setJsonEntity("{\"size\": " + QUERY_MAX_RESULTS_SIZE + "}");

        return getEventsFromRequest(request);
    }

    public List<Event> workshopEventsOnly(String indexName) {
        Request request = new Request("GET", "/" + indexName + "/_search");

        request.setJsonEntity("{\n" +
                "  \"size\": " + QUERY_MAX_RESULTS_SIZE + ",\n" +
                "  \"query\": {\n" +
                "    \"match\": {\n" +
                "      \"eventType\": \"WORKSHOP\"\n" +
                "    }\n" +
                "  }\n" +
                "}");


        return getEventsFromRequest(request);
    }

    public List<Event> eventsWithTitle(String indexName, String title) {
        Request request = new Request("GET", "/" + indexName + "/_search");

        request.setJsonEntity("{\n" +
                "  \"size\": " + QUERY_MAX_RESULTS_SIZE + ",\n" +
                "  \"query\": {\n" +
                "    \"match\": {\n" +
                "      \"title\": \"" + title + "\"\n" +
                "    }\n" +
                "  }\n" +
                "}");


        return getEventsFromRequest(request);
    }

    public List<Event> eventsWithTitleAndAfterDate(String indexName, String title, Date date) {
        Request request = new Request("GET", "/" + indexName + "/_search");

        request.setJsonEntity("{\n" +
                "  \"size\": " + QUERY_MAX_RESULTS_SIZE + ",\n" +
                "  \"query\": {\n" +
                "    \"bool\": {\n" +
                "      \"must\": [\n" +
                "        {\n" +
                "          \"match\": {\n" +
                "              \"title\": \"" + title + "\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"range\": {\n" +
                "            \"date\": {\n" +
                "              \"gt\": " + date.getTime() + "\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "}");


        return getEventsFromRequest(request);
    }

    public Response deleteAllEventsWithTitle(String indexName, String title) throws IOException {
        Request request = new Request("POST", "/_bulk");
        List<Event> events = eventsWithTitle(indexName, title);

        StringBuilder stringBuilder = new StringBuilder();
        for (Event event : events) {
            stringBuilder.append("{ \"delete\" : { \"_index\" :" + "\"" + indexName + "\", \"_id\" :  " + "\"" + event.getEventId() + "\" } }\n");
        }

        request.setJsonEntity(stringBuilder.toString());

        return client.performRequest(request);
    }

    private List<Event> getEventsFromRequest(Request request) {

        List<Event> events = new ArrayList<>();
        try {
            Response response = client.performRequest(request);
            String responseResult = EntityUtils.toString(response.getEntity());
            JsonNode jsonNode = JsonHelper.getInstance().readTree(responseResult);
            JsonNode hits = jsonNode.get("hits").get("hits");

            for (Object object : JsonHelper.getInstance().convertValue(hits, ArrayList.class)) {
                String dtoJson = JsonHelper.getInstance().writeValueAsString(object);
                ElasticResponseDto elasticResponseDto = JsonHelper.getInstance().readValue(dtoJson, ElasticResponseDto.class);
                String eventJson = JsonHelper.getInstance().writeValueAsString(elasticResponseDto.get_source());
                Event event = JsonHelper.getInstance().readValue(eventJson, Event.class);
                events.add(event);
            }
        } catch (IOException e) {
            throw new NullPointerException(e.getMessage());
        }
        return events;
    }
}
