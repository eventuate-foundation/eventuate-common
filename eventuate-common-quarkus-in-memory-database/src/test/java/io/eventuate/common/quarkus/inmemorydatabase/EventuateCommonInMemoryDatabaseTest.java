package io.eventuate.common.quarkus.inmemorydatabase;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.sql.DataSource;

@QuarkusTest
public class EventuateCommonInMemoryDatabaseTest {

  @Inject
  Instance<DataSource> dataSource;

  @Test
  public void testThatInMemoryDataSourceIsUsed() {
    Assertions.assertTrue(dataSource.get().getClass().getName().contains("EmbeddedDataSource"));
  }
}
