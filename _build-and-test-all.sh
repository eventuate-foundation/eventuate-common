#! /bin/bash

export TERM=dumb

TEST_REGULAR=true
TEST_DBID_WITH_APPID_GEN=true
TEST_DBID_WITH_DBID_GEN=true
USE_EXISTING=

set -e

GRADLE_OPTS=""

if [ "$1" = "--clean" ] ; then
  GRADLE_OPTS="clean"
  shift
fi
if [ "$1" = "--use-existing" ] ; then
  USE_EXISTING=true
  shift
fi

function gradlew() {
  ./gradlew -P dbIdUsed=${DB_ID_USED:-false} -P useDbId=${USE_DB_ID:-false} \
  -P useJsonPayloadAndHeaders=${USE_JSON_PAYLOAD_AND_HEADERS:-false} $*
}

function dockerCompose() {
  gradlew -P removeContainers=true ${DATABASE//-}Compose${1}
}

function dockerUp() {
  echo "DOCKER UP WITH USE_DB_ID=$USE_DB_ID DB_ID_USED=$DB_ID_USED"
  dockerCompose "Up"
}
function dockerDown() {
  dockerCompose "Down"
  # > Container 2ca5624f08b1e87c83379db571567aeb31c164634fe87faa5b980f1cdeb522b0 of mysql8_1 is not running. Logs:
  sleep 5
}

function testJdbc() {
  dockerUp
  echo "RUNNING TESTS WITH USE_DB_ID=$USE_DB_ID DB_ID_USED=$DB_ID_USED"
  gradlew :eventuate-common-micronaut-data-jdbc:cleanTest :eventuate-common-micronaut-data-jdbc:test \
                     :eventuate-common-spring-jdbc:cleanTest :eventuate-common-spring-jdbc:test

  dockerDown
}

if [ -z "$USE_EXISTING" ]; then
 dockerDown
fi

./gradlew ${GRADLE_OPTS} testClasses


unset USE_DB_ID
unset DB_ID_USED

if [ ! -z "$TEST_REGULAR" ] ; then
  echo ""
  echo "TESTING REGULAR DATABASE"
  echo ""

  dockerUp
  echo "RUNNING TESTS WITH USE_DB_ID=$USE_DB_ID DB_ID_USED=$DB_ID_USED"
  gradlew cleanTest build
  dockerDown
fi

export USE_DB_ID=true
export DB_ID_USED=true

if [ ! -z "$TEST_DBID_WITH_APPID_GEN" ] ; then

echo ""
echo "TESTING DATABASE WITH DBID WITH APPLICATION ID GENERATION"
echo ""

testJdbc
fi

export EVENTUATE_OUTBOX_ID=1

if [ ! -z "$TEST_DBID_WITH_DBID_GEN" ] ; then

echo ""
echo "TESTING DATABASE WITH DBID WITH DATABASE ID GENERATION"
echo ""

testJdbc
fi

echo ""
echo "TESTING DBID MIGRATION"
echo ""

unset USE_DB_ID
dockerUp
./migration/db-id/migration.sh
testJdbc

echo ""
echo "TESTING DATABASE WITH JSON SUPPORT"
echo ""

unset DB_ID_USED
unset EVENTUATE_OUTBOX_ID
export USE_JSON_PAYLOAD_AND_HEADERS=true
testJdbc
