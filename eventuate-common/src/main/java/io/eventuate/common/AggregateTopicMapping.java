package io.eventuate.common;

import io.eventuate.common.kafka.TopicCleaner;

public class AggregateTopicMapping {

  public static String aggregateTypeToTopic(String aggregateType) {
    return TopicCleaner.clean(aggregateType);
  }

}
