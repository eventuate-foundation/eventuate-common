#! /bin/bash

export TERM=dumb

set -e

. ./set-env-${DATABASE}.sh

GRADLE_OPTS=""

if [ "$1" = "--clean" ] ; then
  GRADLE_OPTS="clean"
  shift
fi

./gradlew ${GRADLE_OPTS} testClasses

docker-compose -f docker-compose-${DATABASE}.yml up --build -d

./wait-for-${DATABASE}.sh

./gradlew $* cleanTest build
docker-compose -f docker-compose-${DATABASE}.yml down
