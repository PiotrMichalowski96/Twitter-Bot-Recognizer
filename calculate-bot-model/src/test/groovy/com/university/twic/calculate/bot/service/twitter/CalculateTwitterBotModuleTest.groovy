package com.university.twic.calculate.bot.service.twitter

import com.university.twic.twitter.model.domain.Tweet
import com.university.twic.twitter.model.domain.TwitterBot
import com.university.twic.twitter.model.domain.TwitterUser
import spock.lang.Specification

import java.time.LocalDateTime

import static com.university.twic.calculate.bot.service.twitter.ModelParameter.ACC_NAME_INCREASE_WEIGHT
import static com.university.twic.calculate.bot.service.twitter.ModelParameter.BACKGROUND_INCREASE_WEIGHT
import static com.university.twic.calculate.bot.service.twitter.ModelParameter.CREATING_TIME_INCREASE_WEIGHT
import static com.university.twic.calculate.bot.service.twitter.ModelParameter.DESCRIPTION_INCREASE_WEIGHT
import static com.university.twic.calculate.bot.service.twitter.ModelParameter.FAST_TWEETING_INCREASE_WEIGHT
import static com.university.twic.calculate.bot.service.twitter.ModelParameter.FOLLOWERS_INCREASE_WEIGHT
import static com.university.twic.calculate.bot.service.twitter.ModelParameter.MAX_FOLLOWINGS
import static com.university.twic.calculate.bot.service.twitter.ModelParameter.MAX_ISSUED_TWEETS
import static com.university.twic.calculate.bot.service.twitter.ModelParameter.MAX_LIKED_TWEETS
import static com.university.twic.calculate.bot.service.twitter.ModelParameter.MIN_FOLLOWERS
import static com.university.twic.calculate.bot.service.twitter.ModelParameter.MIN_SEC_BETWEEN_TWEETS
import static com.university.twic.calculate.bot.service.twitter.ModelParameter.PHOTO_DECREASE_WEIGHT
import static com.university.twic.calculate.bot.service.twitter.ModelParameter.PHOTO_INCREASE_WEIGHT
import static com.university.twic.calculate.bot.service.twitter.ModelParameter.RECENTLY_IN_HOURS
import static com.university.twic.calculate.bot.service.twitter.ModelParameter.TWEET_CONTENT_INCREASE_WEIGHT
import static com.university.twic.calculate.bot.util.BigDecimalAssertionUtil.assertBigDecimal
import static java.util.Map.entry

class CalculateTwitterBotModuleTest extends Specification {

    private static final BigDecimal INITIAL_BOT_PROBABILITY = BigDecimal.valueOf(0.3)
    private static final Set<String> WARNING_WORDS = Set.of('register', 'bonus')
    private static final Map<ModelParameter, Integer> MODEL_PARAMETERS_MAP = Map.ofEntries(
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

    def "should initialize bot model"() {
        given:
        CalculateTwitterBotModule twitterBotModule = new CalculateTwitterBotModule(MODEL_PARAMETERS_MAP, WARNING_WORDS, INITIAL_BOT_PROBABILITY)

        when:
        TwitterBot twitterBot = twitterBotModule.initializeBotModel()

        then:
        twitterBot.botProbability == INITIAL_BOT_PROBABILITY
        twitterBot.analyzedTweets == 0
    }

    def "should calculate bot probability based on tweet"() {
        given:
        String tweetContent = 'Register here and join our bitcoin club'
        LocalDateTime newTweetTime = LocalDateTime.now()
        LocalDateTime lastTweetTime = LocalDateTime.now().minusSeconds(3)

        TwitterBot previousTwitterBot = TwitterBot.builder()
            .botProbability(0.3)
            .analyzedTweets(1)
            .lastTweetDateTime(lastTweetTime)
            .build()

        Tweet newTweet = new Tweet(createNormalUser(), tweetContent, newTweetTime)

        CalculateTwitterBotModule twitterBotModule = new CalculateTwitterBotModule(previousTwitterBot, MODEL_PARAMETERS_MAP, WARNING_WORDS, INITIAL_BOT_PROBABILITY)

        when:
        TwitterBot actualTwitterBot = twitterBotModule.calculateBotModel(newTweet)

        then:
        actualTwitterBot.analyzedTweets == 2
        assertBigDecimal(actualTwitterBot.getBotProbability(), BigDecimal.valueOf(0.627), BigDecimal.valueOf(0.001))
    }

    def "should calculate bot probability based on twitter user"(TwitterUser twitterUser, BigDecimal expectedProbability) {
        expect:
        String normalTweetContent = 'It is normal tweet'
        LocalDateTime newTweetTime = LocalDateTime.now()
        LocalDateTime lastTweetTime = LocalDateTime.now().minusHours(3)

        TwitterBot previousTwitterBot = TwitterBot.builder()
                .botProbability(0.3)
                .analyzedTweets(1)
                .lastTweetDateTime(lastTweetTime)
                .build()

        Tweet newTweet = new Tweet(twitterUser, normalTweetContent, newTweetTime)

        CalculateTwitterBotModule twitterBotModule = new CalculateTwitterBotModule(previousTwitterBot, MODEL_PARAMETERS_MAP, WARNING_WORDS, INITIAL_BOT_PROBABILITY)

        TwitterBot actualTwitterBot = twitterBotModule.calculateBotModel(newTweet)

        assertBigDecimal(actualTwitterBot.getBotProbability(), expectedProbability, BigDecimal.valueOf(0.001))

        where:
        twitterUser        | expectedProbability
        createBotUser()    | BigDecimal.valueOf(0.914)
        createNormalUser() | BigDecimal.valueOf(0.039)
    }

    private static TwitterUser createBotUser() {
        TwitterUser twitterUser = new TwitterUser()
        twitterUser.setVerified(false)
        twitterUser.setDefaultProfile(true)
        twitterUser.setDefaultProfileImage(true)
        twitterUser.setCreatedAt('Fri Jul 03 01:18:18 +0000 2009')
        twitterUser.setName('AA123')
        twitterUser.setScreenName('AA123')
        twitterUser.setDescription('Register here and join us')
        twitterUser.setFollowersCount(1L)
        twitterUser.setFriendsCount(1000L)
        twitterUser.setFavouritesCount(10000L)
        twitterUser.setStatusesCount(10000L)
        return twitterUser
    }

    private static TwitterUser createNormalUser() {
        TwitterUser twitterUser = new TwitterUser()
        twitterUser.setVerified(false)
        twitterUser.setDefaultProfile(false)
        twitterUser.setDefaultProfileImage(false)
        twitterUser.setCreatedAt('Fri Jul 03 01:18:18 +0000 2009')
        twitterUser.setName('John Smith')
        twitterUser.setScreenName('JSmth')
        twitterUser.setDescription('Normal Description')
        twitterUser.setFollowersCount(50L)
        twitterUser.setFriendsCount(30L)
        twitterUser.setFavouritesCount(600L)
        twitterUser.setStatusesCount(1000L)
        return twitterUser
    }
}
