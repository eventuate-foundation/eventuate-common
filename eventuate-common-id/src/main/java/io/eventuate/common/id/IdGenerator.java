package io.eventuate.common.id;

import java.util.Optional;

public interface IdGenerator {

  boolean databaseIdRequired();

  Int128 genId(Long databaseId);

  Optional<Int128> incrementIdIfPossible(Int128 anchorId);
}
