package com.university.twic.tweets.processing.twitter.bot;

import static com.university.twic.tweets.processing.twitter.bot.math.Probability.INITIAL_BOT_PROBABILITY;
import static com.university.twic.tweets.processing.twitter.bot.math.Probability.decreaseProbability;
import static com.university.twic.tweets.processing.twitter.bot.math.Probability.increaseProbability;

import com.university.twic.tweets.processing.twitter.model.Tweet;
import com.university.twic.tweets.processing.twitter.model.TwitterUser;
import com.university.twic.tweets.processing.twitter.util.TwitterDateTimeConverter;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * It contains some basic methods to calculate probability that Twitter Account is a bot
 */
@UtilityClass
public class TwitterBotRecognizing {

  private static final Set<String> WARNING_WORDS = Set.of("register", "join", "sign up");

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
    botProbability = calculateBotProbabilityByCurrentTweet(tweetContent, tweetTime,
        lastTweetTime, botProbability);

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

    botProbability = calculateBotProbabilityBasedOnPhoto(twitterUser, botProbability);

    botProbability = calculateBotProbabilityBasedOnBackground(twitterUser, botProbability);

    botProbability = calculateBotProbabilityBasedOnCreatingTime(twitterUser, botProbability);

    botProbability = calculateBotProbabilityBasedOnAccountName(twitterUser, botProbability);

    botProbability = calculateBotProbabilityBasedOnDescription(twitterUser, botProbability);

    botProbability = calculateBotProbabilityBasedOnFollowers(twitterUser, botProbability);

    botProbability = calculateBotProbabilityBasedOnLikedTweets(twitterUser, botProbability);

    botProbability = calculateBotProbabilityBasedOnIssuedTweets(twitterUser, botProbability);

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

  public static BigDecimal calculateBotProbabilityBasedOnPhoto(TwitterUser twitterUser,
      BigDecimal botProbability) {

    return twitterUser.getDefaultProfile() ? increaseProbability(botProbability)
        : decreaseProbability(botProbability);
  }

  public static BigDecimal calculateBotProbabilityBasedOnBackground(TwitterUser twitterUser,
      BigDecimal botProbability) {

    return twitterUser.getDefaultProfileImage() ? increaseProbability(botProbability)
        : decreaseProbability(botProbability);
  }

  public static BigDecimal calculateBotProbabilityBasedOnCreatingTime(TwitterUser twitterUser,
      BigDecimal botProbability) {

    String accountTime = twitterUser.getCreatedAt();
    LocalDateTime createdAccountTime = TwitterDateTimeConverter.convertTwitterDateTime(accountTime);
    boolean isRecentlyCreated = (Duration.between(createdAccountTime, LocalDateTime.now()).toHours() < 240L);
    return isRecentlyCreated ? increaseProbability(botProbability) : botProbability;
  }

  public static BigDecimal calculateBotProbabilityBasedOnAccountName(TwitterUser twitterUser,
      BigDecimal botProbability) {

    String userName = twitterUser.getName();
    String screenName = twitterUser.getScreenName();
    boolean doesNameContainNumbers = (!StringUtils.isAlpha(userName) || !StringUtils.isAlpha(screenName));
    return doesNameContainNumbers ? increaseProbability(botProbability) : botProbability;
  }

  public static BigDecimal calculateBotProbabilityBasedOnDescription(TwitterUser twitterUser,
      BigDecimal botProbability) {

    String description = twitterUser.getDescription();
    boolean isDescriptionWarning = WARNING_WORDS.stream()
        .anyMatch(word -> StringUtils.containsIgnoreCase(description, word));

    return isDescriptionWarning ? increaseProbability(botProbability) : botProbability;
  }

  public static BigDecimal calculateBotProbabilityBasedOnFollowers(TwitterUser twitterUser,
      BigDecimal botProbability) {

    Long followers = twitterUser.getFollowersCount();
    Long followings = twitterUser.getFriendsCount();
    return (followers < 10 && followings > 100) ? increaseProbability(botProbability) : botProbability;
  }

  public static BigDecimal calculateBotProbabilityBasedOnLikedTweets(TwitterUser twitterUser,
      BigDecimal botProbability) {

    Long likedTweets = twitterUser.getFavouritesCount();
    return (likedTweets > 1000) ? increaseProbability(botProbability) : botProbability;
  }

  public static BigDecimal calculateBotProbabilityBasedOnIssuedTweets(TwitterUser twitterUser,
      BigDecimal botProbability) {

    Long issuedTweets = twitterUser.getStatusesCount();
    return (issuedTweets > 1000) ? increaseProbability(botProbability) : botProbability;
  }

  public static BigDecimal calculateBotProbabilityBasedOnTweetContent(String tweetContent,
      BigDecimal botProbability) {

    boolean isContentWarning = WARNING_WORDS.stream()
        .anyMatch(word -> StringUtils.containsIgnoreCase(tweetContent, word));
    return (isContentWarning) ? increaseProbability(botProbability) : botProbability;
  }

  public static BigDecimal calculateBotProbabilityBasedOnFastTweeting(LocalDateTime newTweetTime,
      LocalDateTime lastTweetTime,
      BigDecimal botProbability) {

    long secondsBetweenTweets = Duration.between(lastTweetTime, newTweetTime).toSeconds();
    return (secondsBetweenTweets < 5L) ? increaseProbability(botProbability) : botProbability;
  }
}
