#! /bin/bash

export DATABASE=postgres
export SPRING_PROFILES_ACTIVE=postgres
export MICRONAUT_ENVIRONMENTS=postgres
export EVENTUATEDATABASE=postgres

./_build-and-test-all.sh