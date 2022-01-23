package com.jgo.logparser;

import com.jgo.logparser.model.Event;
import com.jgo.logparser.model.LogEntry;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EventTest {
    @Test
    public void event_canBeCreatedFromTwoLogEntries() {
        String id = "test";
        String type = "type";
        String hostname = "hostname";

        LogEntry startEntry = new LogEntry();
        startEntry.setId(id);
        startEntry.setType(type);
        startEntry.setHostname(hostname);
        startEntry.setState("STARTED");
        startEntry.setTimestamp(BigInteger.ONE);

        LogEntry endEntry = new LogEntry();
        endEntry.setId(id);
        endEntry.setType(type);
        endEntry.setHostname(hostname);
        endEntry.setState("FINISHED");
        endEntry.setTimestamp(BigInteger.TEN);

        List<LogEntry> entries = new ArrayList<>();
        entries.add(endEntry);
        entries.add(startEntry);

        Event createdEvent = Event.createEventFromEntryPair(entries, 8);
        assertEquals(createdEvent.getId(), id);
        assertEquals(createdEvent.getType(), type);
        assertEquals(createdEvent.getHost(), hostname);
        assertEquals(createdEvent.getDuration(), BigInteger.TEN.subtract(BigInteger.ONE));
        assertTrue(createdEvent.isAlert());
    }
}
