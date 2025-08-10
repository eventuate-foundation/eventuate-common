package io.eventuate.common.jdbc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EventuateSchemaTest {

  @Test
  public void testDefaultConstructor() {
    EventuateSchema eventuateSchema = new EventuateSchema();

    Assertions.assertTrue(eventuateSchema.isDefault());
    Assertions.assertFalse(eventuateSchema.isEmpty());
    Assertions.assertEquals(EventuateSchema.DEFAULT_SCHEMA, eventuateSchema.getEventuateDatabaseSchema());
    Assertions.assertEquals(EventuateSchema.DEFAULT_SCHEMA + ".test", eventuateSchema.qualifyTable("test"));
  }

  @Test
  public void testDefaultSchema() {
    EventuateSchema eventuateSchema = new EventuateSchema(EventuateSchema.DEFAULT_SCHEMA);

    Assertions.assertTrue(eventuateSchema.isDefault());
    Assertions.assertFalse(eventuateSchema.isEmpty());
    Assertions.assertEquals(EventuateSchema.DEFAULT_SCHEMA, eventuateSchema.getEventuateDatabaseSchema());
    Assertions.assertEquals(EventuateSchema.DEFAULT_SCHEMA + ".test", eventuateSchema.qualifyTable("test"));
  }

  @Test
  public void testEmptySchema() {
    EventuateSchema eventuateSchema = new EventuateSchema(EventuateSchema.EMPTY_SCHEMA);

    Assertions.assertFalse(eventuateSchema.isDefault());
    Assertions.assertTrue(eventuateSchema.isEmpty());
    Assertions.assertEquals(EventuateSchema.EMPTY_SCHEMA, eventuateSchema.getEventuateDatabaseSchema());
    Assertions.assertEquals("test", eventuateSchema.qualifyTable("test"));
  }

  @Test
  public void testCustomSchema() {
    EventuateSchema eventuateSchema = new EventuateSchema("custom");

    Assertions.assertFalse(eventuateSchema.isDefault());
    Assertions.assertFalse(eventuateSchema.isEmpty());
    Assertions.assertEquals("custom", eventuateSchema.getEventuateDatabaseSchema());
    Assertions.assertEquals("custom.test", eventuateSchema.qualifyTable("test"));
  }
}
