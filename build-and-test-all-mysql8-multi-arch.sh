#! /bin/bash

export DATABASE=mysql8-multi-arch

(cd mysql ;  ./build-docker-mysql-8-multi-arch.sh)

docker pull localhost:5002/eventuate-mysql8:local-build

./_build-and-test-all.sh
