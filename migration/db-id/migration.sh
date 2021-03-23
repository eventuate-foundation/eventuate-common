#! /bin/bash

set -e

if [ -z "$DB_ID_MIGRATION_REPOSITORY" ] ; then
  export DB_ID_MIGRATION_REPOSITORY=https://raw.githubusercontent.com/eventuate-foundation/eventuate-common
fi

if [ -z "$DB_ID_MIGRATION_DIR" ] ; then
  # docker-compose understand path only with "./" part.
  # Also it should be part of the variable.
  # docker-compose cannot parse this ./${DB_ID_MIGRATION_DIR}
  export DB_ID_MIGRATION_DIR=./tmp-migration
fi

get_db_id_migration_path () {
  search="eventuateCommonVersion="
  version_line="$(grep $search ./gradle.properties)"
  version=${version_line#$search}

  if [[ $version =~ "BUILD-SNAPSHOT" ]]; then
     version=master
  fi

  echo "${DB_ID_MIGRATION_REPOSITORY}/${version}"
}

db_id_migration_path=$(get_db_id_migration_path)

if [ "${DATABASE}" == "mysql" ]; then
  curl -s ${db_id_migration_path}/mysql/4.initialize-database-db-id.sql &> /dev/stdout | ./mysql-cli.sh -i
elif [ "${DATABASE}" == "postgres" ]; then
  curl -s ${db_id_migration_path}/postgres/5.initialize-database-db-id.sql &> /dev/stdout | ./postgres-cli.sh -i
elif [ "${DATABASE}" == "mssql" ]; then
  rm -rf ${DB_ID_MIGRATION_DIR}
  migration_file=${DB_ID_MIGRATION_DIR}/migration.sql
  migration_tool=docker-compose-mssql-migration-tool.yml
  migration_entrypoint=${DB_ID_MIGRATION_DIR}/entrypoint.sh

  curl ${db_id_migration_path}/mssql/4.setup-db-id.sql --output ${migration_file} --create-dirs
  curl ${db_id_migration_path}/migration/db-id/mssql/${migration_tool} --output ${migration_tool}
  curl ${db_id_migration_path}/migration/db-id/mssql/entrypoint.sh --output ${migration_entrypoint}
  docker-compose -f docker-compose-mssql-polling.yml -f ${migration_tool} up --build --no-deps mssql-migration
  docker-compose -f docker-compose-mssql-polling.yml -f ${migration_tool} stop mssql-migration
  docker-compose -f docker-compose-mssql-polling.yml -f ${migration_tool} rm -f mssql-migration

  rm -rf ${DB_ID_MIGRATION_DIR}
  rm -rf ${migration_tool}

else
  echo "Unknown Database ${DATABASE}"
  exit 99
fi