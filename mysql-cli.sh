#! /bin/bash -e

if [ -z "$MYSQL_PORT" ]; then
    export MYSQL_PORT=3306
fi

if [ -z "$DATABASE" ] ; then
  export DATABASE=mysql
fi

if [ "${DATABASE}" == "mysql" ]; then
  export mysqlimage="mysql:5.7.13"
elif [ "${DATABASE}" == "mariadb" ]; then
  export mysqlimage="mariadb:10.3.8"
elif [ "${DATABASE}" == "mysql8" ]; then
  export mysqlimage="mysql:8.0.22"
fi

docker run $* \
   --name mysqlterm --network=${PWD##*/}_default --rm \
   -e MYSQL_PORT_3306_TCP_ADDR=${DATABASE} -e MYSQL_PORT_3306_TCP_PORT=$MYSQL_PORT -e MYSQL_ENV_MYSQL_ROOT_PASSWORD=rootpassword \
   ${mysqlimage}  \
   sh -c 'exec mysql -h"$MYSQL_PORT_3306_TCP_ADDR" -P"$MYSQL_PORT_3306_TCP_PORT" -uroot -p"$MYSQL_ENV_MYSQL_ROOT_PASSWORD" -o eventuate'