package io.eventuate.common.testcontainers;

import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

public abstract class EventuateDatabaseContainer<T extends EventuateDatabaseContainer<T>> extends EventuateGenericContainer<T> implements PropertyProvidingContainer {

    protected EventuateDatabaseContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
    }

    protected EventuateDatabaseContainer(String dockerImageName) {
        super(dockerImageName);
    }

    protected EventuateDatabaseContainer(ImageFromDockerfile withDockerfile) {
        super(withDockerfile);
    }

    public abstract DatabaseCredentials getCredentials();
    public abstract String getLocalJdbcUrl();
    public abstract String getJdbcUrl();

    public abstract String getDatabaseName();
    public abstract DatabaseCredentials getAdminCredentials();
    public abstract String getDriverClassName();
    public abstract String getEventuateDatabaseSchema();
    public abstract String getMonitoringSchema(); 
    public abstract String getCdcReaderType();
}
