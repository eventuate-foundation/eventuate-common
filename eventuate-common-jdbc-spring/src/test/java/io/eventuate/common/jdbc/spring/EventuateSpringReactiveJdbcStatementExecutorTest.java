package io.eventuate.common.jdbc.spring;

import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.eventuate.common.jdbc.spring.common.EventuateSpringReactiveJdbcStatementExecutor;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@SpringBootTest(classes = EventuateSpringReactiveJdbcStatementExecutorTest.Config.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class EventuateSpringReactiveJdbcStatementExecutorTest {

  @Configuration
  @EnableAutoConfiguration
  @Import(EventuateCommonReactiveJdbcOperationsConfiguration.class)
  public static class Config {
  }

  public static class TestEntity {
    private int id;
    private String data;

    public TestEntity() {
    }

    public TestEntity(int id, String data) {
      this.id = id;
      this.data = data;
    }

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getData() {
      return data;
    }

    public void setData(String data) {
      this.data = data;
    }

    @Override
    public boolean equals(Object o) {
      return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, data);
    }
  }

  @Autowired
  private EventuateSpringReactiveJdbcStatementExecutor eventuateSpringReactiveJdbcStatementExecutor;

  @Autowired
  private EventuateTransactionTemplate eventuateTransactionTemplate;

  private static final int records = 10;
  private static final int recordsForQuery = 5;
  private String table;

  @Before
  public void init() {
    table = "test" + System.nanoTime();

    eventuateTransactionTemplate.executeInTransaction(() -> {
      eventuateSpringReactiveJdbcStatementExecutor
              .update(String.format("create table %s (id int primary key, data varchar(1000))", table))
              .block();

      for (int i = 1; i <= records; i++) {
        eventuateSpringReactiveJdbcStatementExecutor.update(String.format("insert into %s (id, data) values (?, ?)", table), i, String.valueOf(i)).block();
      }

      return null;
    });
  }

  @Test
  public void testInsertAndQuery() {
    List<Map<String, Object>> result = eventuateTransactionTemplate.executeInTransaction(() -> {
      return eventuateSpringReactiveJdbcStatementExecutor.query(String.format("select * from %s where id > ? order by id", table), recordsForQuery).collectList().block();
    });

    Assert.assertEquals(recordsForQuery, result.size());

    for (int i = 1; i <= recordsForQuery; i++) {
      Assert.assertEquals(result.get(i - 1).get("id"), i + recordsForQuery);
      Assert.assertEquals(result.get(i - 1).get("data"), String.valueOf(i + recordsForQuery));
    }
  }

  @Test
  public void testInsertAndQueryForList() {
    List<TestEntity> result = eventuateTransactionTemplate.executeInTransaction(() -> {
      return eventuateSpringReactiveJdbcStatementExecutor.queryForList(String.format("select * from %s where id > ? order by id", table),
              (row, rowMetadata) -> new TestEntity((Integer) row.get("id"), (String)row.get("data")),
              recordsForQuery).collectList().block();
    });

    Assert.assertEquals(recordsForQuery, result.size());

    for (int i = 1; i <= recordsForQuery; i++) {
      Assert.assertEquals(result.get(i - 1).getId(), i + recordsForQuery);
      Assert.assertEquals(result.get(i - 1).getData(), String.valueOf(i + recordsForQuery));
    }
  }
}
