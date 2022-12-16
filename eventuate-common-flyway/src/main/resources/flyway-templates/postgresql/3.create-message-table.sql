
CREATE TABLE message${EVENTUATE_OUTBOX_SUFFIX} (
     id VARCHAR(255) PRIMARY KEY,
     destination TEXT NOT NULL,
     headers TEXT NOT NULL,
     payload TEXT NOT NULL,
     published SMALLINT DEFAULT 0,
     message_partition SMALLINT,
     creation_time BIGINT
);

CREATE INDEX message${EVENTUATE_OUTBOX_SUFFIX}_published_idx ON message${EVENTUATE_OUTBOX_SUFFIX}(published, id);
