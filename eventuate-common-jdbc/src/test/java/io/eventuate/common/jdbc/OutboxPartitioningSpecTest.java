package io.eventuate.common.jdbc;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

public class OutboxPartitioningSpecTest {

    private final String destination = "Order";
    private final String messageKey = "1";


    @Test
    public void shouldCalculateDefault() {
        assertEquals(new OutboxPartitionValues("", null), values(OutboxPartitioningSpec.DEFAULT));
    }

    @Test
    public void shouldCalculateOneOutboxTable() {
        assertEquals(new OutboxPartitionValues("", null), values(new OutboxPartitioningSpec(1, null)));
    }

    @Test
    public void shouldCalculateOneMessagePartition() {
        assertEquals(new OutboxPartitionValues("", null), values(new OutboxPartitioningSpec(null, 1)));
    }

    @Test
    public void shouldCalculateBothOne() {
        assertEquals(new OutboxPartitionValues("", null), values(new OutboxPartitioningSpec(1, 1)));
    }

    @Test
    public void shouldCalculateWithMultipleOutboxTables() {
        OutboxPartitioningSpec spec = new OutboxPartitioningSpec(2, null);
        assertOneOfExpectedValues(spec, Arrays.asList("0", "1"), null);
    }

    @Test
    public void shouldCalculateWithMessagePartition() {
        OutboxPartitioningSpec spec = new OutboxPartitioningSpec(null, 2);
        assertOneOfExpectedValues(spec, Arrays.asList(""), Arrays.asList(0, 1));
    }

    @Test
    public void shouldCalculateWithBoth() {
        OutboxPartitioningSpec spec = new OutboxPartitioningSpec(2, 2);
        assertEquals(new OutboxPartitionValues("0", 0), values(spec));
        assertOneOfExpectedValues(spec, Arrays.asList("0", "1"), Arrays.asList(0, 1));
    }

    private OutboxPartitionValues values(OutboxPartitioningSpec spec) {
        return spec.outboxTableValues(destination, messageKey);
    }

    private OutboxPartitionValues values(OutboxPartitioningSpec spec, int i) {
        return spec.outboxTableValues(destination, Integer.toString(i));
    }

    private void assertOneOfExpectedValues(OutboxPartitioningSpec spec, List<String> expectedSuffixes, List<Integer> expectedPartitions) {
        IntStream.range(0, 100).forEach(i -> {
            OutboxPartitionValues actual = values(spec, i);
            assertThat(actual.outboxTableSuffix).isIn(expectedSuffixes);

            if (expectedPartitions == null)
                assertThat(actual.messagePartition).isNull();
            else
                assertThat(actual.messagePartition).isIn(expectedPartitions);
        });
    }

    @Test
    public void shouldReturnDefaultSuffix() {
        assertEquals(singletonList(""), OutboxPartitioningSpec.DEFAULT.outboxTableSuffixes());
    }

    @Test
    public void shouldReturnSuffixesForMultipleOutboxTables() {
        assertEquals(Arrays.asList("0", "1"), new OutboxPartitioningSpec(2, 2).outboxTableSuffixes());
    }
}