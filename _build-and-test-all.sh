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

${docker}Down
${dockerjson}Down

./gradlew ${GRADLE_OPTS} testClasses

${docker}UP

./gradlew $* cleanTest build

${docker}Down

cd ${DATABASE}
sh ./build-docker.sh
cd ..

${dockerjson}Up

./gradlew $* :eventuate-common-micronaut-data-jdbc:cleanTest :eventuate-common-micronaut-data-jdbc:test \
:eventuate-common-micronaut-spring-jdbc:cleanTest :eventuate-common-micronaut-spring-jdbc:test \
:eventuate-common-spring-jdbc:cleanTest :eventuate-common-spring-jdbc:test

${dockerjson}Down
