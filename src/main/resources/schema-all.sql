DROP TABLE log_event IF EXISTS;
DROP TABLE event_duration IF EXISTS;

CREATE TABLE log_event  (
    id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    event_id varchar(20) NOT NULL,
    type VARCHAR(20),
    state VARCHAR(20) NOT NULL,
    host VARCHAR(20),
    time_stamp TIMESTAMP NOT NULL
);

CREATE INDEX idx_log_event
ON log_event (event_id, state);

CREATE TABLE event_duration  (
    id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    event_id varchar(20) NOT NULL,
    duration INTEGER,
    type VARCHAR(20),
    host VARCHAR(20),
    alert BOOLEAN
  );

CREATE INDEX idx_event_duration
ON event_duration (event_id);

