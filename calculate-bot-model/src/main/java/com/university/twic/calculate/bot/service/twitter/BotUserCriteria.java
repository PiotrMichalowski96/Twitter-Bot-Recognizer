package com.university.twic.calculate.bot.service.twitter;

import static com.university.twic.calculate.bot.math.Probability.decreaseProbability;
import static com.university.twic.calculate.bot.math.Probability.increaseProbability;
import static com.university.twic.calculate.bot.math.Probability.multipleDecreaseProbability;
import static com.university.twic.calculate.bot.math.Probability.multipleIncreaseProbability;
import static com.university.twic.calculate.bot.service.twitter.ModelParameter.*;

import com.university.twic.twitter.model.domain.TwitterUser;
import com.university.twic.twitter.model.util.TwitterDateTimeConverter;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * It contains some basic criteria to calculate probability that Twitter Account is a bot
 */
@Getter
@RequiredArgsConstructor
enum BotUserCriteria {

  PHOTO() {
    @Override
    public BigDecimal calculateBotProbability(TwitterUser twitterUser, BigDecimal botProbability,
        Map<ModelParameter, Integer> modelParametersMap) {

      int photoIncreaseWeight = modelParametersMap.getOrDefault(PHOTO_INCREASE_WEIGHT, 5);
      int photoDecreaseWeight = modelParametersMap.getOrDefault(PHOTO_DECREASE_WEIGHT, 2);
      return twitterUser.getDefaultProfile()
          ? multipleIncreaseProbability(botProbability, photoIncreaseWeight)
          : multipleDecreaseProbability(botProbability, photoDecreaseWeight);
    }
  },

  BACKGROUND(){
    @Override
    public BigDecimal calculateBotProbability(TwitterUser twitterUser, BigDecimal botProbability,
        Map<ModelParameter, Integer> modelParametersMap) {

      int backgroundIncreaseWeight = modelParametersMap.getOrDefault(BACKGROUND_INCREASE_WEIGHT, 4);
      return twitterUser.getDefaultProfileImage()
          ? multipleIncreaseProbability(botProbability, backgroundIncreaseWeight)
          : decreaseProbability(botProbability);
    }
  },

  CREATING_TIME(){
    @Override
    public BigDecimal calculateBotProbability(TwitterUser twitterUser, BigDecimal botProbability,
        Map<ModelParameter, Integer> modelParametersMap) {

      String accountTime = twitterUser.getCreatedAt();
      LocalDateTime createdAccountTime = TwitterDateTimeConverter.convertTwitterDateTime(accountTime);
      int recentlyInHours = modelParametersMap.getOrDefault(RECENTLY_IN_HOURS, 240);
      boolean isRecentlyCreated = (Duration.between(createdAccountTime, LocalDateTime.now()).toHours() < recentlyInHours);
      int creatingTimeIncreaseWeight = modelParametersMap.getOrDefault(CREATING_TIME_INCREASE_WEIGHT, 2);
      return isRecentlyCreated ? multipleIncreaseProbability(botProbability, creatingTimeIncreaseWeight) : botProbability;
    }
  },

  ACCOUNT_NAME(){
    @Override
    public BigDecimal calculateBotProbability(TwitterUser twitterUser, BigDecimal botProbability,
        Map<ModelParameter, Integer> modelParametersMap) {

      String userName = StringUtils.remove(twitterUser.getName(), StringUtils.SPACE);
      String screenName = StringUtils.remove(twitterUser.getName(), StringUtils.SPACE);
      int accountNameIncreaseWeight = modelParametersMap.getOrDefault(ACC_NAME_INCREASE_WEIGHT, 3);
      boolean doesNameContainNumbers = (!StringUtils.isAlpha(userName) || !StringUtils.isAlpha(screenName));
      return doesNameContainNumbers ? multipleIncreaseProbability(botProbability, accountNameIncreaseWeight) : botProbability;
    }
  },

  FOLLOWERS(){
    @Override
    public BigDecimal calculateBotProbability(TwitterUser twitterUser, BigDecimal botProbability,
        Map<ModelParameter, Integer> modelParametersMap) {

      Long followers = twitterUser.getFollowersCount();
      Long followings = twitterUser.getFriendsCount();
      int minFollowers = modelParametersMap.getOrDefault(MIN_FOLLOWERS, 10);
      int maxFollowers = modelParametersMap.getOrDefault(MAX_FOLLOWINGS, 100);
      int followersIncreaseWeight = modelParametersMap.getOrDefault(FOLLOWERS_INCREASE_WEIGHT, 2);
      return (followers < minFollowers && followings > maxFollowers)
          ? multipleIncreaseProbability(botProbability ,followersIncreaseWeight) : botProbability;
    }
  },

  LIKED_TWEETS(){
    @Override
    public BigDecimal calculateBotProbability(TwitterUser twitterUser, BigDecimal botProbability,
        Map<ModelParameter, Integer> modelParametersMap) {

      Long likedTweets = twitterUser.getFavouritesCount();
      int maxLikedTweets = modelParametersMap.getOrDefault(MAX_LIKED_TWEETS, 1000);
      return (likedTweets > maxLikedTweets) ? increaseProbability(botProbability) : botProbability;
    }
  },

  ISSUED_TWEETS(){
    @Override
    public BigDecimal calculateBotProbability(TwitterUser twitterUser, BigDecimal botProbability,
        Map<ModelParameter, Integer> modelParametersMap) {

      Long issuedTweets = twitterUser.getStatusesCount();
      int maxIssuedTweets = modelParametersMap.getOrDefault(MAX_ISSUED_TWEETS, 1000);
      return (issuedTweets > maxIssuedTweets) ? increaseProbability(botProbability) : botProbability;
    }
  };

  public abstract BigDecimal calculateBotProbability(TwitterUser twitterUser,
      BigDecimal botProbability, Map<ModelParameter, Integer> modelParametersMap);

  public static BigDecimal getBotProbabilityAllCriteria(TwitterUser twitterUser,
      BigDecimal botProbability, Map<ModelParameter, Integer> modelParametersMap, Set<String> warningWordsProperty) {

    for (BotUserCriteria criteria : BotUserCriteria.values()) {
      botProbability = criteria.calculateBotProbability(twitterUser, botProbability, modelParametersMap);
    }
    botProbability = botProbabilityBasedOnDescription(twitterUser, botProbability, modelParametersMap, warningWordsProperty);
    return botProbability;
  }

  private static BigDecimal botProbabilityBasedOnDescription(TwitterUser twitterUser, BigDecimal botProbability,
      Map<ModelParameter, Integer> modelParametersMap, Set<String> warningWords) {

    String description = twitterUser.getDescription();
    boolean isDescriptionWarning = warningWords.stream()
        .anyMatch(word -> StringUtils.containsIgnoreCase(description, word));

    int descriptionIncreaseWeight = modelParametersMap.getOrDefault(DESCRIPTION_INCREASE_WEIGHT, 4);
    return isDescriptionWarning ? multipleIncreaseProbability(botProbability, descriptionIncreaseWeight) : botProbability;
  }
}
