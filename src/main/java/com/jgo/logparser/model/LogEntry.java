package com.jgo.logparser.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;

public class LogEntry {

    @SerializedName("id")
    private String id;

    @SerializedName("state")
    private String state;

    @SerializedName("timestamp")
    private BigInteger timestamp;

    @SerializedName("type")
    private String type;

    @SerializedName("hostname")
    private String hostname;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public BigInteger getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(BigInteger timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String toJsonString() {
        Gson gson = new Gson();
        String nestedJsonString = gson.toJson(this);

        // We need to flatten the log entries before we put them into logs.
        return nestedJsonString.replace(System.lineSeparator(), "").replace("\t", "");
    }

    public static LogEntry fromJsonString(String jsonString) {
        Gson gson = new Gson();

        return gson.fromJson(jsonString, LogEntry.class);
    }
}
