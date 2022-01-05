package com.university.twic.tweets.processing.streams

import com.university.twic.calculate.bot.model.Tweet
import com.university.twic.calculate.bot.model.TwitterBot
import com.university.twic.calculate.bot.model.TwitterUser
import com.university.twic.calculate.bot.service.CalculateBotModuleCreator
import com.university.twic.calculate.bot.service.CalculateBotProcessorService
import com.university.twic.calculate.bot.service.twitter.CalculateTwitterBotModule
import com.university.twic.calculate.bot.service.twitter.ModelParameter
import com.university.twic.tweets.processing.kafka.TwitterDeserializer
import com.university.twic.tweets.processing.twitter.util.JsonTwitterConverter
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.RandomStringUtils
import org.apache.kafka.common.serialization.LongDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static com.university.twic.calculate.bot.service.twitter.ModelParameter.*
import static com.university.twic.tweets.processing.util.JsonReaderUtil.extractTwitterBotFromJson
import static com.university.twic.tweets.processing.util.JsonReaderUtil.readFileAsString
import static java.util.Map.entry

@Slf4j
class KafkaStreamsConfigTest {

    private static final String INPUT_TOPIC_NAME = 'inputTopic'
    private static final String OUTPUT_TOPIC_NAME = 'outputTopic'

    private KafkaStreamsConfig kafkaStreams
    private CalculateBotProcessorService<TwitterBot, Tweet> botProcessorService

    private TopologyTestDriver testDriver
    private TestInputTopic<String, String> inputTopic
    private TestOutputTopic<Long, TwitterBot> outputTopic

    @BeforeEach
    void setup() throws IOException {
        StreamsBuilder builder = new StreamsBuilder()
        kafkaStreams = initializeKafkaStreamsConfig()
        botProcessorService = initializeBotProcessorService()

        //Create Actual Stream Processing pipeline
        kafkaStreams.tweetStream(builder, botProcessorService)

        testDriver = new TopologyTestDriver(builder.build(), kafkaStreams.kafkaStreamConfig().asProperties())

        inputTopic = testDriver.createInputTopic(INPUT_TOPIC_NAME, new StringSerializer(), new StringSerializer())
        outputTopic = testDriver.createOutputTopic(OUTPUT_TOPIC_NAME, new LongDeserializer(), new TwitterDeserializer<>(TwitterBot.class))
    }

    @AfterEach
    void tearDown() {
        try {
            testDriver.close()
        } catch (final RuntimeException e) {
            log.warn('Ignoring exception, test failing in Windows due this exception: {}', e.getLocalizedMessage())
        }
    }

    @Test
    void testProcessingStreamOfOneTweet() {
        //given
        String tweet = readFileAsString('samples/input/tweet1.json')
        Long expectedKey = extractUserIdFrom(tweet)

        TwitterBot expectedTwitterBot = extractTwitterBotFromJson('samples/output/twitterBot1.json')

        //when
        inputTopic.pipeInput(tweet)
        KeyValue<Long, TwitterBot> twitterBotKeyValue = outputTopic.readKeyValue()
        TwitterBot actualTwitterBot = twitterBotKeyValue.value

        //then
        Assertions.assertThat(twitterBotKeyValue.key).isEqualTo(expectedKey)
        Assertions.assertThat(actualTwitterBot).usingRecursiveComparison().isEqualTo(expectedTwitterBot)
    }

    @Test
    void testProcessingStreamOfTweetsSequence() {
        //given
        String tweet1 = readFileAsString('samples/input/tweet2_1.json')
        String tweet2 = readFileAsString('samples/input/tweet2_2.json')
        String tweet3 = readFileAsString('samples/input/tweet2_3.json')
        String tweet4 = readFileAsString('samples/input/tweet2_4.json')

        Long userId1 = extractUserIdFrom(tweet1)
        Long userId2 = extractUserIdFrom(tweet3)

        final Map<Long, TwitterBot> expectedTwitterBots = Map.ofEntries(
            entry(userId1, extractTwitterBotFromJson('samples/output/twitterBot2_1.json')),
            entry(userId2, extractTwitterBotFromJson('samples/output/twitterBot2_2.json'))
        )

        //when
        inputTopic.pipeInput(tweet1)
        inputTopic.pipeInput(tweet2)
        inputTopic.pipeInput(tweet3)
        inputTopic.pipeInput(tweet4)

        final Map<Long, TwitterBot> actualTwitterBots = outputTopic.readKeyValuesToMap()

        //then
        Assertions.assertThat(actualTwitterBots).usingRecursiveComparison().isEqualTo(expectedTwitterBots)
    }

    private static Long extractUserIdFrom(String tweetJson) {
        return Optional.ofNullable(tweetJson)
            .map(JsonTwitterConverter::extractTweetFromJson)
            .map(Tweet::getUser)
            .map(TwitterUser::getId)
            .orElse(null)
    }

    private static KafkaStreamsConfig initializeKafkaStreamsConfig() {
        KafkaStreamsConfig kafkaStreamsConfig = new KafkaStreamsConfig()
        kafkaStreamsConfig.setInputTopic(INPUT_TOPIC_NAME)
        kafkaStreamsConfig.setIntermediaryTopic('intermediaryTopic')
        kafkaStreamsConfig.setOutputTopic(OUTPUT_TOPIC_NAME)
        kafkaStreamsConfig.setAppName(RandomStringUtils.randomAlphabetic(10))
        kafkaStreamsConfig.setBootstrapAddress('1.2.3.4')
        return kafkaStreamsConfig
    }

    private static CalculateBotProcessorService<TwitterBot, Tweet> initializeBotProcessorService() {
        BigDecimal initialBotProbability = BigDecimal.valueOf(0.3)
        Set<String> warningWords = Set.of('register', 'join', 'sign', 'link', 'retweet', 'receive', 'bonus')
        Map<ModelParameter, Integer> modelBotFactorsMap = Map.ofEntries(
            entry(RECENTLY_IN_HOURS, 240),
            entry(MIN_FOLLOWERS, 10),
            entry(MAX_FOLLOWINGS, 100),
            entry(MAX_LIKED_TWEETS, 1000),
            entry(MAX_ISSUED_TWEETS, 1000),
            entry(MIN_SEC_BETWEEN_TWEETS, 5),
            entry(PHOTO_INCREASE_WEIGHT, 5),
            entry(PHOTO_DECREASE_WEIGHT, 2),
            entry(BACKGROUND_INCREASE_WEIGHT, 4),
            entry(CREATING_TIME_INCREASE_WEIGHT, 4),
            entry(ACC_NAME_INCREASE_WEIGHT, 3),
            entry(DESCRIPTION_INCREASE_WEIGHT, 4),
            entry(FOLLOWERS_INCREASE_WEIGHT, 2),
            entry(TWEET_CONTENT_INCREASE_WEIGHT, 4),
            entry(FAST_TWEETING_INCREASE_WEIGHT, 5)
        )

        CalculateBotModuleCreator<TwitterBot, Tweet> botModuleCreator = previousBotModel ->  previousBotModel == null ?
                new CalculateTwitterBotModule(modelBotFactorsMap, warningWords, initialBotProbability) :
                new CalculateTwitterBotModule(previousBotModel, modelBotFactorsMap, warningWords, initialBotProbability)

        return new CalculateBotProcessorService<TwitterBot, Tweet>(botModuleCreator)
    }
}
