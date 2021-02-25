package io.eventuate.common.spring.jdbc;

import io.eventuate.common.common.spring.jdbc.EventuateSpringTransactionTemplate;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class EventuateTransactionTemplateConfiguration {
  @Bean
  public EventuateTransactionTemplate eventuateTransactionTemplate(TransactionTemplate transactionTemplate) {
    return new EventuateSpringTransactionTemplate(transactionTemplate);
  }
}
