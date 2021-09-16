package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.elasticsearch.action.index.IndexResponse;
import org.example.model.Event;
import org.example.model.EventType;
import org.example.providers.ElasticJavaApiDataProvider;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class Runner {

    public static void main(String[] args) throws JsonProcessingException, UnknownHostException, ParseException {
        ElasticJavaApiDataProvider elasticJavaApiDataProvider = new ElasticJavaApiDataProvider();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        IndexResponse event1 = elasticJavaApiDataProvider.createOrUpdateIndex("user", new Event(
                11L,
                "New tech-talk",
                EventType.TECH_TALK,
                "2021-12-21",
                "New York",
                "This is tech-talk in New York",
                Arrays.asList("car", "scooter"
                )));


        IndexResponse indexResponse = elasticJavaApiDataProvider.insertDocumentIntoIndex("user", new Event(
                11L,
                "New tech-talk",
                EventType.TECH_TALK,
                "2021-12-21",
                "New York",
                "This is tech-talk in New York",
                Arrays.asList("car", "scooter"
                )
        ), "4");

        List<Event> events = elasticJavaApiDataProvider.getAllEvents("event1");
        List<Event> workshopEventList = elasticJavaApiDataProvider.workshopEventsOnly("event1");
        List<Event> eventsWithTitle = elasticJavaApiDataProvider.eventsWithTitle("event1", "tech-talk");
        List<Event> eventsWithTitleAndDateAfter = elasticJavaApiDataProvider.eventsWithTitleAndAfterDate("event1", "tech-talk", simpleDateFormat.parse("2021-12-00"));
    }

}
