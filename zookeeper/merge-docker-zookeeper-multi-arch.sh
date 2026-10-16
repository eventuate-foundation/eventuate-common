#! /bin/bash -e

# Combines the per-platform images pushed by build-docker-zookeeper-native.sh into one multi-platform image

docker buildx imagetools create -t ${ZOOKEEPER_MULTI_ARCH_IMAGE?} \
  ${ZOOKEEPER_MULTI_ARCH_IMAGE?}-amd64 \
  ${ZOOKEEPER_MULTI_ARCH_IMAGE?}-arm64
