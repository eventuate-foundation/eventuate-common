
CREATE TABLE message${EVENTUATE_OUTBOX_SUFFIX} (
   id VARCHAR(767) PRIMARY KEY,
   destination NVARCHAR(MAX) NOT NULL,
   headers NVARCHAR(MAX) NOT NULL,
   payload NVARCHAR(MAX) NOT NULL,
   published SMALLINT DEFAULT 0,
   message_partition SMALLINT,
   creation_time BIGINT
    );

CREATE INDEX message${EVENTUATE_OUTBOX_SUFFIX}_published_idx ON message${EVENTUATE_OUTBOX_SUFFIX}(published, id);
