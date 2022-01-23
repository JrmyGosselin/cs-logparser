package com.jgo.logparser;

import com.jgo.logparser.model.LogEntry;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class LogEntryTest {

    @Test
    public void entry_toJsonString_shouldBeFlattened() {
        LogEntry entry = new LogEntry();

        entry.setId("test");
        entry.setState("test2");
        entry.setTimestamp(BigInteger.valueOf(123456));

        String stringifiedEntry = entry.toJsonString();

        assertFalse(stringifiedEntry.contains(System.lineSeparator()));
        assertFalse(stringifiedEntry.contains("\t"));
    }

    @Test
    public void entry_fromJsonString_shouldHaveValidValues() {
        String jsonString = "{\"id\":\"nfvabfsyxh\",\"state\":\"STARTED\",\"timestamp\":1491377698825}";

        LogEntry entry = LogEntry.fromJsonString(jsonString);

        assertEquals(entry.getId(), "nfvabfsyxh");
        assertEquals(entry.getState(), "STARTED");
        assertEquals(entry.getTimestamp(), new BigInteger("1491377698825"));
    }
}
