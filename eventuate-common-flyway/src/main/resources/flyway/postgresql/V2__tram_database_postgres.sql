CREATE TABLE received_messages (
                                             consumer_id VARCHAR(1000),
                                             message_id VARCHAR(1000),
                                             creation_time BIGINT,
                                             published SMALLINT DEFAULT 0,
                                             PRIMARY KEY(consumer_id, message_id)
);

