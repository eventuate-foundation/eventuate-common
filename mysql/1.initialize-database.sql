create database eventuate;
GRANT ALL PRIVILEGES ON eventuate.* TO 'mysqluser'@'%' WITH GRANT OPTION;

USE eventuate;

DROP table IF EXISTS events;
DROP table IF EXISTS  entities;
DROP table IF EXISTS  snapshots;
DROP table IF EXISTS cdc_monitoring;

create table events (
  event_id VARCHAR(255) PRIMARY KEY,
  event_type LONGTEXT,
  event_data LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  entity_type VARCHAR(255) NOT NULL,
  entity_id VARCHAR(255) NOT NULL,
  triggering_event LONGTEXT,
  metadata LONGTEXT,
  published TINYINT DEFAULT 0
);

CREATE INDEX events_idx ON events(entity_type, entity_id, event_id);
CREATE INDEX events_published_idx ON events(published, event_id);

create table entities (
  entity_type VARCHAR(255),
  entity_id VARCHAR(255),
  entity_version LONGTEXT NOT NULL,
  PRIMARY KEY(entity_type, entity_id)
);

CREATE INDEX entities_idx ON entities(entity_type, entity_id);

create table snapshots (
  entity_type VARCHAR(255),
  entity_id VARCHAR(255),
  entity_version VARCHAR(255),
  snapshot_type LONGTEXT NOT NULL,
  snapshot_json LONGTEXT NOT NULL,
  triggering_events LONGTEXT,
  PRIMARY KEY(entity_type, entity_id, entity_version)
);

create table cdc_monitoring (
  reader_id VARCHAR(255) PRIMARY KEY,
  last_time BIGINT
);
