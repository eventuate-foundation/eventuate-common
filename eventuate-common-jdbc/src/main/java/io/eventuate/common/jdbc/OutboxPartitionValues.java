package io.eventuate.common.jdbc;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class OutboxPartitionValues {
    public final String outboxTableSuffix;
    public final Integer messagePartition;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OutboxPartitionValues that = (OutboxPartitionValues) o;

        return new EqualsBuilder().append(outboxTableSuffix, that.outboxTableSuffix).append(messagePartition, that.messagePartition).isEquals();
    }

    @Override
    public String toString() {
        return "OutboxPartitionValues{" +
                "outboxTableSuffix='" + outboxTableSuffix + '\'' +
                ", messagePartition=" + messagePartition +
                '}';
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(outboxTableSuffix).append(messagePartition).toHashCode();
    }

    public OutboxPartitionValues(String outboxTableSuffix, Integer messagePartition) {
        this.outboxTableSuffix = outboxTableSuffix;
        this.messagePartition = messagePartition;
    }
}
