package io.eventuate.common.id;

public interface IdGenerator {

  boolean databaseIdRequired();

  Int128 genId(Long databaseId);
}
