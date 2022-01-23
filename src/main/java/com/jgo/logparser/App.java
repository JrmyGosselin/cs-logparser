package com.jgo.logparser;

import com.jgo.logparser.db.client.EventWriter;
import com.jgo.logparser.db.server.EventDataBaseServer;
import com.jgo.logparser.parser.LogParser;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {

        // Start file based HSQL server
        EventDataBaseServer server = new EventDataBaseServer();
        server.start();

        // Parse the log passed in arguments
        LogParser logParser = new LogParser();
        EventWriter eventWriter = new EventWriter();
        logParser.attachEventWriter(eventWriter);

        logParser.parseLogs(Paths.get(args[0]));

        server.displayEvents();
        server.stop();
    }
}
