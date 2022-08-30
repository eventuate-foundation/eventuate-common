#! /bin/bash -e


if [[ "${USE_JSON_PAYLOAD_AND_HEADERS}" == "true" ]]; then
  cp /dev/null /docker-entrypoint-initdb.d/4.initialize-database-json.sql
fi

if [[ "${USE_DB_ID}" == "true" ]]; then
  cp /dev/null /docker-entrypoint-initdb.d/5.initialize-database-db-id.sql
fi

cp /dev/null /docker-entrypoint-initdb.d/1.create-database.sql
cp /dev/null /docker-entrypoint-initdb.d/2.initialize-database.sql
cp /dev/null /docker-entrypoint-initdb.d/3.initialize-database.sql

for db in ${EVENTUATE_DATABASES:-eventuate} ; do
  export EVENTUATE_DATABASE=$db

  for template in 1.create-database.sql 2.initialize-database.sql 3.initialize-database.sql; do
    envsubst '$EVENTUATE_DATABASE' < /additional-scripts/$template >> /docker-entrypoint-initdb.d/$template
  done

  envsubst '$EVENTUATE_DATABASE,$EVENTUATE_OUTBOX_SUFFIX' < /additional-scripts/3.create-message-table.sql >> /docker-entrypoint-initdb.d/3.initialize-database.sql

  if [ ! -z "$EVENTUATE_OUTBOX_TABLES" ] ; then
    for i in $(seq 0 $(($EVENTUATE_OUTBOX_TABLES - 1))) ; do
      export EVENTUATE_OUTBOX_SUFFIX=$i

      envsubst '$EVENTUATE_DATABASE,$EVENTUATE_OUTBOX_SUFFIX' < /additional-scripts/3.create-message-table.sql >> /docker-entrypoint-initdb.d/3.initialize-database.sql

      if [[ "${USE_JSON_PAYLOAD_AND_HEADERS}" == "true" ]]; then
        envsubst '$EVENTUATE_DATABASE,$EVENTUATE_OUTBOX_SUFFIX' <  /additional-scripts/4.initialize-database-json.sql >> /docker-entrypoint-initdb.d/4.initialize-database-json.sql
        echo "4.initialize-database-json.sql is activated for $EVENTUATE_DATABASE, $EVENTUATE_OUTBOX_SUFFIX"
      fi

      if [[ "${USE_DB_ID}" == "true" ]]; then
        envsubst '$EVENTUATE_DATABASE,$EVENTUATE_OUTBOX_SUFFIX' <  /additional-scripts/5.initialize-database-db-id.sql >> /docker-entrypoint-initdb.d/5.initialize-database-db-id.sql
        echo "5.initialize-database-db-id.sql is activated for $EVENTUATE_DATABASE, $EVENTUATE_OUTBOX_SUFFIX"
      fi

    done
  fi

  if [[ "${USE_JSON_PAYLOAD_AND_HEADERS}" == "true" ]]; then
    envsubst '$EVENTUATE_DATABASE,$EVENTUATE_OUTBOX_SUFFIX' <  /additional-scripts/4.initialize-database-json.sql >> /docker-entrypoint-initdb.d/4.initialize-database-json.sql
    echo "4.initialize-database-json.sql is activated for $EVENTUATE_DATABASE"
  fi

  if [[ "${USE_DB_ID}" == "true" ]]; then
    envsubst '$EVENTUATE_DATABASE,$EVENTUATE_OUTBOX_SUFFIX' <  /additional-scripts/5.initialize-database-db-id.sql >> /docker-entrypoint-initdb.d/5.initialize-database-db-id.sql
    echo "5.initialize-database-db-id.sql is activated for $EVENTUATE_DATABASE"
  fi

done