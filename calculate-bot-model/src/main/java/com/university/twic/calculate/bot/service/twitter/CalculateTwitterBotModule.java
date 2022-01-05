package com.university.twic.calculate.bot.service.twitter;

import static com.university.twic.calculate.bot.math.Probability.INITIAL_BOT_PROBABILITY;
import static com.university.twic.calculate.bot.math.Probability.multipleIncreaseProbability;
import static com.university.twic.calculate.bot.service.twitter.Parameters.FAST_TWEETING_INCREASE_WEIGHT;
import static com.university.twic.calculate.bot.service.twitter.Parameters.MIN_SEC_BETWEEN_TWEETS;
import static com.university.twic.calculate.bot.service.twitter.Parameters.TWEET_CONTENT_INCREASE_WEIGHT;
import static com.university.twic.calculate.bot.service.twitter.Parameters.WARNING_WORDS;

import com.university.twic.calculate.bot.model.Tweet;
import com.university.twic.calculate.bot.model.TwitterBot;
import com.university.twic.calculate.bot.model.TwitterUser;
import com.university.twic.calculate.bot.service.CalculateBotModule;
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
  private final Map<String, Double> modelParametersMap;
  @NonNull
  private final Set<String> warningWordsSet;

  @Override
  public TwitterBot initializeBotModel() {
//    BigDecimal initialBotProbability = modelParametersMap.getOrDefault(ModelParameter.INITIAL_BOT_PROBABILITY, BigDecimal.ZERO);
    BigDecimal initialBotProbability = INITIAL_BOT_PROBABILITY;

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

    botProbability = BotUserCriteria.getBotProbabilityAllCriteria(twitterUser, botProbability);

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

    boolean isContentWarning = WARNING_WORDS.stream()
        .anyMatch(word -> StringUtils.containsIgnoreCase(tweetContent, word));
    return (isContentWarning)
        ? multipleIncreaseProbability(botProbability, TWEET_CONTENT_INCREASE_WEIGHT) : botProbability;
  }

  private BigDecimal calculateBotProbabilityBasedOnFastTweeting(LocalDateTime newTweetTime,
      LocalDateTime lastTweetTime,
      BigDecimal botProbability) {

    long secondsBetweenTweets = Duration.between(lastTweetTime, newTweetTime).toSeconds();
    return (secondsBetweenTweets < MIN_SEC_BETWEEN_TWEETS)
        ? multipleIncreaseProbability(botProbability, FAST_TWEETING_INCREASE_WEIGHT) : botProbability;
  }
}
