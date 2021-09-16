package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event implements Entity {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public Event(Long eventId, String title, EventType eventType, String dateString, String place, String description, List<String> subTopics) {
        Date tempDate;
        this.eventId = eventId;
        this.title = title;
        this.eventType = eventType;
        try {
            tempDate = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            tempDate = new Date();
        }
        this.date = tempDate;
        this.place = place;
        this.description = description;
        this.subTopics = subTopics;
    }

    private Long eventId;
    private String title;
    private EventType eventType;
    private Date date;
    private String place;
    private String description;
    private List<String> subTopics;
}
