#! /bin/bash -e

SCRIPT_DIR=$(cd $( dirname "${BASH_SOURCE[0]}" ) ; pwd)

docker build -f "$SCRIPT_DIR"/Dockerfile-mariadb -t test-eventuate-mysql "$SCRIPT_DIR"
