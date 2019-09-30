FROM mysql:5.7.13
RUN apt-get update && apt-get install -y netcat-traditional
COPY replication.cnf /etc/mysql/conf.d
COPY 1.initialize-database.sql /docker-entrypoint-initdb.d
COPY 2.initialize-database.sql /docker-entrypoint-initdb.d
COPY 9.initialization-completed.sh /docker-entrypoint-initdb.d
COPY mysql-healthcheck.sh /
RUN chmod +x /mysql-healthcheck.sh /docker-entrypoint-initdb.d/9.initialization-completed.sh && chown mysql -R /docker-entrypoint-initdb.d
HEALTHCHECK --start-period=30s --interval=5s CMD /mysql-healthcheck.sh
