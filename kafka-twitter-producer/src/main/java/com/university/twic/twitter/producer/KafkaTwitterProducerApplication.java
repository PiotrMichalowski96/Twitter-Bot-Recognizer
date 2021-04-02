package com.university.twic.twitter.producer;

import com.university.twic.twitter.producer.service.TwitterSendingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class KafkaTwitterProducerApplication implements CommandLineRunner {

  private final TwitterSendingService twitterSendingServiceImpl;

  public static void main(String[] args) {
    SpringApplication.run(KafkaTwitterProducerApplication.class, args);
  }

  @Override
  public void run(String... args) {
    twitterSendingServiceImpl.startSendingTweetsProcess(List.of(args));
  }
}