#! /bin/bash -e

docker-compose -f ../docker-compose-registry.yml --project-name eventuate-common-registry up -d registry

docker buildx build --platform linux/amd64,linux/arm64 \
  -t host.docker.internal:5002/eventuate-mysql8:local-build \
  -f Dockerfile-mysql8-multi-arch \
  --output=type=image,push=true,registry.insecure=true \
  .
