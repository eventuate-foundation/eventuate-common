#! /bin/bash -e

docker login -u ${DOCKER_USER_ID?} -p ${DOCKER_PASSWORD?}

./mysql/build-docker-mysql-8-multi-arch.sh
./zookeeper/build-docker-zookeeper-multi-arch.sh
./postgres/build-docker-postgres-multi-arch.sh
