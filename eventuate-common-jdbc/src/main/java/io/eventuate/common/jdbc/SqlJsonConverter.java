package io.eventuate.common.jdbc;

public interface SqlJsonConverter {
  String convert(EventuateSchema schema, String column);
}
