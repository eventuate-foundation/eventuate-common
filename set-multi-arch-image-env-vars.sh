

export MULTI_ARCH_TAG=test-build-${CIRCLE_SHA1?}
export MYSQL8_MULTI_ARCH_IMAGE=eventuateio/eventuate-mysql8:$MULTI_ARCH_TAG
export ZOOKEEPER_MULTI_ARCH_IMAGE=eventuateio/eventuate-zookeeper:$MULTI_ARCH_TAG
export BUILDX_PUSH_OPTIONS=--push
