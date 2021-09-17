package org.example;

import com.unboundid.util.json.JSONException;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Response;
import org.example.model.Event;
import org.example.model.EventType;
import org.example.providers.ElasticJavaApiDataProvider;
import org.example.providers.ElasticJavaRestApiDataProvider;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Runner {

    private static final String MAPPING_PATH = "src/main/resources/eventMapping.json";

    public static void main(String[] args) throws IOException, ParseException, JSONException {
       testRestApiProvider();
        testJavaApiProvider();
    }

    public static void testRestApiProvider() throws IOException {
        ElasticJavaRestApiDataProvider javaRestApiDataProvider = new ElasticJavaRestApiDataProvider();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");


        Response event5 = javaRestApiDataProvider.setIndexMapping("event5", JsonHelper.generateJsonStringFromFile(MAPPING_PATH));
        Event event = new Event(
                1L,
                "New tech-talk",
                EventType.TECH_TALK,
                "2021-12-21",
                "New York",
                "This is tech-talk in New York",
                Arrays.asList("car", "scooter"
                ));
        Response event51 = javaRestApiDataProvider.insertDocumentIntoIndex("event5", event, "1");
        List<Event> allEvents = javaRestApiDataProvider.getAllEvents("event5");
        List<Event> workshopEventsOnly = javaRestApiDataProvider.workshopEventsOnly("event5");
        List<Event> eventsByTitle = javaRestApiDataProvider.eventsWithTitle("event5", "tech-talk");
        List<Event> eventsByTitleAndAfterDate = javaRestApiDataProvider.eventsWithTitleAndAfterDate("event5", "workshop", new Date(1650037500000L));
        Response response = javaRestApiDataProvider.deleteAllEventsWithTitle("event5", "workshop");
        List<Event> allEventsAfter = javaRestApiDataProvider.getAllEvents("event5");
    }

    public static void testJavaApiProvider() throws UnknownHostException, ParseException {
        ElasticJavaApiDataProvider elasticJavaApiDataProvider = new ElasticJavaApiDataProvider();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        IndexResponse event1 = elasticJavaApiDataProvider.createOrUpdateIndex("event1", new Event(
                11L,
                "New tech-talk",
                EventType.TECH_TALK,
                "2021-12-21",
                "New York",
                "This is tech-talk in New York",
                Arrays.asList("car", "scooter"
                )));


        IndexResponse indexResponse = elasticJavaApiDataProvider.insertDocumentIntoIndex("event1", new Event(
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
        List<Event> eventsWithTitleAndDateAfter = elasticJavaApiDataProvider.eventsWithTitleAndAfterDate("event1", "tech-talk", JsonHelper.getSimpleDateFormat().parse("2021-12-00"));
    }
}
