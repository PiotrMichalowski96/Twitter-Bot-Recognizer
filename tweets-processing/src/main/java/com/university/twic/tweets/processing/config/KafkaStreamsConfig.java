package com.university.twic.tweets.processing.config;

import com.university.twic.tweets.processing.twitter.model.Tweet;
import com.university.twic.tweets.processing.twitter.util.JsonTwitterConverter;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamsConfig {

  @Value("${kafka.bootstrapAddress}")
  private String bootstrapAddress;

  @Value("${application.name}")
  private String appName;

  @Value("${kafka.topic.tweets}")
  private String topic;

  @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
  public KafkaStreamsConfiguration kafkaStreamConfig() {
    Map<String, Object> properties = new HashMap<>();

    properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    properties.put(StreamsConfig.APPLICATION_ID_CONFIG, appName);
    properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
    properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());

    return new KafkaStreamsConfiguration(properties);
  }

  @Bean
  public KStream<String, String> tweetStream(StreamsBuilder streamsBuilder, NewTopic usersTopic) {

    Serde<String> stringSerde = Serdes.String();

    //TODO: here is a business processing logic - recognizing twitter bot accounts
    KStream<String, String> tweetStream = streamsBuilder.stream(topic, Consumed.with(stringSerde, stringSerde));

    tweetStream
        .mapValues(JsonTwitterConverter::extractTweetFromJson)
        .filter((k, tweet) -> tweet.getUser() != null)
        .filter((k, tweet) -> tweet.getUser().getFriendsCount() > 1000)
        .mapValues(Tweet::toString)
        .to(usersTopic.name());

    return tweetStream;
  }
}
