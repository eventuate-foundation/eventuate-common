FROM mysql:5.7.40-debian

RUN apt-get update && apt-get install -y netcat-traditional gettext && apt-get clean
COPY replication.cnf /etc/mysql/conf.d

COPY 0.activate-additional-scripts.sh /docker-entrypoint-initdb.d

RUN mkdir /additional-scripts
COPY 1.create-database.sql /additional-scripts
COPY 2.initialize-database.sql /additional-scripts
COPY 3.initialize-database.sql /additional-scripts
COPY 3.create-message-table.sql /additional-scripts
COPY 4.initialize-database-json.sql /additional-scripts
COPY 5.initialize-database-db-id.sql /additional-scripts
COPY 9.initialization-completed.sh /docker-entrypoint-initdb.d

RUN touch /docker-entrypoint-initdb.d/1.create-database.sql \
          /docker-entrypoint-initdb.d/2.initialize-database.sql \
          /docker-entrypoint-initdb.d/3.initialize-database.sql \
          /docker-entrypoint-initdb.d/4.initialize-database-json.sql \
          /docker-entrypoint-initdb.d/5.initialize-database-db-id.sql \
          /docker-entrypoint-initdb.d/6.configure-users.sql


COPY mysql-healthcheck.sh /
RUN chmod +x /mysql-healthcheck.sh /docker-entrypoint-initdb.d/9.initialization-completed.sh && chown mysql -R /docker-entrypoint-initdb.d /additional-scripts
HEALTHCHECK --start-period=30s --interval=5s CMD /mysql-healthcheck.sh
