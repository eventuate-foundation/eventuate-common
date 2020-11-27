USE eventuate;
GO

CREATE TABLE eventuate.new_message (
  id VARCHAR(767),
  dbid BIGINT IDENTITY(1, 1) PRIMARY KEY,
  destination NVARCHAR(MAX) NOT NULL,
  headers NVARCHAR(MAX) NOT NULL,
  payload NVARCHAR(MAX) NOT NULL,
  published SMALLINT DEFAULT 0,
  creation_time BIGINT
);
GO

SET IDENTITY_INSERT eventuate.new_message ON;
GO

INSERT INTO eventuate.new_message (id, dbid, destination, headers, payload, published, creation_time)
    VALUES ('', (DATEDIFF_BIG(ms, '1970-01-01', GETUTCDATE())), 'CDC-IGNORED', '{}', '\"ID-GENERATION-STARTING-VALUE\"', 1, (DATEDIFF_BIG(ms, '1970-01-01', GETUTCDATE())));
GO

SET IDENTITY_INSERT eventuate.new_message OFF;
GO

INSERT INTO eventuate.new_message (id, destination, headers, payload, published, creation_time)
    SELECT id, destination, headers, payload, published, creation_time FROM eventuate.message;
GO

DROP TABLE eventuate.message;
GO

EXEC sp_rename 'eventuate.new_message', 'message';
GO

CREATE INDEX message_published_idx ON eventuate.message(published, dbid);
GO

create table eventuate.new_events (
  id BIGINT IDENTITY(1, 1) PRIMARY KEY,
  event_id varchar(1000),
  event_type varchar(1000),
  event_data varchar(1000) NOT NULL,
  entity_type VARCHAR(1000) NOT NULL,
  entity_id VARCHAR(1000) NOT NULL,
  triggering_event VARCHAR(1000),
  metadata VARCHAR(1000),
  published TINYINT DEFAULT 0
);
GO

SET IDENTITY_INSERT eventuate.new_events ON;
GO

INSERT INTO eventuate.new_events (id, event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata, published)
    VALUES ((DATEDIFF_BIG(ms, '1970-01-01', GETUTCDATE())), '', 'CDC-IGNORED', 'ID-GENERATION-STARTING-VALUE', 'CDC-IGNORED', 'CDC-IGNORED', '', '', 1);
GO

SET IDENTITY_INSERT eventuate.new_events OFF;
GO

INSERT INTO eventuate.new_events (event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata, published)
    SELECT event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata, published FROM eventuate.events;
GO

DROP TABLE eventuate.events;

EXEC sp_rename 'eventuate.new_events', 'events';
GO

CREATE INDEX events_idx ON eventuate.events(entity_type, entity_id, id);
GO
CREATE INDEX events_published_idx ON eventuate.events(published, id);
GO