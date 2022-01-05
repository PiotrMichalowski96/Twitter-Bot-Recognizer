package com.university.twic.calculate.bot.service;

import com.university.twic.calculate.bot.service.twitter.ModelParameter;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "calcbot")
@Data
public class CalculateBotProperties {

  private BigDecimal initialBotProbability;
  private Set<String> warningWords;
  private Map<ModelParameter, Integer> modelBotFactorsMap;
}
