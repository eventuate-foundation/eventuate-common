FROM mcr.microsoft.com/mssql/server:2025-latest

WORKDIR /usr/src/app
CMD /bin/bash ./entrypoint.sh
EXPOSE 1433

COPY 1.setup.sql /usr/src/app
COPY 2.setup.sql /usr/src/app
RUN mkdir /usr/src/app/additional-scripts
COPY 3.setup-json.sql /usr/src/app/additional-scripts
COPY 4.setup-db-id.sql /usr/src/app/additional-scripts
COPY 9.initialization-completed.sh /usr/src/app/additional-scripts
COPY create-schema.sh /usr/src/app
COPY entrypoint.sh /usr/src/app

RUN chmod +x /usr/src/app/create-schema.sh

COPY mssql-healthcheck.sh /
RUN chmod +x /mssql-healthcheck.sh
HEALTHCHECK --start-period=30s --interval=5s CMD /mssql-healthcheck.sh