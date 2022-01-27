#! /bin/bash

# https://support.circleci.com/hc/en-us/articles/360058095471-How-To-Use-Docker-Buildx-in-Remote-Docker-
docker version
docker buildx
#docker manifest list
# error: could not create a builder instance with TLS data loaded from environment. Please use `docker context create <context-name>` to create a context for current environment and then create a builder instance with `docker buildx create <context-name>`
docker context ls
docker context create tls-env
docker buildx create tls-env --use
exit 0
# https://circleci.com/blog/building-docker-images-for-multiple-os-architectures/
BUILDX_BINARY_URL="https://github.com/docker/buildx/releases/download/v0.7.1/buildx-v0.7.1.linux-amd64"
curl --output docker-buildx \
  --silent --show-error --location --fail --retry 3 \
  "$BUILDX_BINARY_URL"
mkdir -p ~/.docker/cli-plugins
mv docker-buildx ~/.docker/cli-plugins/
chmod a+x ~/.docker/cli-plugins/docker-buildx
docker buildx install
# Run binfmt
docker run --rm --privileged tonistiigi/binfmt:latest --install "$BUILDX_PLATFORMS"
# Need this to prevent: error: multiple platforms feature is currently not supported for docker driver. Please switch to a different driver (eg. "docker buildx create --use")
docker buildx create --use

./build-and-test-all-mysql8-multi-arch.sh
