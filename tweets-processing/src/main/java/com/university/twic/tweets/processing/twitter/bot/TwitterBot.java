package com.university.twic.tweets.processing.twitter.bot;

import com.university.twic.tweets.processing.twitter.model.TwitterUser;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TwitterBot {
  private TwitterUser twitterUser;
  private String lastTweetContent;
  private LocalDateTime lastTweetDateTime;
  private BigDecimal botProbability;
}
