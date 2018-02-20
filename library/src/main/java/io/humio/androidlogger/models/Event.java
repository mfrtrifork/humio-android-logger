package io.humio.androidlogger.models;


import java.util.Date;
import java.util.Map;

public class Event {
    private final long timestamp = new Date().getTime();
    private final Map<String, String> attributes;
    private final String kvparse = "true";
    private final String rawstring;

    public Event(Map<String, String> attributes, String message) {
        this.attributes = attributes;
        this.rawstring = message;
    }
}