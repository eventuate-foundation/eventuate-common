USE eventuate;

DROP Table IF Exists message;

CREATE TABLE message (
  id VARCHAR(767) PRIMARY KEY,
  destination LONGTEXT NOT NULL,
  headers JSON NOT NULL,
  payload JSON NOT NULL,
  published SMALLINT DEFAULT 0,
  creation_time BIGINT
);
