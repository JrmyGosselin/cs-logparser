package com.jgo.logparser.db.client;

import com.jgo.logparser.model.Event;
import org.hsqldb.jdbc.JDBCPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EventWriter {
    private static final Logger log = LoggerFactory.getLogger(EventWriter.class);

    private JDBCPool jdbcPool;
    private ExecutorService executorService;

    public EventWriter() {
        executorService = Executors.newFixedThreadPool(5);
        jdbcPool = new JDBCPool(5);
        jdbcPool.setUrl("jdbc:hsqldb:hsql://localhost/events");
        jdbcPool.setUser("sa");
        jdbcPool.setPassword("");
    }

    // This method will be used by the LogParser to send events to the db as the file is being parsed
    // However, we don't want to interrupt the parsing by doing so, therefore this will just fire off the thread that
    // will do the writing and then resume
    public void writeEventWithThreadedExecutor(Event event) {
        executorService.execute(() -> {
            writeEvent(event);
        });
    }

    // This is the formerly used mono-thread version. We keep it for mocking purposes.
    public void writeEvent(Event event) {
        log.debug("Inserting event with id {}", event.getId());
        try (Connection c = getConnectionFromPool()) {
            Statement statement = c.createStatement();
            statement.executeUpdate("INSERT INTO EVENTS " +
                    "VALUES ('" + event.getId() + "'," +
                    event.getDuration() + "," +
                    "'" + event.getType() + "'," +
                    "'" + event.getHost() + "'," +
                    event.isAlert() + ")");
        } catch (SQLException e) {
            // If we have an exception here there's nothing much we can do except log and ignore.
            log.error(event.getId() + " -> " + e.getMessage());
        }
    }

    public void close() {
        try {
            executorService.shutdown();
            if(executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            jdbcPool.close(1);
        } catch (InterruptedException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized Connection getConnectionFromPool() throws SQLException {
        return jdbcPool.getConnection();
    }
}
