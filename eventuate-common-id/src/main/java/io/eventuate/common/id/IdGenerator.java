package io.eventuate.common.id;

import java.util.Optional;

public interface IdGenerator {

  boolean databaseIdRequired();

  Int128 genId(Long databaseId, Integer partitionOffset);

  default String genIdAsString(Long databaseId, Integer partitionOffset) {
    return genId(databaseId, partitionOffset).asString();
  }

  default Int128 genId() {
    return genId(null, null);
  }

  default String genIdAsString() {
    return genId().asString();
  }

  Optional<Int128> incrementIdIfPossible(Int128 anchorId);
}
