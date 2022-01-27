#! /bin/bash -e


(cd mysql ;  ./build-docker-mysql-8-multi-arch.sh )

docker-compose -f docker-compose-mysql8-multi-arch.yml pull mysql8

docker-compose -f docker-compose-mysql8-multi-arch.yml up -d mysql8

while [ true ] ; do
  H=$(docker inspect eventuate-common_mysql8_1 --format '{{.State.Health.Status}}')
  echo $H
  if [ "$H" = "healthy" ] ; then
    break
  fi
  if [ "$H" = "unhealthy" ] ; then
    docker logs eventuate-common_mysql8_1
    exit 9
  fi
  sleep 2
done

export RESULTS=$(echo 'show tables; ' | ./mysql-cli-multi-arch.sh -i)

export EXPECTED='[Entrypoint] MySQL Docker Image 8.0.27-1.2.6-server Tables_in_eventuate cdc_monitoring entities events message offset_store received_messages snapshots'


if diff <(echo $EXPECTED) <(echo $RESULTS) ; then
  echo "
  SUCCESS"
else
  echo "
  FAIL"
  exit 1
fi
