package io.eventuate.common.jdbc;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class OutboxPartitionValues {
    public final OutboxTableSuffix outboxTableSuffix;
    public final Integer messagePartition;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OutboxPartitionValues that = (OutboxPartitionValues) o;

        return new EqualsBuilder().append(outboxTableSuffix.suffix, that.outboxTableSuffix.suffix).append(messagePartition, that.messagePartition).isEquals();
    }

    @Override
    public String toString() {
        return "OutboxPartitionValues{" +
                "outboxTableSuffix='" + outboxTableSuffix.suffix + '\'' +
                ", messagePartition=" + messagePartition +
                '}';
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(outboxTableSuffix.suffix).append(messagePartition).toHashCode();
    }

    public OutboxPartitionValues(Integer outboxTableSuffix, Integer messagePartition) {
        this.outboxTableSuffix = new OutboxTableSuffix(outboxTableSuffix);
        this.messagePartition = messagePartition;
    }
}
