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

    @Test
    public void typeSignaturesShouldSupportChaining() {
        EventuateDatabaseContainer<?> db1 = DatabaseContainerFactory.makeDatabaseContainer().withEnv("X", "Y").withEnv("X", "Z");
        EventuateDatabaseContainer<?> db2 = DatabaseContainerFactory.makeVanillaDatabaseContainer().withEnv("X", "Y").withEnv("X", "Z");
    }



}