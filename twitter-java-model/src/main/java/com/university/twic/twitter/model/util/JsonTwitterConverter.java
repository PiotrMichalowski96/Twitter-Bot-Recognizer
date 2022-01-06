package com.university.twic.twitter.model.util;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.university.twic.twitter.model.domain.Tweet;
import com.university.twic.twitter.model.domain.TwitterBot;
import com.university.twic.twitter.model.domain.TwitterUser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

@Slf4j
@UtilityClass
public class JsonTwitterConverter {

  public static String readFileAsString(String fileName) throws IOException {
    String path = "classpath:" + fileName;
    return FileUtils.readFileToString(ResourceUtils.getFile(path), StandardCharsets.UTF_8);
  }

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

  public static TwitterBot extractTwitterBotFromJson(String jsonFilePath) throws IOException {
    String json = readFileAsString(jsonFilePath);
    try {
      return new Gson().fromJson(json, TwitterBot.class);
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
      logger.warn("Couldn't convert Json File to TwitterUser object, Json content: {}", json);
      return null;
    }
  }

  private static String getStringPropertyFromTweetJson(String tweetJson, String propertyName) {
    return JsonParser.parseString(tweetJson)
        .getAsJsonObject()
        .get(propertyName)
        .getAsString();
  }
}
