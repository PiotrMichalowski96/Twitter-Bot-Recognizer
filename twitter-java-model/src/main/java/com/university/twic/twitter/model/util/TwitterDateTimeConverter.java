package com.university.twic.twitter.model.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TwitterDateTimeConverter {

  public static String TWITTER_DATETIME_PATTERN = "EEE MMM dd HH:mm:ss X yyyy";

  public static LocalDateTime convertTwitterDateTime(String time) {
    DateTimeFormatter twitterDateFormatter = DateTimeFormatter.ofPattern(TWITTER_DATETIME_PATTERN, Locale.ROOT);
    return LocalDateTime.parse(time, twitterDateFormatter);
  }
}
