#! /bin/bash -e

if [ -z "$DATABASE" ] ; then
  export DATABASE=mssql
fi

docker run $* \
   --name mssqlterm --rm \
   --network=${PWD##*/}_default \
   -e MSSQL_HOST=${DATABASE} \
   mcr.microsoft.com/mssql/server:2017-latest  \
   sh -c 'exec /opt/mssql-tools18/bin/sqlcmd -C -S "$MSSQL_HOST" -U SA -P "Eventuate123!"'

