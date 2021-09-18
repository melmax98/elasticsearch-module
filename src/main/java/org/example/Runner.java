package org.example;

import com.unboundid.util.json.*;
import org.apache.logging.log4j.*;
import org.elasticsearch.action.index.*;
import org.elasticsearch.client.*;
import org.example.model.*;
import org.example.providers.*;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class Runner {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(Runner.class);

    private static final String MAPPING_PATH = "src/main/resources/eventMapping.json";

    public static void main(String[] args) throws IOException, ParseException, JSONException, InterruptedException {
        generateLogs();
    }

    private static void generateLogs() throws InterruptedException {
        int i = 0;
        while (true) {
            Thread.sleep(500);
            LOGGER.info(i + " [c133ee9a7bfa11e6ae2256b6b6499611 app_name=\"application-name\" app_version=\"1.0.0-SNAPSHOT\" hostname=\"localhost\"] 69427d6c966046c58804d7f4128f7505 MyApp: GOOD");
            i++;
        }
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
        Response insertEventResponse = javaRestApiDataProvider.insertDocumentIntoIndex("event5", event, "1");
        List<Event> allEvents = javaRestApiDataProvider.getAllEvents("event5");
        List<Event> workshopEventsOnly = javaRestApiDataProvider.workshopEventsOnly("event5");
        List<Event> eventsByTitle = javaRestApiDataProvider.eventsWithTitle("event5", "tech-talk");
        List<Event> eventsByTitleAndAfterDate = javaRestApiDataProvider.eventsWithTitleAndAfterDate("event5", "workshop", new Date(1650037500000L));
        Response response = javaRestApiDataProvider.deleteAllEventsWithTitle("event5", "workshop");
        List<Event> allEventsAfter = javaRestApiDataProvider.getAllEvents("event5");

        LOGGER.info(insertEventResponse);
        LOGGER.info(allEvents);
        LOGGER.info(workshopEventsOnly);
        LOGGER.info(eventsByTitle);
        LOGGER.info(eventsByTitleAndAfterDate);
        LOGGER.info(response);
        LOGGER.info(allEventsAfter);
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

        LOGGER.info(events);
        LOGGER.info(workshopEventList);
        LOGGER.info(eventsWithTitle);
        LOGGER.info(eventsWithTitleAndDateAfter);
    }
}
