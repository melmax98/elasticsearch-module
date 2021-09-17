package org.example.providers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.bulk.BulkResponse;
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

    public List<Event> getAllEvents(String indexName) throws IOException {
        Request request = new Request("GET", "/" + indexName + "/_search");

        request.setJsonEntity("{\"size\": " + QUERY_MAX_RESULTS_SIZE + "}");
        Response response = client.performRequest(request);
        String responseResult = EntityUtils.toString(response.getEntity());

        return getEventsFromResponse(responseResult);
    }

    public List<Event> workshopEventsOnly(String indexName) {
        return null;
    }

    public List<Event> eventsWithTitle(String indexName, String title) {
        return null;
    }

    public List<Event> eventsWithTitleAndAfterDate(String indexName, String title, Date date) {
        return null;
    }

    public BulkResponse deleteAllEventsWithTitle(String index, String title) {
        return null;
    }

    private List<Event> getEventsFromResponse(String searchResponse) {
        List<Event> events = new ArrayList<>();
        try {
            JsonNode jsonNode = JsonHelper.getInstance().readTree(searchResponse);
            JsonNode hits = jsonNode.get("hits").get("hits");

            for (Object object : JsonHelper.getInstance().convertValue(hits, ArrayList.class)) {
                String dtoJson = JsonHelper.getInstance().writeValueAsString(object);
                ElasticResponseDto elasticResponseDto = JsonHelper.getInstance().readValue(dtoJson, ElasticResponseDto.class);
                String eventJson = JsonHelper.getInstance().writeValueAsString(elasticResponseDto.get_source());
                Event event = JsonHelper.getInstance().readValue(eventJson, Event.class);
                events.add(event);
            }
        } catch (JsonProcessingException e) {
            throw new NullPointerException();
        }
        return events;
    }
}
