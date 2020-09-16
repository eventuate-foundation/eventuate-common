package io.eventuate.common.id;

public interface IdGenerator {
  Int128 genId(long databaseId);
}
