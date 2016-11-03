package io.humio.androidlogger;

import java.util.List;
import java.util.Map;

public class IngestRequest {
    private Map<String, String> tags;
    private List<Event> events;

    public IngestRequest(Map<String, String> tags, List<Event> events) {
        this.tags = tags;
        this.events = events;
    }
}