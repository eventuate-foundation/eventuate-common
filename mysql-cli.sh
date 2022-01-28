#! /bin/bash -e

NETWORK=${PWD##*/}_default

if [ -z "$DATABASE" ] ; then
  DATABASE=mysql
fi

if [ "${DATABASE}" == "mysql" ] ; then
  dockerfile=Dockerfile
else
  dockerfile=Dockerfile-$DATABASE
fi

mysqlimage=$(head -1 mysql/$dockerfile | sed -e 's/FROM *//')

docker run ${1:--it} \
   --name mysqlterm --network=$NETWORK --rm -e HOST=${DATABASE} -e MYSQL_PORT=${MYSQL_PORT:-3306} \
   ${mysqlimage} \
   sh -c 'exec mysql -h$HOST  -P"$MYSQL_PORT" -uroot -prootpassword -o eventuate'
