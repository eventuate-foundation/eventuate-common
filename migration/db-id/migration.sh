#! /bin/bash

set -e

if [ -z "$DB_ID_MIGRATION_REPOSITORY" ] ; then
  export DB_ID_MIGRATION_REPOSITORY=https://raw.githubusercontent.com/eventuate-foundation/eventuate-common
fi

get_db_id_migration_path () {
  search="eventuateCommonVersion="
  version_line="$(grep $search ./gradle.properties)"
  version=${version_line#$search}
  echo "${DB_ID_MIGRATION_REPOSITORY}/${version}"
}

if [ "${DATABASE}" == "mysql" ]; then
  curl -s $(get_db_id_migration_path)/mysql/4.initialize-database-db-id.sql &> /dev/stdout | ./mysql-cli.sh -i
elif [ "${DATABASE}" == "postgres" ]; then
  curl -s $(get_db_id_migration_path)/postgres/5.initialize-database-db-id.sql &> /dev/stdout | ./postgres-cli.sh -i
elif [ "${DATABASE}" == "mssql" ]; then
  rm -rf migration
  migration_file=migration/migration.sql
  migration_tool=docker-compose-mssql-migration-tool.yml
  migration_entrypoint=migration/entrypoint.sh

  curl $(get_db_id_migration_path)/mssql/4.setup-db-id.sql --output ${migration_file} --create-dirs
  curl "$(get_db_id_migration_path)/migration/db-id/mssql/${migration_tool}" --output ${migration_tool}
  curl "$(get_db_id_migration_path)/migration/db-id/mssql/entrypoint.sh" --output ${migration_entrypoint}
  docker-compose -f docker-compose-mssql-polling.yml -f ${migration_tool} up --build --no-deps mssql-migration
  docker-compose -f docker-compose-mssql-polling.yml -f ${migration_tool} stop mssql-migration
  docker-compose -f docker-compose-mssql-polling.yml -f ${migration_tool} rm -f mssql-migration

  rm -rf migration
  rm -rf ${migration_tool}

else
  echo "Unknown Database ${DATABASE}"
  exit 99
fi