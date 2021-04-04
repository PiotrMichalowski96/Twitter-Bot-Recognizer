package com.university.twic.tweets.processing.twitter.bot;

import com.university.twic.tweets.processing.twitter.model.Tweet;
import com.university.twic.tweets.processing.twitter.model.TwitterUser;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;

/**
 * It contains some basic methods to calculate probability that Twitter Account is a bot
 */
@UtilityClass
public class TwitterBotRecognizing {

  public static TwitterBot initialEmptyTwitterBotModel() {
    //TODO: create initial model for detecting bot accounts
    BigDecimal botProbability = BigDecimal.ONE;

    return TwitterBot.builder()
        .botProbability(botProbability)
        .build();
  }

  public static TwitterBot updateBotModelParameters(TwitterBot twitterBot, Tweet tweet) {
    TwitterUser twitterUser = tweet.getUser();
    String tweetContent = tweet.getText();
    LocalDateTime tweetTime = tweet.getCreatedTime();
    //TODO: change hardcoded bot probability
    BigDecimal botProbability = twitterBot.getBotProbability().multiply(BigDecimal.ONE);

    return TwitterBot.builder()
        .twitterUser(twitterUser)
        .lastTweetContent(tweetContent)
        .lastTweetDateTime(tweetTime)
        .botProbability(botProbability)
        .build();
  }

  public static boolean checkIsTwitterBot(TwitterBot twitterBot) {
    BigDecimal botProbability = twitterBot.getBotProbability();
    //TODO: change
    return botProbability.compareTo(BigDecimal.ZERO) >= 0;
  }
}
