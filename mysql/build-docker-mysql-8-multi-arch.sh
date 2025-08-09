#! /bin/bash -e

SCRIPT_DIR=$(cd $( dirname "${BASH_SOURCE[0]}" ) ; pwd)

docker compose --file $SCRIPT_DIR/../docker-compose-registry.yml --project-name eventuate-common-registry up -d registry

docker buildx build --platform ${BUILDX_TARGET_PLATFORMS:-linux/amd64,linux/arm64} \
  -t ${MYSQL8_MULTI_ARCH_IMAGE:-${DOCKER_HOST_NAME:-host.docker.internal}:5002/eventuate-mysql8:multi-arch-local-build} \
  -f $SCRIPT_DIR/Dockerfile-mysql8 \
  ${BUILDX_PUSH_OPTIONS:---output=type=image,push=true,registry.insecure=true} \
  $SCRIPT_DIR

