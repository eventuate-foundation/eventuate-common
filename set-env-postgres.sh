. ./_set-env.sh

export SPRING_DATASOURCE_URL=jdbc:postgresql://${DOCKER_HOST_IP}:5432/eventuate
export SPRING_DATASOURCE_USERNAME=eventuate
export SPRING_DATASOURCE_PASSWORD=eventuate
export SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver

export DATASOURCE_URL=jdbc:postgresql://${DOCKER_HOST_IP}:5432/eventuate
export DATASOURCE_USERNAME=eventuate
export DATASOURCE_PASSWORD=eventuate
export DATASOURCE_DRIVERCLASSNAME=org.postgresql.Driver


