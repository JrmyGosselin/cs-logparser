package com.jgo.logparser.utils;

import com.jgo.logparser.model.LogEntry;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

// This is a utility class that will help us with creating large datasets
public class LogGenerator {
    private static final BigInteger ROOT_TIMESTAMP = new BigInteger("1491377000000");

    public static List<LogEntry> generateRandomEntries(long entryCount) {
        if(entryCount % 2 != 0) {
            throw new IllegalArgumentException("Entry count should be even");
        } else {
            List<LogEntry> logEntries = new ArrayList<>();

            // Generating entries 2 by 2
            for(int i = 0; i<entryCount; i=i+2) {
                LogEntry startEntry = new LogEntry();
                LogEntry endEntry = new LogEntry();

                // Using UUID to avoid duplicates in large files
                String eventId = UUID.randomUUID().toString();
                startEntry.setId(eventId);
                endEntry.setId(eventId);

                BigInteger startTimestamp = ROOT_TIMESTAMP.add(
                        BigInteger.valueOf(getRandomInteger(0,999999)));
                startEntry.setTimestamp(startTimestamp);

                BigInteger endTimestamp = startTimestamp.add(
                        BigInteger.valueOf(getRandomInteger(1,6)));
                endEntry.setTimestamp(endTimestamp);

                startEntry.setState("STARTED");
                endEntry.setState("FINISHED");

                String hostName = RandomStringUtils.randomAlphabetic(5);
                startEntry.setHostname(hostName);
                endEntry.setHostname(hostName);

                String type = RandomStringUtils.randomAlphabetic(5);
                startEntry.setType(type);
                endEntry.setType(type);

                logEntries.add(startEntry);
                logEntries.add(endEntry);
            }

            Collections.shuffle(logEntries);

            return logEntries;
        }
    }

    // If you want to create very large files (with more lines than long allows), just call this multiple times
    // Calling with lines > 10000 results in bad performance, because it creates large lists
    public static void addLinesToLog(long lines, Path logFile) throws IOException {
        List<LogEntry> logEntries = generateRandomEntries(lines);
        for(LogEntry entry : logEntries) {
            appendEntryToFile(entry, logFile);
        }
    }

    private static void appendEntryToFile(LogEntry entry, Path targetFile) throws IOException {
        try {
            Files.createFile(targetFile);
        } catch (FileAlreadyExistsException ex) {
            // ignore this
        }
        Files.write(targetFile,
                (entry.toJsonString() + System.lineSeparator()).getBytes(),
                StandardOpenOption.APPEND);
    }

    public static void writeEntriesToFile(List<LogEntry> entries, Path targetFile) throws IOException {
        Files.createFile(targetFile);
        for(LogEntry entry : entries) {
            Files.write(targetFile,
                    (entry.toJsonString() + System.lineSeparator()).getBytes(),
                    StandardOpenOption.APPEND);
        }
    }

    private static int getRandomInteger(int lowerBound, int upperBound) {
        return ThreadLocalRandom.current().nextInt(lowerBound, upperBound);
    }

    public static void main(String[] args) throws IOException {
        List<LogEntry> logEntries = LogGenerator.generateRandomEntries(8);

        writeEntriesToFile(logEntries, Paths.get("src/test/resources/shortFile.txt"));
    }
}
