package com.university.twic.twitter.producer.domain.twitter;

public interface TwitterProducer {
  void send(String tweet);
}
