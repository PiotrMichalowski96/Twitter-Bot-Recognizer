package com.university.twic.calculate.bot.service.twitter;

import static com.university.twic.calculate.bot.math.Probability.decreaseProbability;
import static com.university.twic.calculate.bot.math.Probability.increaseProbability;
import static com.university.twic.calculate.bot.math.Probability.multipleDecreaseProbability;
import static com.university.twic.calculate.bot.math.Probability.multipleIncreaseProbability;
import static com.university.twic.calculate.bot.service.twitter.Parameters.*;
import static com.university.twic.calculate.bot.service.twitter.Parameters.RECENTLY_IN_HOURS;

import com.university.twic.calculate.bot.model.TwitterUser;
import com.university.twic.calculate.bot.model.util.TwitterDateTimeConverter;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.BiFunction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * It contains some basic criteria to calculate probability that Twitter Account is a bot
 */
@Getter
@RequiredArgsConstructor
public enum BotUserCriteria {

  PHOTO((twitterUser, botProbability) -> twitterUser.getDefaultProfile()
      ? multipleIncreaseProbability(botProbability, PHOTO_INCREASE_WEIGHT)
      : multipleDecreaseProbability(botProbability, PHOTO_DECREASE_WEIGHT)),

  BACKGROUND((twitterUser, botProbability) -> twitterUser.getDefaultProfileImage()
      ? multipleIncreaseProbability(botProbability, BACKGROUND_INCREASE_WEIGHT)
      : decreaseProbability(botProbability)),

  CREATING_TIME((twitterUser, botProbability) -> {
    String accountTime = twitterUser.getCreatedAt();
    LocalDateTime createdAccountTime = TwitterDateTimeConverter.convertTwitterDateTime(accountTime);
    boolean isRecentlyCreated = (Duration.between(createdAccountTime, LocalDateTime.now()).toHours() < RECENTLY_IN_HOURS);
    return isRecentlyCreated ? multipleIncreaseProbability(botProbability, CREATING_TIME_INCREASE_WEIGHT) : botProbability;
  }),

  ACCOUNT_NAME((twitterUser, botProbability) -> {
    String userName = StringUtils.remove(twitterUser.getName(), StringUtils.SPACE);
    String screenName = StringUtils.remove(twitterUser.getName(), StringUtils.SPACE);
    boolean doesNameContainNumbers = (!StringUtils.isAlpha(userName) || !StringUtils.isAlpha(screenName));
    return doesNameContainNumbers ? multipleIncreaseProbability(botProbability, ACC_NAME_INCREASE_WEIGHT) : botProbability;
  }),

  DESCRIPTION((twitterUser, botProbability) -> {
    String description = twitterUser.getDescription();
    boolean isDescriptionWarning = WARNING_WORDS.stream()
        .anyMatch(word -> StringUtils.containsIgnoreCase(description, word));

    return isDescriptionWarning ? multipleIncreaseProbability(botProbability, DESCRIPTION_INCREASE_WEIGHT) : botProbability;
  }),

  FOLLOWERS((twitterUser, botProbability) -> {
    Long followers = twitterUser.getFollowersCount();
    Long followings = twitterUser.getFriendsCount();
    return (followers < MIN_FOLLOWERS && followings > MAX_FOLLOWINGS)
        ? multipleIncreaseProbability(botProbability ,FOLLOWERS_INCREASE_WEIGHT) : botProbability;
  }),

  LIKED_TWEETS((twitterUser, botProbability) -> {
    Long likedTweets = twitterUser.getFavouritesCount();
    return (likedTweets > MAX_LIKED_TWEETS) ? increaseProbability(botProbability) : botProbability;
  }),

  ISSUED_TWEETS((twitterUser, botProbability) -> {
    Long issuedTweets = twitterUser.getStatusesCount();
    return (issuedTweets > MAX_ISSUED_TWEETS) ? increaseProbability(botProbability) : botProbability;
  });

  private final BiFunction<TwitterUser, BigDecimal, BigDecimal> calculationBotProbability;

  public static BigDecimal getBotProbabilityAllCriteria(TwitterUser twitterUser, BigDecimal botProbability) {
    for (BotUserCriteria criteria : BotUserCriteria.values()) {
      botProbability = criteria.getCalculationBotProbability().apply(twitterUser, botProbability);
    }
    return botProbability;
  }
}
