package io.eventuate.common.jdbc;


import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.abs;

public class OutboxPartitioningSpec {
    private final Integer outboxTables;
    private final Integer outboxTablePartitions;

    public final static OutboxPartitioningSpec DEFAULT = new OutboxPartitioningSpec(null, null);

    public OutboxPartitioningSpec(Integer outboxTables, Integer outboxTablePartitions) {
        this.outboxTables = outboxTables;
        this.outboxTablePartitions = outboxTablePartitions;
    }

    public OutboxPartitionValues outboxTableValues(String destination, String messageKey) {
        Integer hash = abs(Objects.hash(destination, messageKey));

        String outboxTableSuffix = nullOrOne(outboxTables) || messageKey == null ? "" : Integer.toString(hash % outboxTables);
        Integer messagePartition = nullOrOne(outboxTablePartitions) || messageKey == null ? null : hash % outboxTablePartitions;

        return new OutboxPartitionValues(outboxTableSuffix, messagePartition);
    }

    private boolean nullOrOne(Integer x) {
        return x == null || x == 1;
    }

    public List<String> outboxTableSuffixes() {
        if (nullOrOne(outboxTables))
            return Collections.singletonList("");
        else
            return IntStream.range(0, outboxTables).mapToObj(Integer::toString).collect(Collectors.toList());
    }
}
