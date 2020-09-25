FROM mcr.microsoft.com/mssql/server:2017-latest

WORKDIR /usr/src/app
CMD /bin/bash ./entrypoint.sh
EXPOSE 1433

COPY 1.setup.sql /usr/src/app
COPY 2.setup.sql /usr/src/app
RUN mkdir /usr/src/app/additional-scripts
COPY 3.setup-json.sql /usr/src/app/additional-scripts
COPY 4.setup-db-id.sql /usr/src/app/additional-scripts
COPY create-schema.sh /usr/src/app
COPY entrypoint.sh /usr/src/app

RUN chmod +x /usr/src/app/create-schema.sh