#! /bin/bash -e

docker build -f Dockerfile-mariadb -t test-eventuate-mysql .
