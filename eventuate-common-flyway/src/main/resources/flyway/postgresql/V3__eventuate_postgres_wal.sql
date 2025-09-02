SELECT * FROM pg_create_logical_replication_slot('eventuate_slot', 'wal2json')
WHERE NOT EXISTS (
    SELECT 1 FROM pg_replication_slots WHERE slot_name = 'eventuate_slot'
);
