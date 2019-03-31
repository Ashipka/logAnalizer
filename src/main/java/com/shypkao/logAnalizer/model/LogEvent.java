package com.shypkao.logAnalizer.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.Date;

public class LogEvent {
    @JsonAlias("id")
    private String eventId;
    private String state;
    private String type;
    private String host;
    @JsonAlias("timestamp")
    private Date timeStamp;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "event: " + eventId + ", state: " + state + "type: " + type + " host " + host + " timestamp" + timeStamp;
    }
}
