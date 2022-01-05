package com.university.twic.calculate.bot.service.twitter;

import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Parameters {

  // bot criteria
  public static final Set<String> WARNING_WORDS = Set.of("register", "join", "sign up", "sign",
      "link", "retweet", "receive", "bonus");
  public static final int RECENTLY_IN_HOURS = 240;
  public static final int MIN_FOLLOWERS = 10;
  public static final int MAX_FOLLOWINGS = 100;
  public static final int MAX_LIKED_TWEETS = 1000;
  public static final int MAX_ISSUED_TWEETS = 1000;
  public static final int MIN_SEC_BETWEEN_TWEETS = 5;

  // criteria weights
  public static final int PHOTO_INCREASE_WEIGHT = 5;
  public static final int PHOTO_DECREASE_WEIGHT = 2;
  public static final int BACKGROUND_INCREASE_WEIGHT = 4;
  public static final int CREATING_TIME_INCREASE_WEIGHT = 2;
  public static final int ACC_NAME_INCREASE_WEIGHT = 3;
  public static final int DESCRIPTION_INCREASE_WEIGHT = 4;
  public static final int FOLLOWERS_INCREASE_WEIGHT = 2;
  public static final int TWEET_CONTENT_INCREASE_WEIGHT = 4;
  public static final int FAST_TWEETING_INCREASE_WEIGHT = 5;
}
