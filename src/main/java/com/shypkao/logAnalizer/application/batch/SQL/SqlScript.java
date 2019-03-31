package com.shypkao.logAnalizer.application.batch.SQL;

public class SqlScript {
    public static final String INSERT_LOG_EVENT_ROW_SQL =
            "INSERT INTO log_event (event_id, type , state, host, time_stamp) " +
            "VALUES (:eventId, :type, :state, :host, :timeStamp)";

    public static final String FILL_EVENT_DURATION_TABLE_SQL =
            "INSERT INTO event_duration (event_id, duration, type, host, alert) "
            + "SELECT logEventStarted.event_id, " +
            "DATEDIFF(millisecond,logEventStarted.time_stamp, logEventFinished.time_stamp), " +
            "logEventStarted.type, " +
            "logEventStarted.host,  " +
            "CASE" +
            "   WHEN DATEDIFF(millisecond,logEventStarted.time_stamp, logEventFinished.time_stamp) >= :alertEventInterval THEN " +
            "       1 " +
            "   ELSE " +
            "       0 " +
            "   END " +
            "FROM (SELECT event_id, time_stamp, type, host FROM LOG_EVENT where state = :eventStarted) logEventStarted " +
            "JOIN (SELECT event_id, time_stamp FROM LOG_EVENT where state = :eventFinished) logEventFinished " +
            "ON logEventStarted.event_id = logEventFinished.event_id ";

    public static final String ALERT_EVENTS_SQL =
            "SELECT event_id, duration, type, host FROM event_duration where alert = 1";
}
