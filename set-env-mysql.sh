. ./_set-env.sh

export SPRING_DATASOURCE_URL=jdbc:mysql://${DOCKER_HOST_IP}/eventuate
export SPRING_DATASOURCE_USERNAME=mysqluser
export SPRING_DATASOURCE_PASSWORD=mysqlpw
export SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.jdbc.Driver

export DATASOURCES_DEFAULT_URL=jdbc:mysql://${DOCKER_HOST_IP}/eventuate
export DATASOURCES_DEFAULT_USERNAME=mysqluser
export DATASOURCES_DEFAULT_PASSWORD=mysqlpw
export DATASOURCES_DEFAULT_DRIVERCLASSNAME=com.mysql.jdbc.Driver