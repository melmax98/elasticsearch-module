package org.example.providers;

import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.example.model.Entity;
import org.example.model.Event;

import java.util.Date;
import java.util.List;

public interface ElasticSearchProvider {
    IndexResponse createOrUpdateIndex(String indexName, Entity entity);

    IndexResponse insertDocumentIntoIndex(String indexName, Entity entity, String id);

    List<Event> getAllEvents(String indexName);

    List<Event> workshopEventsOnly(String indexName);

    List<Event> eventsWithTitle(String indexName, String title);

    List<Event> eventsWithTitleAndAfterDate(String indexName, String title, Date date);

    BulkResponse deleteAllEventsWithTitle(String index, String title);
}
