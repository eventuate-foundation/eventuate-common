package io.eventuate.common.testcontainers;

import org.springframework.test.context.DynamicPropertyRegistry;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class AliasingDynamicPropertyRegistry implements DynamicPropertyRegistry {

    private final DynamicPropertyRegistry registry;
    private final List<Builder.PropertyAlias> aliases;

    public AliasingDynamicPropertyRegistry(DynamicPropertyRegistry registry, List<Builder.PropertyAlias> aliases) {
        this.registry = registry;
        this.aliases = aliases;
    }

    @Override
    public void add(String name, Supplier<Object> valueSupplier) {
        registry.add(name, valueSupplier);
        aliases.stream()
                .filter(alias -> alias.matches(name))
                .forEach(alias -> registry.add(alias.getAlias(), () -> alias.getValue((String)valueSupplier.get())));

    }

    public static Builder forRegistry(DynamicPropertyRegistry registry) {
        return new Builder(registry);
    }


    public static class Builder {
        private DynamicPropertyRegistry registry;

        static interface PropertyAlias{
            boolean matches(String name);
            String getAlias();
            Object getValue(Object originalValue);
        }

        static class SimplePropertyAlias implements PropertyAlias {

            private final String original;
            private final String alias;

            public SimplePropertyAlias(String original, String alias) {
                this.original = original;
                this.alias = alias;
            }

            @Override
            public boolean matches(String name) {
                return original.equals(name);
            }

            @Override
            public String getAlias() {
                return alias;
            }

            @Override
            public Object getValue(Object originalValue) {
                return originalValue;
            }
        }

        private List<PropertyAlias> aliases = new LinkedList<>();

        public Builder(DynamicPropertyRegistry registry) {
            this.registry = registry;
        }

        public Builder withAlias(String original, String alias) {
            aliases.add(new SimplePropertyAlias(original, alias));
            return this;
        }

        public Builder withAlias(String original, String alias, Function<Object, Object> transformer) {
            aliases.add(new TransformingPropertyAlias(original, alias, transformer));
            return this;
        }

        private static class TransformingPropertyAlias implements PropertyAlias {
            private final String original;
            private final String alias;
            private final Function<Object, Object> transformer;

            public TransformingPropertyAlias(String original, String alias, Function<Object, Object> transformer) {
                this.original = original;
                this.alias = alias;
                this.transformer = transformer;
            }

            @Override
            public boolean matches(String name) {
                return original.equals(name);
            }

            @Override
            public String getAlias() {
                return alias;
            }

            @Override
            public Object getValue(Object originalValue) {
                return transformer.apply(originalValue);
            }

        }

        public DynamicPropertyRegistry build() {
            return new AliasingDynamicPropertyRegistry(registry, aliases);
        }
    }
}
