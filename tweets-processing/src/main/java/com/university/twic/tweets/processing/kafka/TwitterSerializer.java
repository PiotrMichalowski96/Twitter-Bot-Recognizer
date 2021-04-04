package com.university.twic.tweets.processing.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;

@Slf4j
public class TwitterSerializer<T> implements Serializer<T> {

  @Override
  public byte[] serialize(String s, T t) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    try {
      return mapper.writeValueAsBytes(t);
    } catch (JsonProcessingException e) {
      logger.error("Couldn't serialize json of twitter entity: {}", t.toString());
      return null;
    }
  }
}
