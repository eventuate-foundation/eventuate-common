package io.eventuate.common.testcontainers;

import org.junit.Test;

import static io.eventuate.common.testcontainers.DatabaseContainerTypeRegistry.findDatabaseContainerType;
import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseContainerTypeRegistryTest {

    @Test
    public void testFindDatabaseContainerType() {
        assertThat(findDatabaseContainerType(null, null))
            .isInstanceOf(MySqlDatabaseType.class);
    }

    @Test
    public void shouldFindPostgres() {
        assertThat(findDatabaseContainerType("postgres", null))
            .isInstanceOf(PostgresDatabaseType.class);
    }

    @Test
    public void shouldFindSqlServer() {
        assertThat(findDatabaseContainerType(null, "mssql"))
            .isInstanceOf(MsSqlDatabaseType.class);
    }

}