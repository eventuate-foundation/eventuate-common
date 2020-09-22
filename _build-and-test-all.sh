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
dockerimprovedid="./gradlew ${DATABASE?}improvedidCompose"

${docker}Down
${dockerjson}Down
${dockerimprovedid}Down

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



${dockerimprovedid}Up

export EVENTUATELOCAL_CDC_READER_ID=1

echo ""
echo "TESTING DATABASE WITH IMPROVED ID"
echo ""

./gradlew $* :eventuate-common-micronaut-data-jdbc:cleanTest :eventuate-common-micronaut-data-jdbc:test \
:eventuate-common-micronaut-spring-jdbc:cleanTest :eventuate-common-micronaut-spring-jdbc:test \
:eventuate-common-spring-jdbc:cleanTest :eventuate-common-spring-jdbc:test

${dockerimprovedid}Down



unset EVENTUATELOCAL_CDC_READER_ID



${dockerjson}Up

echo ""
echo "TESTING DATABASE WITH JSON SUPPORT"
echo ""

./gradlew $* :eventuate-common-micronaut-data-jdbc:cleanTest :eventuate-common-micronaut-data-jdbc:test \
:eventuate-common-micronaut-spring-jdbc:cleanTest :eventuate-common-micronaut-spring-jdbc:test \
:eventuate-common-spring-jdbc:cleanTest :eventuate-common-spring-jdbc:test

${dockerjson}Down
