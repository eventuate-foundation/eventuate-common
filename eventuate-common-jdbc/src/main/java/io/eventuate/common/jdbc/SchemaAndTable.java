package io.eventuate.common.jdbc;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

public class SchemaAndTable {
  private String schema;
  private String tableName;

  public SchemaAndTable(String schema, String tableName) {
    this.schema = schema;
    this.tableName = tableName.toLowerCase();
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  public String getSchema() {
    return schema;
  }

  public String getTableName() {
    return tableName;
  }

  @Override
  public int hashCode() {
    return Objects.hash(schema, tableName);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }
}
