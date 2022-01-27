#! /bin/bash

MYSQL8_MULTI_ARCH_IMAGE=eventuateio/eventuate-mysql8:test-build-${CIRCLE_SHA1?}

docker login -u ${DOCKER_USER_ID?} -p ${DOCKER_PASSWORD?}

docker buildx build --platform linux/amd64,linux/arm64 \
  -t $MYSQL8_MULTI_ARCH_IMAGE \
  -f mysql/Dockerfile-mysql8-multi-arch \
  --push \
  mysql
