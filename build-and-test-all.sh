#! /bin/bash

export TERM=dumb

set -e

. ./set-env.sh

GRADLE_OPTS=""

if [ "$1" = "--clean" ] ; then
  GRADLE_OPTS="clean"
  shift
fi

./gradlew ${GRADLE_OPTS} testClasses

docker-compose up --build -d

#./gradlew $* eventuate-common-jdbc:cleanTest eventuate-common-jdbc:test eventuate-common-kafka:cleanTest eventuate-common-kafka:test
./gradlew $* cleanTest test
docker-compose down
