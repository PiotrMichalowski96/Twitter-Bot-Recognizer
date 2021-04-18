package com.university.twic.tweets.processing.twitter.util;

import static com.university.twic.tweets.processing.util.JsonReaderUtil.readFileAsString;
import static org.assertj.core.api.Assertions.assertThat;

import com.university.twic.tweets.processing.twitter.model.Tweet;
import com.university.twic.tweets.processing.twitter.model.TwitterUser;
import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class JsonTwitterConverterTest {

  @Test
  public void shouldExtractUserFromTweet() throws IOException {
    //given
    String sampleTweetJson = readFileAsString("sampleTweet.json");
    TwitterUser expectedTwitterUser = getExpectedTwitterUser();

    //when
    TwitterUser resultTwitterUser = JsonTwitterConverter.extractUserFromTweetJson(sampleTweetJson);

    //then
    assertThat(resultTwitterUser).isEqualToComparingFieldByField(expectedTwitterUser);
  }

  @Test
  public void shouldReturnTwitterWithNullFieldsIfWrongJsonTweet() {
    //given
    String wrongTweetJson = "{user:{}}";

    //when
    TwitterUser resultTwitterUser = JsonTwitterConverter.extractUserFromTweetJson(wrongTweetJson);

    //then
    assertThat(resultTwitterUser).hasAllNullFieldsOrProperties();
  }

  @Test
  public void shouldExtractCorrectTweet() throws IOException {
    //given
    String sampleTweetJson = readFileAsString("sampleTweet.json");
    Tweet expectedTweet = getExpectedTweet();

    //when
    Tweet resultTweet = JsonTwitterConverter.extractTweetFromJson(sampleTweetJson);

    //then
    assertThat(resultTweet).isEqualToComparingFieldByFieldRecursively(expectedTweet);
  }

  private TwitterUser getExpectedTwitterUser() {
    return TwitterUser.builder()
        .id(1127819079744729088L)
        .name("Airdropcu90")
        .screenName("airdropcu90")
        .verified(false)
        .followersCount(27L)
        .friendsCount(347L)
        .favouritesCount(264L)
        .statusesCount(641L)
        .createdAt("Mon May 13 06:13:17 +0000 2019")
        .defaultProfile(true)
        .defaultProfileImage(false)
        .build();
  }

  private Tweet getExpectedTweet() {
    TwitterUser expectedTwitterUser = getExpectedTwitterUser();
    String expectedText = "RT @AirdropStario: \ud83d\udca7Twinci Airdrop \ud83d\udca7\n\n\ud83c\udfc6 Task:          \u2795 $15 worth of  TRX\n\n                            \u2795 $50 worth of TWIN\n\n\ud83d\udd1b Airdrop Link\u2026";
    LocalDateTime expectedDateTime = LocalDateTime.of(2021, 4, 2, 11, 49, 15);
    return new Tweet(expectedTwitterUser, expectedText, expectedDateTime);
  }
}
