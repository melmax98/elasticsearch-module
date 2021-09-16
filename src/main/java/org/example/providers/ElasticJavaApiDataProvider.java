package org.example.providers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.example.JsonHelper;
import org.example.model.Entity;
import org.example.model.Event;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ElasticJavaApiDataProvider implements ElasticSearchProvider {

    private final TransportClient client;
    private static final int QUERY_MAX_RESULTS_SIZE = 10000;


    public ElasticJavaApiDataProvider() throws UnknownHostException {
        client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
    }

    @Override
    public IndexResponse insertDocumentIntoIndex(String indexName, Entity entity, String id) {
        return client.prepareIndex(indexName, "_doc", id)
                .setSource(JsonHelper.generateJson(entity), XContentType.JSON)
                .get();
    }

    @Override
    public List<Event> getAllEvents(String indexName) {
        SearchResponse searchResponse = client.prepareSearch(indexName).setSize(QUERY_MAX_RESULTS_SIZE).get();
        return getEventsFromResponse(searchResponse);
    }

    @Override
    public List<Event> workshopEventsOnly(String indexName) {
        SearchResponse searchResponse = client.prepareSearch(indexName)
                .setQuery(QueryBuilders.matchQuery("eventType", "WORKSHOP"))
                .setSize(QUERY_MAX_RESULTS_SIZE)
                .get();


        return getEventsFromResponse(searchResponse);
    }

    @Override
    public List<Event> eventsWithTitle(String indexName, String title) {
        SearchResponse searchResponse = client.prepareSearch(indexName)
                .setQuery(QueryBuilders.matchQuery("title", title))
                .setSize(QUERY_MAX_RESULTS_SIZE)
                .get();


        return getEventsFromResponse(searchResponse);
    }

    @Override
    public List<Event> eventsWithTitleAndAfterDate(String indexName, String title, Date date) {
        SearchResponse searchResponse = client.prepareSearch(indexName)
                .setQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("title", title))
                        .must(QueryBuilders.rangeQuery("date").gt(date.getTime())).boost(5))
                .setSize(QUERY_MAX_RESULTS_SIZE)
                .get();


        return getEventsFromResponse(searchResponse);
    }

    private List<Event> getEventsFromResponse(SearchResponse searchResponse) {
        List<Event> result = new ArrayList<>();
        Event event;
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            String jsonEvent = hit.getSourceAsString();
            try {
                event = JsonHelper.getInstance().readValue(jsonEvent, Event.class);
            } catch (JsonProcessingException e) {
                event = null;
            }
            result.add(event);
        }
        return result;
    }

    /**
     * Entity must be with all fields not null
     */
    public IndexResponse createOrUpdateIndex(String indexName, Entity entity) {
        return client.prepareIndex(indexName, "_doc")
                .setSource(JsonHelper.generateJson(entity), XContentType.JSON)
                .get();
    }
}
