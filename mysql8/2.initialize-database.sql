USE eventuate;

DROP Table IF Exists message;
DROP Table IF Exists received_messages;

CREATE TABLE message (
  id VARCHAR(767) CHARACTER SET latin1 PRIMARY KEY,
  destination LONGTEXT NOT NULL,
  headers LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  payload LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  published SMALLINT DEFAULT 0,
  creation_time BIGINT
);

CREATE INDEX message_published_idx ON message(published, id);

CREATE TABLE received_messages (
  consumer_id VARCHAR(767) CHARACTER SET latin1,
  message_id VARCHAR(767) CHARACTER SET latin1,
  creation_time BIGINT,
  PRIMARY KEY(consumer_id, message_id)
);

CREATE TABLE eventuate.offset_store(
  client_name VARCHAR(255) NOT NULL PRIMARY KEY,
  serialized_offset LONGTEXT
);
