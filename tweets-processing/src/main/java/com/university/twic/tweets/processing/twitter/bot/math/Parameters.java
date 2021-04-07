package com.university.twic.tweets.processing.twitter.bot.math;

import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Parameters {

  public static final Set<String> WARNING_WORDS = Set.of("register", "join", "sign up");
  public static final int RECENTLY_IN_HOURS = 240;
  public static final int MIN_FOLLOWERS = 10;
  public static final int MAX_FOLLOWINGS = 100;
  public static final int MAX_LIKED_TWEETS = 1000;
  public static final int MAX_ISSUED_TWEETS = 1000;
  public static final int MIN_SEC_BETWEEN_TWEETS = 5;

}
