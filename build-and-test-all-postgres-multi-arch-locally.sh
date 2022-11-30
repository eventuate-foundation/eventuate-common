#! /bin/bash -e

export DATABASE=postgres-multi-arch
export SPRING_PROFILES_ACTIVE=postgres
export MICRONAUT_ENVIRONMENTS=postgres

./postgres/build-docker-postgres-multi-arch.sh
./zookeeper/build-docker-zookeeper-multi-arch.sh

docker pull localhost:5002/eventuate-postgres:multi-arch-local-build
docker pull localhost:5002/eventuate-zookeeper:multi-arch-local-build

./_build-and-test-all.sh

./gradlew vanillapostgresComposeUp vanillapostgresComposeDown
