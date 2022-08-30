USE eventuate;
GO

DROP Table IF Exists eventuate.message;
GO
DROP Table IF Exists eventuate.received_messages;
GO

CREATE TABLE eventuate.message (
  id VARCHAR(767) PRIMARY KEY,
  destination NVARCHAR(MAX) NOT NULL,
  headers NVARCHAR(MAX) NOT NULL,
  payload NVARCHAR(MAX) NOT NULL,
  published SMALLINT DEFAULT 0,
  message_partition SMALLINT,
  creation_time BIGINT
);
GO

CREATE INDEX message_published_idx ON eventuate.message(published, id);
GO

CREATE TABLE eventuate.received_messages (
  consumer_id VARCHAR(767),
  message_id VARCHAR(767),
  published SMALLINT DEFAULT 0,
  PRIMARY KEY(consumer_id, message_id),
  creation_time BIGINT
);
GO

CREATE TABLE eventuate.offset_store(
  client_name VARCHAR(255) NOT NULL PRIMARY KEY,
  serialized_offset VARCHAR(255)
);
GO
