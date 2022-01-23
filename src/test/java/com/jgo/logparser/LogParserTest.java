package com.jgo.logparser;

import com.jgo.logparser.db.client.EventWriter;
import com.jgo.logparser.parser.LogParser;
import com.jgo.logparser.parser.ParsingResults;
import com.jgo.logparser.utils.LogGenerator;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LogParserTest {
    @Test
    public void logParser_canHandleLogFiles() throws IOException {
        Path shortFile = Paths.get("src/test/resources/shortFile.txt");
        LogParser logParser = new LogParser();

        ParsingResults results = logParser.parseLogs(shortFile);

        assertEquals(results.getTotal(), BigInteger.valueOf(4));
        assertEquals(results.getAlerts(), BigInteger.valueOf(1));
    }

    @Test
    public void logParser_canHandleVeryLargeLogFiles() throws IOException {
        // This test will generate a very large logfile on your hard drive, read it, then remove it.
        // Creating the logfile is what takes time. This is why I limited this test to a log file containing 10k entries
        Path tempLogFile = Files.createTempFile(Paths.get("src/test/resources"), "bigLog",".txt");

        // The log generator is not perfect, it uses lists to create log entries, shuffles them and then writes them
        // Therefore, using smaller batch sizes increases greatly the creation speed of the big log file
        int iterationsCount = 1500;
        int batchSize = 20;
        for(int i = 0; i<iterationsCount; i++) {
            LogGenerator.addLinesToLog(batchSize, tempLogFile);
        }

        LogParser logParser = new LogParser();

        // We will attach a MOCKED event writer that takes a fixed 5 ms to write an event into the DB
        // It might not be very realistic, but it will give us an idea.
        EventWriter mockedEventWriter = mock(EventWriter.class);
        doAnswer(invocation -> {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    // ignore
                }
                return null;
        }).when(mockedEventWriter).writeEvent(any());

        logParser.attachEventWriter(mockedEventWriter);

        long startTime = System.currentTimeMillis();
        ParsingResults results = logParser.parseLogs(tempLogFile);
        Duration timeElapsed = Duration.of(System.currentTimeMillis() - startTime, ChronoUnit.MILLIS);

        Files.delete(tempLogFile);

        assertEquals(results.getTotal(),
                BigInteger.valueOf(iterationsCount)
                        .multiply(BigInteger.valueOf(batchSize))
                        .divide(BigInteger.valueOf(2)));
        System.out.println("Parsing duration : " + timeElapsed.get(ChronoUnit.NANOS) / 1000000 + " ms");

        // Here are the average results that I got, on my machine, with more interesting numbers :
        // 10 000 log entries   =>  ~215 ms
        // 20 000 log entries   =>  ~315 ms
        // 30 000 log entries   =>  ~500 ms
        // It doesn't look too exponential so I guess the parser will work on very large files
    }

    @Test
    public void logParser_canHaveAnEventWriterAttached() throws IOException {
        Path shortFile = Paths.get("src/test/resources/shortFile.txt");
        LogParser logParser = new LogParser();
        EventWriter eventWriterMock = mock(EventWriter.class);

        logParser.attachEventWriter(eventWriterMock);

        logParser.parseLogs(shortFile);

        // This will check that the event writer is called a given amount of times without having to have a db running
        verify(eventWriterMock, times(4)).writeEventWithThreadedExecutor(any());
    }
}
