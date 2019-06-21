. ./_set-env.sh

export SPRING_DATASOURCE_URL=jdbc:postgresql://${DOCKER_HOST_IP}/eventuate
export SPRING_DATASOURCE_USERNAME=eventuate
export SPRING_DATASOURCE_PASSWORD=eventuate
export SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver

export DATASOURCES_DEFAULT_URL=jdbc:postgresql://${DOCKER_HOST_IP}/eventuate
export DATASOURCES_DEFAULT_USERNAME=eventuate
export DATASOURCES_DEFAULT_PASSWORD=eventuate
export DATASOURCES_DEFAULT_DRIVERCLASSNAME=org.postgresql.Driver


