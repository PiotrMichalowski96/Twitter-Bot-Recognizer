package com.university.twic.calculate.bot.service

import com.university.twic.twitter.model.domain.Tweet
import com.university.twic.twitter.model.domain.TwitterBot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

import static com.university.twic.calculate.bot.util.BigDecimalAssertionUtil.assertBigDecimal
import static com.university.twic.twitter.model.util.JsonTwitterConverter.*
import static org.assertj.core.api.Assertions.assertThat

@Title("Specification for Calculate Bot Processor Service")
@Narrative("It has to calculate twitter bot data based on tweets")
@SpringBootTest(classes = CalculateBotProcessorConfig.class)
@TestPropertySource("classpath:application.yml")
class CalculateBotProcessorServiceTest extends Specification {

    @Autowired
    CalculateBotProcessorService<TwitterBot, Tweet> botProcessorService

    @Autowired
    CalculateBotProperties calculateBotProperties

    def "should initialize bot model with initial bot probability"() {
        given:
        BigDecimal expectedBotProbability = calculateBotProperties.getInitialBotProbability()

        when:
        TwitterBot twitterBot  = botProcessorService.initializeBot()
        BigDecimal actualBotProbability = twitterBot.getBotProbability()

        then:
        assertBigDecimal(actualBotProbability, expectedBotProbability, BigDecimal.valueOf(0.01))
    }

    def "should calculate bot model based on tweet json"() {
        given:
        String tweetJson = readFileAsString('samples/tweet1.json')
        Tweet tweet = extractTweetFromJson(tweetJson)
        String twitterBotJson = readFileAsString('samples/twitterBot1.json')
        TwitterBot expectedTwitterBot = extractTwitterBotFromJson(twitterBotJson)

        when:
        TwitterBot initialTwitterBot  = botProcessorService.initializeBot()
        TwitterBot actualTwitterBot = botProcessorService.calculateBot(initialTwitterBot, tweet)

        then:
        assertThat(actualTwitterBot).usingRecursiveComparison().isEqualTo(expectedTwitterBot)
    }

}
