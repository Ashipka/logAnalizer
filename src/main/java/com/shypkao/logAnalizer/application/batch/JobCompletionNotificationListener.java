package com.shypkao.logAnalizer.application.batch;

import org.hsqldb.types.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

import static com.shypkao.logAnalizer.application.Main.ALERT_EVENT_INTERVAL_NAME;
import static com.shypkao.logAnalizer.application.batch.SQL.SqlScript.ALERT_EVENTS_SQL;
import static com.shypkao.logAnalizer.application.batch.SQL.SqlScript.FILL_EVENT_DURATION_TABLE_SQL;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
    private static final String EVENT_STARTED = "STARTED";
    private static final String EVENT_FINISHED = "FINISHED";
    private static final Integer ALERT_EVENT_INTERVAL = 4;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private Environment environment;

    @Autowired
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void beforeJob(JobExecution jobExecution){
        log.info("!!! Start batch from JSON to DB");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! Finished batch from JSON to DB");
            fillEventDurationTable();
            log.info("!!! Job finished! Time to verify the results");
            logAlertEvents();
        }
    }

    private Object getAlertEventInterval() {
        if(!StringUtils.isEmpty(environment.getProperty(ALERT_EVENT_INTERVAL_NAME))){
            return Integer.parseInt(environment.getProperty(ALERT_EVENT_INTERVAL_NAME));
        }
        return ALERT_EVENT_INTERVAL;
    }

    private void logAlertEvents() {
        jdbcTemplate.query(ALERT_EVENTS_SQL,
                (rs, row) ->
                        new StringBuilder()
                                .append("Found alert event < eventId: ")
                                .append(rs.getString(1))
                                .append(" | duration: ")
                                .append(rs.getInt(2))
                                .append("  | type: ")
                                .append( Optional.ofNullable(rs.getString(3)).orElse(""))
                                .append(" | host: ")
                                .append(Optional.ofNullable(rs.getString(4)).orElse(""))
                                .append(" >")
                                .toString()
        ).forEach(alertEvent -> log.info(alertEvent));
    }

    private void fillEventDurationTable() {
        log.info("!!! Fill event_duration table from log_event");
        Object[] params = { getAlertEventInterval(), EVENT_STARTED, EVENT_FINISHED};
        int[] types = {Types.INTEGER, Types.VARCHAR, Types.VARCHAR };
        jdbcTemplate.update(FILL_EVENT_DURATION_TABLE_SQL, params, types);
    }
}
