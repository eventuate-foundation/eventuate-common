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

./wait-for-mysql.sh

./gradlew $* cleanTest build
docker-compose down
