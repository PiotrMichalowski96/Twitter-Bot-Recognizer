package com.university.twic.tweets.processing.twitter.model;

import java.time.LocalDateTime;
import lombok.Value;

@Value
public class Tweet {
  TwitterUser user;
  String text;
  LocalDateTime createdTime;
}