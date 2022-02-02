

export MYSQL8_MULTI_ARCH_IMAGE=eventuateio/eventuate-mysql8:test-build-${CIRCLE_SHA1?}
export ZOOKEEPER_MULTI_ARCH_IMAGE=eventuateio/eventuate-zookeeper:test-build-${CIRCLE_SHA1?}
export BUILDX_PUSH_OPTIONS=--push
