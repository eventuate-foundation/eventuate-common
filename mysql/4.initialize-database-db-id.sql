USE eventuate;

CREATE TABLE new_message (
  dbid BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  id LONGTEXT,
  destination LONGTEXT NOT NULL,
  headers LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  payload LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  published SMALLINT DEFAULT 0,
  creation_time BIGINT
);

INSERT INTO new_message (id, dbid, destination, headers, payload, published, creation_time)
    VALUES ('', ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000), 'CDC-IGNORED', '{}', '\"ID-GENERATION-STARTING-VALUE\"', 1, ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000));

INSERT INTO new_message (id, destination, headers, payload, published, creation_time)
    SELECT id, destination, headers, payload, published, creation_time FROM message ORDER BY id;

DROP TABLE message;

ALTER TABLE new_message RENAME TO message;

CREATE INDEX message_published_idx ON message(published, dbid);

create table new_events (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  event_id VARCHAR(255),
  event_type LONGTEXT,
  event_data LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  entity_type VARCHAR(255) NOT NULL,
  entity_id VARCHAR(255) NOT NULL,
  triggering_event LONGTEXT,
  metadata LONGTEXT,
  published TINYINT DEFAULT 0
);

INSERT INTO new_events (id, event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata, published)
    VALUES (ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000), '', 'CDC-IGNORED', 'ID-GENERATION-STARTING-VALUE', 'CDC-IGNORED', 'CDC-IGNORED', '', '', 1);

INSERT INTO new_events (event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata, published)
    SELECT event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata, published FROM events ORDER BY event_id;

DROP TABLE events;

ALTER TABLE new_events RENAME TO events;

CREATE INDEX events_idx ON events(entity_type, entity_id, id);

CREATE INDEX events_published_idx ON events(published, id);
