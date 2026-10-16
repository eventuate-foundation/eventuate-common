#! /bin/bash -e

# Builds the image for the Docker host's platform and pushes it with a per-platform tag

SCRIPT_DIR=$(cd $( dirname "${BASH_SOURCE[0]}" ) ; pwd)

ARCH=$(docker version --format '{{.Server.Arch}}')
IMAGE=${ZOOKEEPER_MULTI_ARCH_IMAGE?}-$ARCH

docker build -t $IMAGE -f $SCRIPT_DIR/Dockerfile $SCRIPT_DIR
docker push $IMAGE
