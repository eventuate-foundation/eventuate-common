#! /bin/bash -e

if [ -z "$POSTGRES_PORT" ]; then
    export POSTGRES_PORT=5432
fi

if [ -z "$DATABASE" ]; then
    export DATABASE=postgres
fi

docker run $* \
   --name postgresterm --network=${PWD##*/}_default --rm \
   -e POSTGRES_PORT=$POSTGRES_PORT -e PGPASSWORD=eventuate -e POSTGRES_HOST=${DATABASE/-multi-arch/} \
   postgres:postgres:12 \
   sh -c 'exec psql -p $POSTGRES_PORT -h $POSTGRES_HOST -U eventuate'