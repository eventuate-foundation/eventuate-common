. ./_set-env.sh

export DATASOURCE_URL=jdbc:mysql://${DOCKER_HOST_IP}/eventuate
export DATASOURCE_USERNAME=mysqluser
export DATASOURCE_PASSWORD=mysqlpw
export DATASOURCE_DRIVER_CLASS_NAME=com.mysql.jdbc.Driver
