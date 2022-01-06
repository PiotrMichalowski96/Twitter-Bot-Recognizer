package com.university.twic.calculate.bot.service

import com.university.twic.twitter.model.domain.Tweet
import com.university.twic.twitter.model.domain.TwitterBot
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

import static com.university.twic.calculate.bot.util.BigDecimalAssertionUtil.assertBigDecimal
import static com.university.twic.twitter.model.util.JsonTwitterConverter.*
import static org.assertj.core.api.Assertions.assertThat

@SpringBootTest(classes = CalculateBotProcessorConfig.class)
@TestPropertySource("classpath:application.yml")
class CalculateBotProcessorServiceTest {

    @Autowired
    CalculateBotProcessorService<TwitterBot, Tweet> botProcessorService

    @Autowired
    CalculateBotProperties calculateBotProperties

    @Test
    void shouldInitializeBotModelWithInitialBotProbability() {
        //given
        BigDecimal expectedBotProbability = calculateBotProperties.getInitialBotProbability()

        //when
        TwitterBot twitterBot  = botProcessorService.initializeBot()
        BigDecimal actualBotProbability = twitterBot.getBotProbability()

        //then
        assertBigDecimal(actualBotProbability, expectedBotProbability, BigDecimal.valueOf(0.01))
    }

    @Test
    void shouldCalculateBotModel() {
        //given
        String tweetJson = readFileAsString('samples/tweet1.json')
        Tweet tweet = extractTweetFromJson(tweetJson)
        String twitterBotJson = readFileAsString('samples/twitterBot1.json')
        TwitterBot expectedTwitterBot = extractTwitterBotFromJson(twitterBotJson)

        //when
        TwitterBot initialTwitterBot  = botProcessorService.initializeBot()
        TwitterBot actualTwitterBot = botProcessorService.calculateBot(initialTwitterBot, tweet)

        //then
        assertThat(actualTwitterBot).usingRecursiveComparison().isEqualTo(expectedTwitterBot)
    }

}
