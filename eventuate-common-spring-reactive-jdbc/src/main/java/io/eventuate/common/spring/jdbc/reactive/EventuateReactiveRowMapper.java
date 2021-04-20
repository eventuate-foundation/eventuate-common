package io.eventuate.common.spring.jdbc.reactive;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

import java.util.function.BiFunction;

public interface EventuateReactiveRowMapper<T> extends BiFunction<Row, RowMetadata, T> {
}
