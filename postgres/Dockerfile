FROM postgres:10

ENV WAL2JSON_COMMIT_ID=d4c0e814696695bbf853c48b38b7479e0f83f6c7

# Install the packages which will be required to get everything to compile
RUN  apt-get update  && apt-get -y install git build-essential postgresql-server-dev-10

RUN git clone https://github.com/eulerto/wal2json -b master --single-branch \
    && cd /wal2json \
    && git checkout $WAL2JSON_COMMIT_ID \
    && make && make install \
    && cd / \
    && rm -rf wal2json \
    && apt-get clean \
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
