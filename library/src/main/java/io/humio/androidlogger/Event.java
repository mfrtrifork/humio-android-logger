package io.humio.androidlogger;


import java.util.Date;
import java.util.Map;

class Event {
    private final long timestamp = new Date().getTime();
    private final Map<String, String> attributes;
    private final String rawstring;
    private final String kvparse = "true";

    Event(Map<String, String> attributes, String message) {
        this.attributes = attributes;
        this.rawstring = message;
    }
}