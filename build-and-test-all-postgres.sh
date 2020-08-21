#! /bin/bash

export DATABASE=postgres
export SPRING_PROFILES_ACTIVE=postgres
export MICRONAUT_ENVIRONMENTS=postgres

./_build-and-test-all.sh