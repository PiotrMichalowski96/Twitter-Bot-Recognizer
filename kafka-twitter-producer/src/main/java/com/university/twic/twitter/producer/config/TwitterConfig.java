package com.university.twic.twitter.producer.config;

import com.twitter.hbc.httpclient.auth.OAuth1;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class TwitterConfig {

  @Value("${twitter.consumerKey}")
  private String consumerKey;
  @Value("${twitter.consumerSecret}")
  private String consumerSecret;
  @Value("${twitter.token}")
  private String token;
  @Value("${twitter.tokenSecret}")
  private String secret;
  @Value("${twitter.capacity}")
  private int capacity;

  @Bean
  public OAuth1 authentication() {
    return new OAuth1(consumerKey, consumerSecret, token, secret);
  }

  @Bean
  @Scope(value = "prototype")
  public BlockingQueue<String> blockingQueue() {
    return new LinkedBlockingQueue<String>(capacity);
  }
}