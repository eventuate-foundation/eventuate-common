package io.eventuate.common.kafka.consumer;

public class KafkaMessageProcessorFailedException extends RuntimeException {
  public KafkaMessageProcessorFailedException(Throwable t) {
    super(t);
  }
}
