package io.eventuate.common.spring.jdbc.reactive;

import io.eventuate.common.jdbc.EventuateDuplicateKeyException;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static io.eventuate.common.reactive.jdbc.EventuateReactiveDatabases.MYSQL;
import static io.eventuate.common.reactive.jdbc.EventuateReactiveDatabases.POSTGRES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SpringBootTest(classes = EventuateCommonReactiveDatabaseConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class EventuateCommonReactiveJdbcStatementExecutorTest {
  @Autowired
  private EventuateSpringReactiveJdbcStatementExecutor executor;

  @Value("${eventuate.reactive.db.driver}")
  public String driver;

  @Test
  public void testInsert() {
    String[] columns = {"a", "b", "c", "d"};
    String[] values = {"a", "b", "c", "d"};

    createTestTable(columns);

    Long id = insert("insert into eventuate.order_test (a, b, c, d) values('a', 'b', 'c', 'd')");

    assertColumnValuesEquals(id, columns, values, query(id));
  }


  @Test
  public void testInsertWithNull() {
    String[] columns = {"a", "b", "c", "d"};
    Object[] values = {"a", null, "c", "d"};

    createTestTable(columns);

    Long id = insert("insert into eventuate.order_test (a, b, c, d) values(?, ?, ?, ?)", values);

    assertColumnValuesEquals(id, columns, values, query(id));
  }


  @Test
  public void testInsertSingleNullColumn() {
    String[] columns = {"a"};
    Object[] values = {null};

    createTestTable(columns);

    Long id = insert("insert into eventuate.order_test (a) values(?)", values);

    assertColumnValuesEquals(id, columns, values, query(id));
  }

  @Test
  public void testInsertDuplicate() {
    String[] columns = {"a"};

    createTestTable(columns);

    Long id = insert("insert into eventuate.order_test (a) values('a')");

    try {
      insert("insert into eventuate.order_test (id, a) values(?, 'a')", id);
      fail("expected EventuateDuplicateKeyException");
    } catch (EventuateDuplicateKeyException e) {
      // expected
    }

  }


  private void createTestTable(String... dataColumns) {
    executor.update("drop table if exists eventuate.order_test").block();

    StringBuffer sb = new StringBuffer("create table eventuate.order_test(id ");
    sb.append(idColumnType());
    sb.append(" PRIMARY KEY");

    for (String dataColumn : dataColumns) {
      sb.append(", ");
      sb.append(dataColumn);
      sb.append(" VARCHAR(10)");
    }
    sb.append(")");

    executor.update(sb.toString()).block();

  }

  @Nullable
  private Long insert(String sql, Object... args) {

    return executor
            .insertAndReturnId(sql, "id", args)
            .block();
  }

  @Nullable
  private Map<String, Object> query(Long id) {
    return executor.query("select * from eventuate.order_test where id = ?", id).blockFirst();
  }

  private void assertColumnValuesEquals(Long id, String[] columns, Object[] values, Map<String, Object> row) {
    assertEquals(id, row.get("id"));
    for (int i = 0; i < columns.length; i++) {
      assertEquals(values[i], row.get(columns[i]));
    }
  }

  private String idColumnType() {
    switch (driver)
    {
      case POSTGRES : return "BIGSERIAL";
      case MYSQL:
      default:
        return "BIGINT AUTO_INCREMENT";
    }
  }
}
