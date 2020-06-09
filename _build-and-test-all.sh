#! /bin/bash

export TERM=dumb

set -e

. ./set-env-${DATABASE}.sh

GRADLE_OPTS=""

if [ "$1" = "--clean" ] ; then
  GRADLE_OPTS="clean"
  shift
fi

docker-compose -f docker-compose-${DATABASE}.yml down
docker-compose -f docker-compose-${DATABASE}-json.yml down

./gradlew ${GRADLE_OPTS} testClasses

docker-compose -f docker-compose-${DATABASE}.yml up --build -d

./wait-for-${DATABASE}.sh

./gradlew $* cleanTest build

docker-compose -f docker-compose-${DATABASE}.yml down


cd ${DATABASE}
sh ./build-docker.sh
cd ..

docker-compose -f docker-compose-${DATABASE}-json.yml up --build -d

./wait-for-${DATABASE}.sh

./gradlew $* :eventuate-common-micronaut-data-jdbc:cleanTest :eventuate-common-micronaut-data-jdbc:test \
:eventuate-common-micronaut-spring-jdbc:cleanTest :eventuate-common-micronaut-spring-jdbc:test \
:eventuate-common-spring-jdbc:cleanTest :eventuate-common-spring-jdbc:test

docker-compose -f docker-compose-${DATABASE}-json.yml down
