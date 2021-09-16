package org.example.providers;

import org.example.model.Entity;
import org.example.model.Event;

import java.util.Date;
import java.util.List;

public interface ElasticSearchProvider {
    <T> T insertDocumentIntoIndex(String indexName, Entity entity, String id);

    List<Event> getAllEvents(String indexName);

    List<Event> workshopEventsOnly(String indexName);

    List<Event> eventsWithTitle(String indexName, String title);

    List<Event> eventsWithTitleAndAfterDate(String indexName, String title, Date date);
}
