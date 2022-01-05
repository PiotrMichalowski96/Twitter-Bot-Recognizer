package com.university.twic.tweets.processing.twitter.bot;

import static com.university.twic.calculate.bot.service.twitter.Parameters.FAST_TWEETING_INCREASE_WEIGHT;
import static com.university.twic.calculate.bot.service.twitter.Parameters.MIN_SEC_BETWEEN_TWEETS;
import static com.university.twic.calculate.bot.service.twitter.Parameters.TWEET_CONTENT_INCREASE_WEIGHT;
import static com.university.twic.calculate.bot.service.twitter.Parameters.WARNING_WORDS;
import static com.university.twic.calculate.bot.math.Probability.INITIAL_BOT_PROBABILITY;
import static com.university.twic.calculate.bot.math.Probability.multipleIncreaseProbability;

import com.university.twic.calculate.bot.model.Tweet;
import com.university.twic.calculate.bot.model.TwitterBot;
import com.university.twic.calculate.bot.model.TwitterUser;
import com.university.twic.calculate.bot.service.twitter.BotUserCriteria;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@UtilityClass
public class TwitterBotRecognizing {

  public static TwitterBot initialEmptyTwitterBotModel() {
    return TwitterBot.builder()
        .botProbability(INITIAL_BOT_PROBABILITY)
        .analyzedTweets(0L)
        .build();
  }

  public static TwitterBot updateBotModelParameters(TwitterBot twitterBot, Tweet tweet) {
    TwitterUser twitterUser = tweet.getUser();
    String tweetContent = tweet.getText();
    LocalDateTime tweetTime = tweet.getCreatedTime();
    long analyzedTweets = twitterBot.getAnalyzedTweets() + 1;
    BigDecimal botProbability = twitterBot.getBotProbability();

    //Perform only one time per twitter user
    if (twitterBot.getTwitterUser() == null) {
      botProbability = calculateBotProbabilityByTwitterUserData(twitterUser, botProbability);
    }

    LocalDateTime lastTweetTime = twitterBot.getLastTweetDateTime();
    botProbability = calculateBotProbabilityByCurrentTweet(tweetContent, tweetTime, lastTweetTime, botProbability);

    TwitterBot updatedTwitterBot = TwitterBot.builder()
        .twitterUser(twitterUser)
        .lastTweetContent(tweetContent)
        .lastTweetDateTime(tweetTime)
        .analyzedTweets(analyzedTweets)
        .botProbability(botProbability)
        .build();

    logger.info(updatedTwitterBot.toString());
    return updatedTwitterBot;
  }

  public static BigDecimal calculateBotProbabilityByTwitterUserData(TwitterUser twitterUser,
      BigDecimal botProbability) {

    //verified account couldn't be a bot
    if (twitterUser.getVerified()) {
      return BigDecimal.ZERO;
    }

    botProbability = BotUserCriteria.getBotProbabilityAllCriteria(twitterUser, botProbability);

    return botProbability;
  }

  public static BigDecimal calculateBotProbabilityByCurrentTweet(String tweetContent,
      LocalDateTime newTweetTime,
      LocalDateTime lastTweetTime,
      BigDecimal botProbability) {

    botProbability = calculateBotProbabilityBasedOnTweetContent(tweetContent, botProbability);

    //exclude first processed tweet
    if (lastTweetTime != null) {
      botProbability = calculateBotProbabilityBasedOnFastTweeting(newTweetTime, lastTweetTime, botProbability);
    }

    return botProbability;
  }

  public static BigDecimal calculateBotProbabilityBasedOnTweetContent(String tweetContent, BigDecimal botProbability) {

    boolean isContentWarning = WARNING_WORDS.stream()
        .anyMatch(word -> StringUtils.containsIgnoreCase(tweetContent, word));
    return (isContentWarning)
        ? multipleIncreaseProbability(botProbability, TWEET_CONTENT_INCREASE_WEIGHT) : botProbability;
  }

  public static BigDecimal calculateBotProbabilityBasedOnFastTweeting(LocalDateTime newTweetTime,
      LocalDateTime lastTweetTime,
      BigDecimal botProbability) {

    long secondsBetweenTweets = Duration.between(lastTweetTime, newTweetTime).toSeconds();
    return (secondsBetweenTweets < MIN_SEC_BETWEEN_TWEETS)
        ? multipleIncreaseProbability(botProbability, FAST_TWEETING_INCREASE_WEIGHT) : botProbability;
  }
}