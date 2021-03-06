package com.university.twic.tweets.processing.streams;

import com.university.twic.calculate.bot.service.CalculateBotProcessorConfig;
import com.university.twic.calculate.bot.service.CalculateBotProcessorService;
import com.university.twic.tweets.processing.kafka.TwitterDeserializer;
import com.university.twic.tweets.processing.kafka.TwitterSerializer;
import com.university.twic.twitter.model.domain.Tweet;
import com.university.twic.twitter.model.domain.TwitterBot;
import com.university.twic.twitter.model.util.JsonTwitterConverter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

@Configuration
@Import(value = CalculateBotProcessorConfig.class)
@EnableKafka
@EnableKafkaStreams
@Getter
@Setter
public class KafkaStreamsConfig {

  @Value("${kafka.bootstrapAddress}")
  private String bootstrapAddress;

  @Value("${application.name}")
  private String appName;

  @Value("${kafka.topic.tweets}")
  private String inputTopic;

  @Value("${kafka.topic.intermediary}")
  private String intermediaryTopic;

  @Value("${kafka.topic.users}")
  private String outputTopic;

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
  public KStream<String, String> tweetStream(StreamsBuilder streamsBuilder,
      CalculateBotProcessorService<TwitterBot, Tweet> botProcessorService) {

    final Serializer<Tweet> tweetSerializer = new TwitterSerializer<>();
    final Deserializer<Tweet> tweetDeserializer = new TwitterDeserializer<>(Tweet.class);
    final Serde<Tweet> tweetSerde = Serdes.serdeFrom(tweetSerializer, tweetDeserializer);

    final Serializer<TwitterBot> twitterBotSerializer = new TwitterSerializer<>();
    final Deserializer<TwitterBot> twitterBotDeserializer = new TwitterDeserializer<>(TwitterBot.class);
    final Serde<TwitterBot> twitterBotSerde = Serdes.serdeFrom(twitterBotSerializer, twitterBotDeserializer);

    KStream<String, String> tweetJsonStream = streamsBuilder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String()));

    tweetJsonStream
        .mapValues(JsonTwitterConverter::extractTweetFromJson)
        .filter((k, tweet) -> Objects.nonNull(tweet.getUser()))
        .selectKey((ignoredKey, tweet) -> tweet.getUser().getId())
        .to(intermediaryTopic, Produced.with(Serdes.Long(), tweetSerde));

    KStream<Long, Tweet> tweetStream = streamsBuilder
        .stream(intermediaryTopic, Consumed.with(Serdes.Long(), tweetSerde));

    KTable<Long, TwitterBot> twitterBotTable = tweetStream
        .groupByKey(Grouped.with(Serdes.Long(), tweetSerde))
        .aggregate(
            botProcessorService::initializeBot,
            (key, tweet, twitterBot) -> botProcessorService.calculateBot(twitterBot, tweet),
            Materialized.<Long, TwitterBot, KeyValueStore<Bytes, byte[]>>as("twitter-bot-agg")
                .withKeySerde(Serdes.Long())
                .withValueSerde(twitterBotSerde)
        );

    twitterBotTable.toStream().to(outputTopic, Produced.with(Serdes.Long(), twitterBotSerde));
    return tweetJsonStream;
  }
}
