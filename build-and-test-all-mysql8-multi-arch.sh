#! /bin/bash -e

export DATABASE=mysql8-multi-arch

./_build-and-test-all.sh

 ./gradlew vanillamysql8ComposeUp vanillamysq8ComposeDown
