package com.jgo.logparser.model;

import java.math.BigInteger;
import java.util.List;

public class Event {
    String id;
    BigInteger duration;

    String type;
    String host;

    boolean alert;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigInteger getDuration() {
        return duration;
    }

    public void setDuration(BigInteger duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    public static Event createEventFromEntryPair(List<LogEntry> entryPair, long alertThreshold) {
        if(entryPair.size() != 2) {
            throw new IllegalArgumentException("This creates an event from 2 entries, not : " + entryPair.size());
        } else {
            LogEntry logEntry = entryPair.get(0);
            Event event = new Event();
            event.setId(logEntry.getId());

            event.setType(logEntry.getType());
            event.setHost(logEntry.getHostname());

            BigInteger duration = getEventDuration(entryPair);
            event.setDuration(duration);
            event.setAlert(BigInteger.valueOf(alertThreshold).compareTo(duration) < 0);

            return event;
        }
    }

    private static BigInteger getEventDuration(List<LogEntry> entries) {
        LogEntry startingEntry = entries.stream()
                .filter(logEntry -> logEntry.getState().equals("STARTED"))
                .findFirst().orElseThrow(IllegalArgumentException::new);

        LogEntry endingEntry = entries.stream()
                .filter(logEntry -> logEntry.getState().equals("FINISHED"))
                .findFirst().orElseThrow(IllegalArgumentException::new);

        return endingEntry.getTimestamp().subtract(startingEntry.getTimestamp());
    }
}
