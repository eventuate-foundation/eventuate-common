#! /bin/bash -e

if [[ -f /docker-entrypoint-initdb.d/initialization-completed ]] ; then
  nc -zv localhost 3306
  exit 0
fi

exit 1
