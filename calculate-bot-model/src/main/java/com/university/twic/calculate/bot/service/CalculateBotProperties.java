package com.university.twic.calculate.bot.service;

import java.util.Map;
import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "calcbot")
@Data
public class CalculateBotProperties {

  private Set<String> warningWords;
  private Map<String, Double> modelBotFactorsMap;
}
