package com.university.twic.tweets.processing.twitter.bot;

import static com.university.twic.tweets.processing.twitter.bot.math.Probability.decreaseProbability;
import static com.university.twic.tweets.processing.twitter.bot.math.Probability.increaseProbability;
import static com.university.twic.tweets.processing.twitter.bot.math.Probability.multipleIncreaseProbability;

import com.university.twic.tweets.processing.twitter.model.TwitterUser;
import com.university.twic.tweets.processing.twitter.util.TwitterDateTimeConverter;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.BiFunction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * It contains some basic criteria to calculate probability that Twitter Account is a bot
 */
@Getter
@RequiredArgsConstructor
enum BotUserCriteria {

  PHOTO((twitterUser, botProbability) -> twitterUser.getDefaultProfile()
      ? multipleIncreaseProbability(botProbability, 5)
      : decreaseProbability(botProbability)),

  BACKGROUND((twitterUser, botProbability) -> twitterUser.getDefaultProfileImage()
      ? increaseProbability(botProbability)
      : decreaseProbability(botProbability)),

  CREATING_TIME((twitterUser, botProbability) -> {
    String accountTime = twitterUser.getCreatedAt();
    LocalDateTime createdAccountTime = TwitterDateTimeConverter.convertTwitterDateTime(accountTime);
    boolean isRecentlyCreated = (Duration.between(createdAccountTime, LocalDateTime.now()).toHours()
        < 240L);
    return isRecentlyCreated ? increaseProbability(botProbability) : botProbability;
  }),

  ACCOUNT_NAME((twitterUser, botProbability) -> {
    String userName = twitterUser.getName();
    String screenName = twitterUser.getScreenName();
    boolean doesNameContainNumbers = (!StringUtils.isAlpha(userName) || !StringUtils
        .isAlpha(screenName));
    return doesNameContainNumbers ? increaseProbability(botProbability) : botProbability;
  }),

  DESCRIPTION((twitterUser, botProbability) -> {
    final Set<String> WARNING_WORDS = Set.of("register", "join", "sign up"); //TODO: move to other class Parameters

    String description = twitterUser.getDescription();
    boolean isDescriptionWarning = WARNING_WORDS.stream()
        .anyMatch(word -> StringUtils.containsIgnoreCase(description, word));

    return isDescriptionWarning ? increaseProbability(botProbability) : botProbability;
  }),

  FOLLOWERS((twitterUser, botProbability) -> {
    Long followers = twitterUser.getFollowersCount();
    Long followings = twitterUser.getFriendsCount();
    return (followers < 10 && followings > 100) ? increaseProbability(botProbability)
        : botProbability;
  }),

  LIKED_TWEETS((twitterUser, botProbability) -> {
    Long likedTweets = twitterUser.getFavouritesCount();
    return (likedTweets > 1000) ? increaseProbability(botProbability) : botProbability;
  }),

  ISSUED_TWEETS((twitterUser, botProbability) -> {
    Long issuedTweets = twitterUser.getStatusesCount();
    return (issuedTweets > 1000) ? increaseProbability(botProbability) : botProbability;
  });

  private final BiFunction<TwitterUser, BigDecimal, BigDecimal> calculationBotProbability;

  public static BigDecimal getBotProbabilityAllCriteria(TwitterUser twitterUser, BigDecimal botProbability) {
    for (BotUserCriteria criteria : BotUserCriteria.values()) {
      botProbability = criteria.getCalculationBotProbability().apply(twitterUser, botProbability);
    }
    return botProbability;
  }
}
