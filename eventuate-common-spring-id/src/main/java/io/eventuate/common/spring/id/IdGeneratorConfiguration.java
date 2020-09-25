package io.eventuate.common.spring.id;

import io.eventuate.common.id.ApplicationIdGenerator;
import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.id.DatabaseIdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdGeneratorConfiguration {

  @Bean
  @ConditionalOnProperty(name = "eventuate.outbox.id", matchIfMissing = true)
  public IdGenerator idGenerator() {
    return new ApplicationIdGenerator();
  }

  @Bean
  @ConditionalOnProperty(name = "eventuate.outbox.id")
  public IdGenerator idGenerator(@Value("${eventuate.outbox.id:#{null}}") long id) {
    return new DatabaseIdGenerator(id);
  }
}