package io.eventuate.common.inmemorydatabase;

import java.util.List;
import java.util.function.Supplier;

public interface EventuateDatabaseScriptSupplier extends Supplier<List<String>> {
}
