package com.jgo.logparser.parser;

import com.jgo.logparser.db.client.EventWriter;
import com.jgo.logparser.model.Event;
import com.jgo.logparser.model.LogEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogParser {
    private static final long ALERT_THRESHOLD = 4;

    EventWriter eventWriter;

    public void attachEventWriter(EventWriter eventWriter) {
        this.eventWriter = eventWriter;
    }

    public ParsingResults parseLogs(Path logs) throws IOException {
        Map<String, List<LogEntry>> pairedEntries = new HashMap<>();
        ParsingResults parsingResults = new ParsingResults();

        if(!Files.exists(logs)) {
            throw new RuntimeException("Log file not found");
        }

        // We need to use streams to read the file, otherwise we might bloat the memory
        // lines() uses lazy loading, so it should work fine with very large files

        // As we find "complete" events (i.e events that have started and finished), we send them directly into the db
        // (if a writer is attached) and then remove them from the memory.
        Files.lines(logs).forEach((line) -> {
            LogEntry logEntry = LogEntry.fromJsonString(line);

            if(!pairedEntries.containsKey(logEntry.getId())) {
                pairedEntries.put(logEntry.getId(), new ArrayList<>());
            }

            List<LogEntry> correspondingEntries = pairedEntries.get(logEntry.getId());
            correspondingEntries.add(logEntry);

            if(correspondingEntries.size() == 2) {
                Event event = Event.createEventFromEntryPair(correspondingEntries, ALERT_THRESHOLD);

                parsingResults.addEvent(event);

                if(eventWriter != null) {
                    eventWriter.writeEventWithThreadedExecutor(event);
                }

                // When done with the event, remove the entries from the map, to prevent the memory from being bloated
                // with data that has already been sent to the database
                pairedEntries.remove(logEntry.getId());
            }
        });

        if(eventWriter != null) {
            eventWriter.close();
        }

        return parsingResults;
    }
}
