DROP TABLE IF EXISTS eventuate.message CASCADE;

CREATE TABLE eventuate.message (
  id VARCHAR(1000) PRIMARY KEY,
  destination TEXT NOT NULL,
  headers JSON NOT NULL,
  payload JSON NOT NULL,
  published SMALLINT DEFAULT 0,
  creation_time BIGINT
);
