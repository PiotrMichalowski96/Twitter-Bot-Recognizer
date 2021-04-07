package com.university.twic.tweets.processing.twitter.bot;

import static com.university.twic.tweets.processing.twitter.bot.math.Parameters.MIN_SEC_BETWEEN_TWEETS;
import static com.university.twic.tweets.processing.twitter.bot.math.Parameters.WARNING_WORDS;
import static com.university.twic.tweets.processing.twitter.bot.math.Probability.INITIAL_BOT_PROBABILITY;
import static com.university.twic.tweets.processing.twitter.bot.math.Probability.increaseProbability;

import com.university.twic.tweets.processing.twitter.model.Tweet;
import com.university.twic.tweets.processing.twitter.model.TwitterUser;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class TwitterBotRecognizing {

  public static TwitterBot initialEmptyTwitterBotModel() {
    return TwitterBot.builder()
        .botProbability(INITIAL_BOT_PROBABILITY)
        .build();
  }

  public static TwitterBot updateBotModelParameters(TwitterBot twitterBot, Tweet tweet) {
    TwitterUser twitterUser = tweet.getUser();
    String tweetContent = tweet.getText();
    LocalDateTime tweetTime = tweet.getCreatedTime();

    BigDecimal botProbability = twitterBot.getBotProbability();

    //Perform only one time per twitter user
    if (twitterBot.getTwitterUser() == null) {
      botProbability = calculateBotProbabilityByTwitterUserData(twitterUser, botProbability);
    }

    LocalDateTime lastTweetTime = twitterBot.getLastTweetDateTime();
    botProbability = calculateBotProbabilityByCurrentTweet(tweetContent, tweetTime, lastTweetTime, botProbability);

    return TwitterBot.builder()
        .twitterUser(twitterUser)
        .lastTweetContent(tweetContent)
        .lastTweetDateTime(tweetTime)
        .botProbability(botProbability)
        .build();
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
    return (isContentWarning) ? increaseProbability(botProbability) : botProbability;
  }

  public static BigDecimal calculateBotProbabilityBasedOnFastTweeting(LocalDateTime newTweetTime,
      LocalDateTime lastTweetTime,
      BigDecimal botProbability) {

    long secondsBetweenTweets = Duration.between(lastTweetTime, newTweetTime).toSeconds();
    return (secondsBetweenTweets < MIN_SEC_BETWEEN_TWEETS) ? increaseProbability(botProbability) : botProbability;
  }
}