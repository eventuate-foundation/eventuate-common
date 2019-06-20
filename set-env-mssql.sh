. ./_set-env.sh

export DATASOURCE_URL="jdbc:sqlserver://${DOCKER_HOST_IP}:1433;databaseName=eventuate"
export DATASOURCE_USERNAME=sa
export DATASOURCE_PASSWORD=Eventuate123!
export DATASOURCE_DRIVER_CLASS_NAME=com.microsoft.sqlserver.jdbc.SQLServerDriver


