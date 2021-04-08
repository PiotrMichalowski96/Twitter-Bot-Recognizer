package com.university.twic.elastic.consumer;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@Slf4j
@SpringBootApplication
public class KafkaElasticsearchConsumerApplication {
  public static void main(String[] args) {
    SpringApplication.run(KafkaElasticsearchConsumerApplication.class, args);
  }
}