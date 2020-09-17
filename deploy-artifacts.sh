#! /bin/bash -e

BRANCH=$(git rev-parse --abbrev-ref HEAD)

VERSION=$BRANCH

function deploy() {
  image=$1

  ./gradlew ${image}ComposePull || echo no image to pull
  ./gradlew ${image}ComposeBuild ${image}ComposePush
}

export DOCKER_IMAGE_TAG=$VERSION

docker login -u ${DOCKER_USER_ID?} -p ${DOCKER_PASSWORD?}

deploy "postgres"
deploy "mysql"
deploy "mariadb"
deploy "mssql"

./gradlew  publishEventuateArtifacts
