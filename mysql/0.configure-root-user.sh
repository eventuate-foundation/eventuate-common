#! /bin/bash

if [ -z "$MYSQL_ROOT_HOST" ] ; then
  MYSQL_ROOT_HOST=%
  echo defaulting MYSQL_ROOT_HOST=$MYSQL_ROOT_HOST
fi

cat >> /docker-entrypoint-initdb.d/5.configure-users.sql <<END
CREATE USER 'root'@'${MYSQL_ROOT_HOST}' IDENTIFIED BY '${MYSQL_ROOT_PASSWORD}';
GRANT ALL ON *.* TO 'root'@'${MYSQL_ROOT_HOST}' WITH GRANT OPTION ;
GRANT PROXY ON ''@'' TO 'root'@'${MYSQL_ROOT_HOST}' WITH GRANT OPTION ;
END

echo start /docker-entrypoint-initdb.d/5.configure-users.sql
cat /docker-entrypoint-initdb.d/5.configure-users.sql
echo end echo start /docker-entrypoint-initdb.d/5.configure-users.sql
