package com.university.twic.tweets.processing.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

  @Value("${kafka.bootstrapAddress}")
  private String bootstrapAddress;

  @Value("${kafka.topic.users}")
  private String twitterTopic;

  @Value("${kafka.topic.intermediary}")
  private String intermediaryTopic;

  @Bean
  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    return new KafkaAdmin(configs);
  }

  @Bean(name = "twitter-users")
  public NewTopic twitterTopic() {
    return TopicBuilder.name(twitterTopic)
        .partitions(3)
        .replicas(1)
//        .compact() //TODO: check
        .build();
  }

  @Bean(name = "twitter-users-intermediary")
  public NewTopic intermediaryTopic() {
    return TopicBuilder.name(intermediaryTopic)
        .partitions(1)
        .replicas(1)
//        .compact() //TODO: check
        .build();
  }
}
