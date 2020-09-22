package io.eventuate.common.spring.id;

import io.eventuate.common.id.DefaultIdGenerator;
import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.id.ImprovedIdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdGeneratorConfiguration {

  @Bean
  @ConditionalOnProperty(name = "eventuatelocal.cdc.reader.id", matchIfMissing = true)
  public IdGenerator idGenerator() {
    return new DefaultIdGenerator();
  }

  @Bean
  @ConditionalOnProperty(name = "eventuatelocal.cdc.reader.id")
  public IdGenerator idGenerator(@Value("${eventuatelocal.cdc.reader.id:#{null}}") long id) {
    return new ImprovedIdGenerator(id);
  }
}