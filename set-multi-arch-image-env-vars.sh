

export MULTI_ARCH_TAG=test-build-${CIRCLE_SHA1?}
export MYSQL8_MULTI_ARCH_IMAGE=eventuateio/eventuate-mysql8:$MULTI_ARCH_TAG
export POSTGRES_MULTI_ARCH_IMAGE=eventuateio/eventuate-postgres:$MULTI_ARCH_TAG
export ZOOKEEPER_MULTI_ARCH_IMAGE=eventuateio/eventuate-zookeeper:$MULTI_ARCH_TAG
export VANILLA_MYSQL8_MULTI_ARCH_IMAGE=eventuateio/eventuate-vanilla-mysql8:$MULTI_ARCH_TAG
export VANILLA_POSTGRES_MULTI_ARCH_IMAGE=eventuateio/eventuate-vanilla-postgres:$MULTI_ARCH_TAG

export BUILDX_PUSH_OPTIONS=--push
