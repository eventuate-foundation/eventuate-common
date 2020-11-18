#! /bin/bash

if [[ "${USE_JSON_PAYLOAD_AND_HEADERS}" == "true" ]]; then
  rm /docker-entrypoint-initdb.d/3.initialize-database-json.sql
  cp /additional-scripts/3.initialize-database-json.sql docker-entrypoint-initdb.d
  echo "3.initialize-database-json.sql is activated"
fi

if [[ "${USE_DB_ID}" == "true" ]]; then
  rm /docker-entrypoint-initdb.d/4.initialize-database-db-id.sql
  sed -i -e "s/<<CURRENT_TIME_IN_MILLISECONDS>>/$(date +%s000)/g" /additional-scripts/4.initialize-database-db-id.sql
  cp /additional-scripts/4.initialize-database-db-id.sql docker-entrypoint-initdb.d
  echo "db id migration script"
  echo "----------------------"
  cat /docker-entrypoint-initdb.d/4.initialize-database-db-id.sql
  echo "----------------------"
  echo "4.initialize-database-db-id.sql is activated"
fi