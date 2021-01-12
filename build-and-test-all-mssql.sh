#! /bin/bash

export DATABASE=mssql
export SPRING_PROFILES_ACTIVE=mssql
export MICRONAUT_ENVIRONMENTS=mssql
export EVENTUATEDATABASE=mssql

./_build-and-test-all.sh