#! /bin/bash -e

export DATABASE=mysql8-multi-arch

./mysql/build-docker-mysql-8-multi-arch.sh
./zookeeper/build-docker-zookeeper-multi-arch.sh

docker pull localhost:5002/eventuate-mysql8:multi-arch-local-build
docker pull localhost:5002/eventuate-zookeeper:multi-arch-local-build

./_build-and-test-all.sh

 ./gradlew vanillamysql8ComposeUp vanillamysq8ComposeDown
