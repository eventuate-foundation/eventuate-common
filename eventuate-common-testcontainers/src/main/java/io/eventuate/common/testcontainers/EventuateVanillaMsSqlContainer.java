package io.eventuate.common.testcontainers;

import org.testcontainers.containers.wait.strategy.Wait;

public class EventuateVanillaMsSqlContainer extends AbstractEventuateMsSqlContainer<EventuateVanillaMsSqlContainer> {

    public EventuateVanillaMsSqlContainer() {
        super("mcr.microsoft.com/mssql/server:2022-latest");
        withConfiguration();
    }

    @Override
    protected void withConfiguration() {
        super.withConfiguration();
        waitingFor(Wait.forListeningPort());

    }

    public static EventuateVanillaMsSqlContainer make() {
        return new EventuateVanillaMsSqlContainer();
    }

    @Override
    public String getEventuateDatabaseSchema() {
        return "dbo";
    }

    @Override
    public String getMonitoringSchema() {
        return "dbo";
    }

}
