package com.university.twic.tweets.processing.twitter.util;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.university.twic.tweets.processing.twitter.model.Tweet;
import com.university.twic.tweets.processing.twitter.model.TwitterUser;
import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class JsonTwitterConverter {

  public static TwitterUser extractUserFromTweetJson(String tweetJson) {
    try {
      String jsonUser = JsonParser.parseString(tweetJson)
          .getAsJsonObject()
          .get("user")
          .toString();

      Gson gson = new Gson();

      return gson.fromJson(jsonUser, TwitterUser.class);
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
      logger.warn("Couldn't convert Json File to TwitterUser object, Json content: {}", tweetJson);
      return null;
    }
  }

  public static Tweet extractTweetFromJson(String tweetJson) {
    String text = getStringPropertyFromTweetJson(tweetJson, "text");
    String time = getStringPropertyFromTweetJson(tweetJson, "created_at");
    LocalDateTime createdTime = TwitterDateTimeConverter.convertTwitterDateTime(time);
    TwitterUser twitterUser = extractUserFromTweetJson(tweetJson);
    return new Tweet(twitterUser, text, createdTime);
  }

  private static String getStringPropertyFromTweetJson(String tweetJson, String propertyName) {
    return JsonParser.parseString(tweetJson)
        .getAsJsonObject()
        .get(propertyName)
        .getAsString();
  }
}
