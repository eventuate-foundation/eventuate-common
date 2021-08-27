package io.eventuate.common.spring.jdbc.reactive;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

@SpringBootTest(classes = EventuateCommonReactiveDatabaseConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class EventuateCommonReactiveJdbcStatementExecutorTest {
  @Autowired
  private EventuateSpringReactiveJdbcStatementExecutor eventuateSpringReactiveJdbcStatementExecutor;

  @Test
  public void testInsert() {
    eventuateSpringReactiveJdbcStatementExecutor.update("drop table if exists eventuate.order_test").block();

    eventuateSpringReactiveJdbcStatementExecutor.update(
            "create table eventuate.order_test(id BIGINT AUTO_INCREMENT PRIMARY KEY, a VARCHAR(10), b VARCHAR(10), c VARCHAR(10), d VARCHAR(10))").block();

    Long id = eventuateSpringReactiveJdbcStatementExecutor
            .insertAndReturnId("insert into eventuate.order_test (a, b, c, d) values('a', 'b', 'c', 'd')", "id")
            .block();

    Map<String, Object> row = eventuateSpringReactiveJdbcStatementExecutor.query("select * from eventuate.order_test where id = ?", id).blockFirst();

    Assert.assertEquals(id, row.get("id"));
    Assert.assertEquals("a", row.get("a"));
    Assert.assertEquals("b", row.get("b"));
    Assert.assertEquals("c", row.get("c"));
    Assert.assertEquals("d", row.get("d"));
  }

  @Test
  public void testInsertNull() {
    eventuateSpringReactiveJdbcStatementExecutor.update("drop table if exists eventuate.order_test").block();

    eventuateSpringReactiveJdbcStatementExecutor.update(
            "create table eventuate.order_test(id BIGINT AUTO_INCREMENT PRIMARY KEY, a VARCHAR(10))").block();

    Long id = eventuateSpringReactiveJdbcStatementExecutor
            .insertAndReturnId("insert into eventuate.order_test (a) values(?)", "id", (Object)null)
            .block();

    Map<String, Object> row = eventuateSpringReactiveJdbcStatementExecutor.query("select * from eventuate.order_test where id = ?", id).blockFirst();

    Assert.assertEquals(id, row.get("id"));
    Assert.assertNull(row.get("a"));
  }
}
