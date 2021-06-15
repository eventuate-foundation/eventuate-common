package io.eventuate.common.spring.jdbc.reactive;

import io.eventuate.common.reactive.jdbc.EventuateCommonReactiveJdbcOperations;
import io.eventuate.common.spring.jdbc.EventuateSpringTransactionTemplate;
import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.jdbc.EventuateDuplicateKeyException;
import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.eventuate.common.jdbc.sqldialect.EventuateSqlDialect;
import io.eventuate.common.jdbc.tests.AbstractEventuateCommonJdbcOperationsTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static io.eventuate.common.jdbc.EventuateJdbcOperationsUtils.MESSAGE_APPLICATION_GENERATED_ID_COLUMN;
import static io.eventuate.common.jdbc.EventuateJdbcOperationsUtils.MESSAGE_AUTO_GENERATED_ID_COLUMN;

@SpringBootTest(classes = EventuateCommonReactiveJdbcOperationsTest.Config.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class EventuateCommonReactiveJdbcOperationsTest extends AbstractEventuateCommonJdbcOperationsTest {

  @Configuration
  @EnableAutoConfiguration
  @Import(EventuateCommonReactiveDatabaseConfiguration.class)
  public static class Config {
    @Bean
    public EventuateTransactionTemplate eventuateTransactionTemplate(TransactionTemplate transactionTemplate) {
      return new EventuateSpringTransactionTemplate(transactionTemplate);
    }
  }

  @Autowired
  private EventuateSpringReactiveJdbcStatementExecutor eventuateSpringReactiveJdbcStatementExecutor;

  @Autowired
  private EventuateCommonReactiveJdbcOperations eventuateCommonReactiveJdbcOperations;

  @Autowired
  private EventuateTransactionTemplate eventuateTransactionTemplate;

  @Autowired
  private DataSource dataSource;

  @Autowired
  private IdGenerator idGenerator;

  @Autowired
  private TransactionalOperator transactionalOperator;

  @Test
  public void testTransactionRollbackOnException() {
    String payload1 = generateId();
    String dest1 = generateId();

    AtomicReference<IdColumnAndValue> idAndColumn = new AtomicReference<>();

    try {
      insertIntoMessageTableNoBlock(payload1, dest1, Collections.emptyMap())
              .map(idColumnAndValue -> {
                idAndColumn.set(idColumnAndValue);
                throw new TransactionRollbackCheckException();
              })
              .as(transactionalOperator::transactional)
              .block(Duration.ofSeconds(30));
    } catch (TransactionRollbackCheckException e) {
      //does not need special actions
    }

    Assert.assertTrue(getMessages(idAndColumn.get()).isEmpty());
  }

  @Override
  @Test(expected = EventuateDuplicateKeyException.class)
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

  @Override
  @Test
  public void testGeneratedIdOfEventsTableRow() {
    super.testGeneratedIdOfEventsTableRow();
  }

  @Override
  @Test
  public void testGeneratedIdOfMessageTableRow() {
    super.testGeneratedIdOfMessageTableRow();
  }

  @Override
  protected DataSource getDataSource() {
    return dataSource;
  }

  @Override
  protected String insertIntoMessageTable(String payload,
                                        String destination,
                                        Map<String, String> headers) {

    Mono<IdColumnAndValue> result = insertIntoMessageTableNoBlock(payload, destination, headers);

    return result.block().getValue().toString();
  }

  protected Mono<IdColumnAndValue> insertIntoMessageTableNoBlock(String payload,
                                          String destination,
                                          Map<String, String> headers) {

    Mono<String> id = eventuateCommonReactiveJdbcOperations.insertIntoMessageTable(idGenerator,
            payload,
            destination,
            headers,
            eventuateSchema);

    return id.map(_id -> new IdColumnAndValue(idGenerator.databaseIdRequired()
            ? MESSAGE_AUTO_GENERATED_ID_COLUMN : MESSAGE_APPLICATION_GENERATED_ID_COLUMN, _id));
  }

  @Override
  protected String insertIntoEventsTable(String entityId,
                                         String eventData,
                                         String eventType,
                                         String entityType,
                                         Optional<String> triggeringEvent,
                                         Optional<String> metadata) {

    Mono<String> result = eventuateCommonReactiveJdbcOperations.insertIntoEventsTable(idGenerator,
            entityId, eventData, eventType, entityType, triggeringEvent, metadata, eventuateSchema);

    return result.block();
  }

  @Override
  protected void insertIntoEntitiesTable(String entityId, String entityType, EventuateSchema eventuateSchema) {
    String table = eventuateSchema.qualifyTable("entities");
    String sql = String.format("insert into %s values (?, ?, ?);", table);

    eventuateSpringReactiveJdbcStatementExecutor.update(sql, entityId, entityType, System.nanoTime()).block();
  }

  @Override
  protected IdGenerator getIdGenerator() {
    return idGenerator;
  }

  @Override
  protected EventuateTransactionTemplate getEventuateTransactionTemplate() {
    return eventuateTransactionTemplate;
  }

  @Override
  protected EventuateSqlDialect getEventuateSqlDialect() {
    return eventuateCommonReactiveJdbcOperations.getEventuateSqlDialect();
  }
}
