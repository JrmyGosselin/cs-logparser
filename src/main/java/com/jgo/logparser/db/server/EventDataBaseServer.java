package com.jgo.logparser.db.server;

import org.hsqldb.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class EventDataBaseServer {
    private static final Logger log = LoggerFactory.getLogger(EventDataBaseServer.class);

    private static final String DATABASE_PATH = "file:hsqldb_catalog";
    private static final String DATABASE_EVENT_TABLE_NAME = "EVENTS";

    private Server server = new Server();

    // This will start the server and make sure everything is in order, meaning that the table that stores event exists
    public void start() throws ClassNotFoundException, SQLException {
        server.setLogWriter(null);
        server.setSilent(true);

        server.setDatabaseName(0, "events");
        server.setDatabasePath(0, DATABASE_PATH);

        log.info("Starting event HSQLDB server.");
        server.start();

        Class.forName("org.hsqldb.jdbcDriver");
        Connection connection = DriverManager.getConnection(
                "jdbc:hsqldb:hsql://localhost/events", "sa", "");

        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet tables =
                databaseMetaData.getTables(null, null, DATABASE_EVENT_TABLE_NAME, null);

        if (tables.next()) {
            // Table already exists
            int existingRecords = getNumberOfEventsAlreadyRecorded(connection);
            log.info("Table already created - " + existingRecords + " existing record(s)");
        } else {
            log.info("Table does not exist. Creating one...");
            String tableCreationStatement = String.format("CREATE TABLE %s (" +
                    "id VARCHAR(50) NOT NULL , " +
                    "duration BIGINT NOT NULL, " +
                    "type VARCHAR(50), " +
                    "host VARCHAR(50), " +
                    "alert BOOLEAN NOT NULL, " +
                    "PRIMARY KEY (id));", DATABASE_EVENT_TABLE_NAME);

            Statement statement = connection.createStatement();

            statement.executeUpdate(tableCreationStatement);

            log.info("Table is created and ready to receive events.");
        }

        connection.close();
    }

    public void stop() {
        log.info("Stopping event HSQLDB server.");
        if(!server.isNotRunning()) {
            server.stop();
        }
    }

    private int getNumberOfEventsAlreadyRecorded(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM EVENTS");
        int count = 0;
        while(rs.next()) {
            count = rs.getInt(1);
        }
        return count;
    }

    // Mostly a manual test utility
    public void displayEvents() {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:hsqldb:hsql://localhost/events", "sa", "");

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM EVENTS");

            int count = 0;
            int columnsCount = rs.getMetaData().getColumnCount();
            while(rs.next()) {
                count++;
                StringBuilder eventAsString = new StringBuilder();
                for(int i = 1; i<=columnsCount; i++) {
                    eventAsString.append(rs.getString(i)).append(" | ");
                }
                log.debug(eventAsString.toString());
            }

            log.info("Events in database : {}", count);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
