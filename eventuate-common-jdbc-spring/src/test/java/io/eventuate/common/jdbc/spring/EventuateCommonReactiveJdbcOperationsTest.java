package io.eventuate.common.jdbc.spring;

import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.eventuate.common.jdbc.spring.common.EventuateCommonReactiveJdbcOperations;
import io.eventuate.common.jdbc.tests.AbstractEventuateCommonJdbcOperationsTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.publisher.Mono;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

@SpringBootTest(classes = EventuateCommonReactiveJdbcOperationsTest.Config.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class EventuateCommonReactiveJdbcOperationsTest extends AbstractEventuateCommonJdbcOperationsTest {

  @Configuration
  @EnableAutoConfiguration
  @Import(EventuateCommonReactiveJdbcOperationsConfiguration.class)
  public static class Config {
  }

  @Autowired
  private EventuateCommonReactiveJdbcOperations eventuateCommonReactiveJdbcOperations;

  @Autowired
  private EventuateTransactionTemplate eventuateTransactionTemplate;

  @Autowired
  private DataSource dataSource;

  @Override
  protected DataSource getDataSource() {
    return dataSource;
  }

  @Override
  protected void insertIntoMessageTable(String messageId, String payload, String destination, String currentTimeInMillisecondsSql, Map<String, String> headers, EventuateSchema eventuateSchema) {
    Mono<Integer> result = eventuateTransactionTemplate.executeInTransaction(() ->
      eventuateCommonReactiveJdbcOperations.insertIntoMessageTable(messageId,
              payload,
              destination,
              currentTimeInMillisecondsSql,
              headers,
              eventuateSchema)
    );

    Assert.assertEquals(1, (int) result.block());
  }

  @Override
  protected void insertIntoEventsTable(String eventId, String entityId, String eventData, String eventType, String entityType, Optional<String> triggeringEvent, Optional<String> metadata, EventuateSchema eventuateSchema) {
    Mono<Integer> result = eventuateTransactionTemplate.executeInTransaction(() ->
      eventuateCommonReactiveJdbcOperations.insertIntoEventsTable(eventId,
              entityId, eventData, eventType, entityType, triggeringEvent, metadata, eventuateSchema));

    Assert.assertEquals(1, (int) result.block());
  }

  @Override
  @Test(expected = DuplicateKeyException.class)
  public void testEventuateDuplicateKeyException() {
    super.testEventuateDuplicateKeyException();
  }

  @Override
  @Test
  public void testInsertIntoEventsTable() throws SQLException {
    super.testInsertIntoEventsTable();
  }

  @Override
  @Test
  public void testInsertIntoMessageTable() throws SQLException {
    super.testInsertIntoMessageTable();
  }
}
