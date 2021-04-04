package com.university.twic.tweets.processing.twitter.util;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.university.twic.tweets.processing.twitter.model.TwitterUser;
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
}
