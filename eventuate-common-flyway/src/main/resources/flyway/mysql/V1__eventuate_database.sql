create table cdc_monitoring (
                                reader_id VARCHAR(255) PRIMARY KEY,
                                last_time BIGINT
);


CREATE TABLE offset_store(
                             client_name VARCHAR(255) NOT NULL PRIMARY KEY,
                             serialized_offset TEXT
);
