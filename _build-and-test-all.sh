#! /bin/bash

export TERM=dumb

set -e

GRADLE_OPTS=""

if [ "$1" = "--clean" ] ; then
  GRADLE_OPTS="clean"
  shift
fi

docker="./gradlew ${DATABASE?}Compose"
dockerjson="./gradlew ${DATABASE?}jsonCompose"
dockerdatabaseid="./gradlew ${DATABASE?}databaseidCompose"

${docker}Down
${dockerjson}Down
${dockerdatabaseid}Down

./gradlew ${GRADLE_OPTS} testClasses

${docker}UP

echo ""
echo "TESTING REGULAR DATABASE"
echo ""

./gradlew $* cleanTest build

${docker}Down



cd ${DATABASE}
sh ./build-docker.sh
cd ..



${dockerdatabaseid}Up

export EVENTUATELOCAL_CDC_READER_ID=1

echo ""
echo "TESTING DATABASE WITH DATABASE ID"
echo ""

./gradlew $* :eventuate-common-micronaut-data-jdbc:cleanTest :eventuate-common-micronaut-data-jdbc:test \
:eventuate-common-micronaut-spring-jdbc:cleanTest :eventuate-common-micronaut-spring-jdbc:test \
:eventuate-common-spring-jdbc:cleanTest :eventuate-common-spring-jdbc:test

${dockerdatabaseid}Down



unset EVENTUATELOCAL_CDC_READER_ID



${dockerjson}Up

echo ""
echo "TESTING DATABASE WITH JSON SUPPORT"
echo ""

./gradlew $* :eventuate-common-micronaut-data-jdbc:cleanTest :eventuate-common-micronaut-data-jdbc:test \
:eventuate-common-micronaut-spring-jdbc:cleanTest :eventuate-common-micronaut-spring-jdbc:test \
:eventuate-common-spring-jdbc:cleanTest :eventuate-common-spring-jdbc:test

${dockerjson}Down
