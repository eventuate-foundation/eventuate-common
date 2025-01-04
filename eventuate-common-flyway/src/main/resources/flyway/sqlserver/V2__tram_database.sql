
CREATE TABLE received_messages (
                                             consumer_id VARCHAR(767),
                                             message_id VARCHAR(767),
                                             published SMALLINT DEFAULT 0,
                                             PRIMARY KEY(consumer_id, message_id),
                                             creation_time BIGINT
);
GO
