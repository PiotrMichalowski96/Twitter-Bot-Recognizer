package com.university.twic.elastic.consumer.util;

import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TwitterBotJsonExtractor {
  public static String extractIdFromTwitterBotModel(String tweetJson) {
    return JsonParser.parseString(tweetJson)
        .getAsJsonObject()
        .get("twitterUser")
        .getAsJsonObject()
        .get("id")
        .getAsString();
  }
}
