package io.eventuate.common.reactive.jdbc;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

import java.util.function.BiFunction;

public interface EventuateReactiveRowMapper<T> extends BiFunction<Row, RowMetadata, T> {
}
