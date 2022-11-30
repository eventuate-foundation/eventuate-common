package io.eventuate.common.testcontainers;

import org.junit.ClassRule;
import org.junit.Test;

public class DatabaseContainerFactoryTest {

    @ClassRule
    public static EventuateDatabaseContainer vanillaPostgres = DatabaseContainerFactory.makeVanillaPostgresContainerFromDockerfile();

    @ClassRule
    public static EventuateDatabaseContainer vanillaMySql = DatabaseContainerFactory.makeVanillaMySqlContainerFromDockerfile();

    @Test
    public void databasesShouldBecomeHealthy() {

    }



}