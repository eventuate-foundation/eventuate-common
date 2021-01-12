#run the setup scripts to create the DB and the schema in the DB

until (echo select 1 from dual | /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P Eventuate123! -d master > /dev/null)
do
 echo sleeping for mssql
 sleep 5
done

if [[ "${USE_JSON_PAYLOAD_AND_HEADERS}" == "true" ]]; then
  cp /usr/src/app/additional-scripts/3.setup-json.sql /usr/src/app
  echo "3.setup-json.sql is activated"
fi

if [[ "${USE_DB_ID}" == "true" ]]; then

  /usr/src/app/additional-scripts

  cp /usr/src/app/additional-scripts/4.setup-db-id.sql /usr/src/app
  echo "4.setup-db-id.sql is activated"
fi

for i in `ls *.sql | sort -V`; do
 echo ""
 echo "RUNNING $i"
 echo ""
 /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P Eventuate123! -d master -i "$i"
done;

sh /usr/src/app/additional-scripts/9.initialization-completed.sh
