#! /bin/bash -e

export DATABASE=postgres-multi-arch
export SPRING_PROFILES_ACTIVE=postgres
export MICRONAUT_ENVIRONMENTS=postgres

./_build-and-test-all.sh

./gradlew vanillapostgresComposeUp vanillapostgresComposeDown
