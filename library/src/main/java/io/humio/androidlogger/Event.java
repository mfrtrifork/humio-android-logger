package io.humio.androidlogger;


import java.util.Date;
import java.util.Map;

class Event {
    private final long timestamp = new Date().getTime();
    private final Map<String, String> attributes;

    Event(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}