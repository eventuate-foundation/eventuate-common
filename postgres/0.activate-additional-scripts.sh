#! /bin/bash

if [[ "${USE_JSON_PAYLOAD_AND_HEADERS}" == "true" ]]; then
  rm -f /docker-entrypoint-initdb.d/4.initialize-database-json.sql
  cp /additional-scripts/4.initialize-database-json.sql docker-entrypoint-initdb.d
  echo "4.initialize-database-json.sql is activated"
fi

if [[ "${USE_DB_ID}" == "true" ]]; then
  rm -f /docker-entrypoint-initdb.d/5.initialize-database-db-id.sql
  cp /additional-scripts/5.initialize-database-db-id.sql docker-entrypoint-initdb.d
  echo "5.initialize-database-db-id.sql is activated"
fi