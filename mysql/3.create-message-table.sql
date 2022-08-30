USE ${EVENTUATE_DATABASE};

DROP Table IF Exists message${EVENTUATE_OUTBOX_SUFFIX};

CREATE TABLE message${EVENTUATE_OUTBOX_SUFFIX} (
     id VARCHAR(255) PRIMARY KEY,
     destination LONGTEXT NOT NULL,
     headers LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
     payload LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
     published SMALLINT DEFAULT 0,
     message_partition SMALLINT,
     creation_time BIGINT
);

CREATE INDEX message${EVENTUATE_OUTBOX_SUFFIX}_published_idx ON message${EVENTUATE_OUTBOX_SUFFIX}(published, id);
