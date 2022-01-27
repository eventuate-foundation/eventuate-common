#! /bin/bash -e

NETWORK=${PWD##*/}_default

docker run ${1:--it} \
   --name mysqlterm --network=$NETWORK --rm \
   mysql/mysql-server:8.0.27-1.2.6-server \
   sh -c 'exec mysql -hmysql8  -uroot -prootpassword -o eventuate'
