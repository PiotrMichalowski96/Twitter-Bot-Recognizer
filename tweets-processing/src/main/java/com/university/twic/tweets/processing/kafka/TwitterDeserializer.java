package com.university.twic.tweets.processing.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

@Slf4j
@RequiredArgsConstructor
public class TwitterDeserializer<T> implements Deserializer<T> {

  private final Class<T> clazz;

  @Override
  public T deserialize(String s, byte[] bytes) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    try {
      return mapper.readValue(bytes, clazz);
    } catch (IOException e) {
      logger.error("Couldn't deserialize json of twitter entity object: {}", clazz.getSimpleName());
      return null;
    }
  }
}
