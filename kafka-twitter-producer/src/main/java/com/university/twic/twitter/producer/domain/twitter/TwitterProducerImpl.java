package com.university.twic.twitter.producer.domain.twitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TwitterProducerImpl implements TwitterProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final NewTopic topic;

  @Override
  public void send(String tweet) {
    String topicName = topic.name();
    logger.info("sending tweet={} to topic={}", tweet, topicName);
    kafkaTemplate.send(topicName, tweet);
  }
}
