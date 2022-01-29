#! /bin/bash -e

NETWORK=${PWD##*/}_default

if [ -z "$DATABASE" ] ; then
  DATABASE=mysql
fi

if [ "${DATABASE}" == "mysql8-multi-arch" ] ; then
  mysqlimage=localhost:5002/eventuate-mysql8:multi-arch-local-build
  HOST=mysql8
else
  if [ "${DATABASE}" == "mysql" ] ; then
    dockerfile=Dockerfile
  else
    dockerfile=Dockerfile-$DATABASE
  fi

  mysqlimage=$(head -1 mysql/$dockerfile | sed -e 's/FROM *//')
  HOST=${DATABASE}
fi

docker run ${1:--it} \
   --name mysqlterm --network=$NETWORK --rm -e HOST=$HOST -e MYSQL_PORT=${MYSQL_PORT:-3306} \
   ${mysqlimage} \
   sh -c 'exec mysql -h$HOST  -P"$MYSQL_PORT" -uroot -prootpassword -o eventuate'
