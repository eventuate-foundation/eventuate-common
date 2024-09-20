FROM postgres:13.13-bullseye

RUN  apt-get update \
    && apt-get install -y postgresql-13-wal2json \
    && rm -rf /var/lib/apt/lists/*

# Copy the custom configuration which will be passed down to the server (using a .sample file is the preferred way of doing it by
# the base Docker image)
COPY postgresql.conf.sample /usr/share/postgresql/postgresql.conf.sample

# Copy the script which will initialize the replication permissions
COPY docker-entrypoint-initdb.d /docker-entrypoint-initdb.d

# Initialize schema
COPY 0.activate-additional-scripts.sh /docker-entrypoint-initdb.d

COPY 1.initialize-database.sql /docker-entrypoint-initdb.d
COPY 2.initialize-database.sql /docker-entrypoint-initdb.d
COPY 3.initialize-database.sql /docker-entrypoint-initdb.d

RUN mkdir additional-scripts
RUN chown postgres -R /additional-scripts /docker-entrypoint-initdb.d

COPY 4.initialize-database-json.sql /additional-scripts
COPY 5.initialize-database-db-id.sql /additional-scripts

RUN touch /docker-entrypoint-initdb.d/4.initialize-database-json.sql
RUN touch /docker-entrypoint-initdb.d/5.initialize-database-db-id.sql
