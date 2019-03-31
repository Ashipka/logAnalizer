package com.shypkao.logAnalizer.application.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shypkao.logAnalizer.model.LogEvent;
import org.springframework.batch.item.file.LineMapper;

public class LogEventJsonLineMapper implements LineMapper<LogEvent> {
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public LogEvent mapLine(String line, int lineNumber) throws Exception {
        return mapper.readValue(line, LogEvent.class);
    }
}
