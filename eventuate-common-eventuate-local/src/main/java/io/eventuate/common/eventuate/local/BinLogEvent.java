package io.eventuate.common.eventuate.local;

import java.util.Optional;

public interface BinLogEvent {
  Optional<BinlogFileOffset> getBinlogFileOffset();
}
