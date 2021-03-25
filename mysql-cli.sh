#! /bin/bash -e

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

docker run ${1:--it} \
   --name mysqlterm --network=${PWD##*/}_default --rm \
   -e MYSQL_HOST=${DATABASE} \
   $mysqlimage \
   sh -c 'exec mysql -h"$MYSQL_HOST"  -uroot -prootpassword -o eventuate'
