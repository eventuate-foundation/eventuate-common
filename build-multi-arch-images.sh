#! /bin/bash -e

docker login -u ${DOCKER_USER_ID?} -p ${DOCKER_PASSWORD?}

# failed to solve: process "/dev/.buildkit_qemu_emulator
# https://github.com/docker/buildx/issues/493#issuecomment-754834977
# https://github.com/tonistiigi/binfmt#installing-emulators

docker run --privileged --rm tonistiigi/binfmt --install arm64,arm

./mysql/build-docker-mysql-8-multi-arch.sh
./zookeeper/build-docker-zookeeper-multi-arch.sh
