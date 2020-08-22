#! /bin/bash

export DATABASE=mssql
export SPRING_PROFILES_ACTIVE=mssql
export MICRONAUT_ENVIRONMENTS=mssql


./_build-and-test-all.sh