USE ${EVENTUATE_DATABASE};

DROP Table IF Exists received_messages;


CREATE TABLE received_messages (
  consumer_id VARCHAR(255),
  message_id VARCHAR(255),
  creation_time BIGINT,
  published SMALLINT DEFAULT 0,
  PRIMARY KEY(consumer_id, message_id)
);

CREATE TABLE offset_store(
  client_name VARCHAR(255) NOT NULL PRIMARY KEY,
  serialized_offset LONGTEXT
);
