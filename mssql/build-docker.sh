#! /bin/bash -e

SCRIPT_DIR=$(cd $( dirname "${BASH_SOURCE[0]}" ) ; pwd)

docker build -t test-eventuate-mssql "$SCRIPT_DIR"

