package com.university.twic.tweets.processing.twitter.bot;

import static com.university.twic.tweets.processing.twitter.bot.TwitterBotRecognizing.calculateBotProbabilityByCurrentTweet;
import static com.university.twic.tweets.processing.twitter.bot.TwitterBotRecognizing.calculateBotProbabilityByTwitterUserData;
import static com.university.twic.tweets.processing.util.BigDecimalAssertionUtil.assertBigDecimal;

import com.university.twic.tweets.processing.twitter.model.TwitterUser;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TwitterBotRecognizingTest {

  @Test
  public void shouldCalculateBotProbabilityBasedOnTweet() {
    //given
    String tweetContent = "Register here and join our bitcoin club";
    LocalDateTime newTweetTime = LocalDateTime.now();
    LocalDateTime lastTweetTime = LocalDateTime.now().minusSeconds(3);
    BigDecimal probability = BigDecimal.valueOf(0.3);

    BigDecimal expectedProbability = BigDecimal.valueOf(0.698);

    //when
    BigDecimal result = calculateBotProbabilityByCurrentTweet(tweetContent, newTweetTime,
        lastTweetTime, probability);

    //then
    assertBigDecimal(result, expectedProbability, BigDecimal.valueOf(0.001));
  }

  @ParameterizedTest
  @MethodSource("provideTwitterUsers")
  public void shouldCalculateBotProbabilityBasedOnTwitterUser(TwitterUser twitterUser, BigDecimal expectedProbability) {
    //given
    BigDecimal probability = BigDecimal.valueOf(0.3);

    //when
    BigDecimal result = calculateBotProbabilityByTwitterUserData(twitterUser, probability);

    //then
    assertBigDecimal(result, expectedProbability, BigDecimal.valueOf(0.001));
  }

  private static Stream<Arguments> provideTwitterUsers() {
    return Stream.of(
        Arguments.of(createBotUser(), BigDecimal.valueOf(0.914)),
        Arguments.of(createNormalUser(), BigDecimal.valueOf(0.039))
    );
  }

  private static TwitterUser createBotUser() {
    TwitterUser twitterUser = new TwitterUser();
    twitterUser.setVerified(false);
    twitterUser.setDefaultProfile(true);
    twitterUser.setDefaultProfileImage(true);
    twitterUser.setCreatedAt("Fri Jul 03 01:18:18 +0000 2009");
    twitterUser.setName("AA123");
    twitterUser.setScreenName("AA123");
    twitterUser.setDescription("Register here and join us");
    twitterUser.setFollowersCount(1L);
    twitterUser.setFriendsCount(1000L);
    twitterUser.setFavouritesCount(10000L);
    twitterUser.setStatusesCount(10000L);
    return twitterUser;
  }

  private static TwitterUser createNormalUser() {
    TwitterUser twitterUser = new TwitterUser();
    twitterUser.setVerified(false);
    twitterUser.setDefaultProfile(false);
    twitterUser.setDefaultProfileImage(false);
    twitterUser.setCreatedAt("Fri Jul 03 01:18:18 +0000 2009");
    twitterUser.setName("John Smith");
    twitterUser.setScreenName("JSmth");
    twitterUser.setDescription("Normal Description");
    twitterUser.setFollowersCount(50L);
    twitterUser.setFriendsCount(30L);
    twitterUser.setFavouritesCount(600L);
    twitterUser.setStatusesCount(1000L);
    return twitterUser;
  }
}
