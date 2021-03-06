package com.university.twic.calculate.bot.service.twitter;

import static com.university.twic.calculate.bot.math.Probability.multipleIncreaseProbability;
import static com.university.twic.calculate.bot.service.twitter.ModelParameter.FAST_TWEETING_INCREASE_WEIGHT;
import static com.university.twic.calculate.bot.service.twitter.ModelParameter.MIN_SEC_BETWEEN_TWEETS;
import static com.university.twic.calculate.bot.service.twitter.ModelParameter.TWEET_CONTENT_INCREASE_WEIGHT;

import com.university.twic.calculate.bot.service.CalculateBotModule;
import com.university.twic.twitter.model.domain.Tweet;
import com.university.twic.twitter.model.domain.TwitterBot;
import com.university.twic.twitter.model.domain.TwitterUser;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@RequiredArgsConstructor
@AllArgsConstructor
public class CalculateTwitterBotModule implements CalculateBotModule<TwitterBot, Tweet> {

  private TwitterBot previousBotModel;
  @NonNull
  private final Map<ModelParameter, Integer> modelParametersMap;
  @NonNull
  private final Set<String> warningWordsSet;
  @NonNull
  private final BigDecimal initialBotProbability;

  @Override
  public TwitterBot initializeBotModel() {
    return TwitterBot.builder()
        .botProbability(initialBotProbability)
        .analyzedTweets(0L)
        .build();
  }

  @Override
  public TwitterBot calculateBotModel(Tweet newEvent) {
    TwitterUser twitterUser = newEvent.getUser();
    String tweetContent = newEvent.getText();
    LocalDateTime tweetTime = newEvent.getCreatedTime();
    long analyzedTweets = previousBotModel.getAnalyzedTweets() + 1;
    BigDecimal botProbability = previousBotModel.getBotProbability();

    //Perform only one time per twitter user
    if (previousBotModel.getTwitterUser() == null) {
      botProbability = calculateBotProbabilityByTwitterUserData(twitterUser, botProbability);
    }

    LocalDateTime lastTweetTime = previousBotModel.getLastTweetDateTime();
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

  private BigDecimal calculateBotProbabilityByTwitterUserData(TwitterUser twitterUser,
      BigDecimal botProbability) {

    //verified account couldn't be a bot
    if (twitterUser.getVerified()) {
      return BigDecimal.ZERO;
    }

    botProbability = BotUserCriteria.getBotProbabilityAllCriteria(twitterUser, botProbability, modelParametersMap, warningWordsSet);

    return botProbability;
  }

  private BigDecimal calculateBotProbabilityByCurrentTweet(String tweetContent,
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

  private BigDecimal calculateBotProbabilityBasedOnTweetContent(String tweetContent, BigDecimal botProbability) {

    boolean isContentWarning = warningWordsSet.stream()
        .anyMatch(word -> StringUtils.containsIgnoreCase(tweetContent, word));

    int tweetContentIncreaseWeight = modelParametersMap.getOrDefault(TWEET_CONTENT_INCREASE_WEIGHT, 4);
    return (isContentWarning)
        ? multipleIncreaseProbability(botProbability, tweetContentIncreaseWeight) : botProbability;
  }

  private BigDecimal calculateBotProbabilityBasedOnFastTweeting(LocalDateTime newTweetTime,
      LocalDateTime lastTweetTime,
      BigDecimal botProbability) {

    long secondsBetweenTweets = Duration.between(lastTweetTime, newTweetTime).toSeconds();
    int minSecBetweenTweets = modelParametersMap.getOrDefault(MIN_SEC_BETWEEN_TWEETS, 5);
    int fastTweetingIncreaseWeight = modelParametersMap.getOrDefault(FAST_TWEETING_INCREASE_WEIGHT, 5);
    return (secondsBetweenTweets < minSecBetweenTweets)
        ? multipleIncreaseProbability(botProbability, fastTweetingIncreaseWeight) : botProbability;
  }
}
