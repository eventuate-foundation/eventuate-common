package io.eventuate.common.jdbc;

import java.util.Objects;

public class OutboxTableSuffix {
    public final Integer suffix;
    public final String suffixAsString;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OutboxTableSuffix that = (OutboxTableSuffix) o;
        return Objects.equals(suffix, that.suffix) && suffixAsString.equals(that.suffixAsString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(suffix, suffixAsString);
    }

    public OutboxTableSuffix(Integer suffix) {
        this.suffix = suffix;
        this.suffixAsString = suffix == null ? "" : this.suffix.toString();

    }
}