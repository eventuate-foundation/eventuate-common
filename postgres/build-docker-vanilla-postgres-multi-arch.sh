#! /bin/bash -e

SCRIPT_DIR=$(cd $( dirname "${BASH_SOURCE[0]}" ) ; pwd)

docker-compose -f $SCRIPT_DIR/../docker-compose-registry.yml --project-name eventuate-common-registry up -d registry

docker buildx build --platform ${BUILDX_TARGET_PLATFORMS:-linux/amd64,linux/arm64} \
  -t ${VANILLA_POSTGRES_MULTI_ARCH_IMAGE:-${DOCKER_HOST_NAME:-host.docker.internal}:5002/eventuate-vanilla-postgres:multi-arch-local-build} \
  -f $SCRIPT_DIR/Dockerfile-vanilla \
  ${BUILDX_PUSH_OPTIONS:---output=type=image,push=true,registry.insecure=true} \
  $SCRIPT_DIR
