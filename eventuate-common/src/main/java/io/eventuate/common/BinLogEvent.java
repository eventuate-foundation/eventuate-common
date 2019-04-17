package io.eventuate.common;

import java.util.Optional;

public interface BinLogEvent {
  Optional<BinlogFileOffset> getBinlogFileOffset();
}
