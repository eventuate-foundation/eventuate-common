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



echo ""
echo "TESTING REGULAR DATABASE"
echo ""



${docker}UP

./gradlew $* cleanTest build

${docker}Down



echo ""
echo "TESTING WITH XID WITH APPLICATION ID GENERATION"
echo ""



cd ${DATABASE}
sh ./build-docker.sh
cd ..

${dockerdatabaseid}Up


./gradlew $* :eventuate-common-micronaut-data-jdbc:cleanTest :eventuate-common-micronaut-data-jdbc:test \
:eventuate-common-micronaut-spring-jdbc:cleanTest :eventuate-common-micronaut-spring-jdbc:test \
:eventuate-common-spring-jdbc:cleanTest :eventuate-common-spring-jdbc:test

${dockerdatabaseid}Down



echo ""
echo "TESTING WITH XID WITH DATABASE ID GENERATION"
echo ""

${dockerdatabaseid}Up

export EVENTUATELOCAL_CDC_READER_ID=1

./gradlew $* :eventuate-common-micronaut-data-jdbc:cleanTest :eventuate-common-micronaut-data-jdbc:test \
:eventuate-common-micronaut-spring-jdbc:cleanTest :eventuate-common-micronaut-spring-jdbc:test \
:eventuate-common-spring-jdbc:cleanTest :eventuate-common-spring-jdbc:test

${dockerdatabaseid}Down

echo ""
echo "TESTING DATABASE WITH JSON SUPPORT"
echo ""



${dockerjson}Up

unset EVENTUATELOCAL_CDC_READER_ID

./gradlew $* :eventuate-common-micronaut-data-jdbc:cleanTest :eventuate-common-micronaut-data-jdbc:test \
:eventuate-common-micronaut-spring-jdbc:cleanTest :eventuate-common-micronaut-spring-jdbc:test \
:eventuate-common-spring-jdbc:cleanTest :eventuate-common-spring-jdbc:test

${dockerjson}Down
