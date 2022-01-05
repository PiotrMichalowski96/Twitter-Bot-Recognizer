package com.university.twic.calculate.bot.service;

import com.university.twic.calculate.bot.model.Tweet;
import com.university.twic.calculate.bot.model.TwitterBot;
import com.university.twic.calculate.bot.service.twitter.CalculateTwitterBotModule;
import com.university.twic.calculate.bot.service.twitter.ModelParameter;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = CalculateBotProperties.class)
public class CalculateBotProcessorConfig {

  @Bean
  public CalculateBotProcessorService<TwitterBot, Tweet> calculateBotProcessorService(CalculateBotProperties calculateBotProperties) {
    return new CalculateBotProcessorService<>(initializeCreator(calculateBotProperties));
  }

  private CalculateBotModuleCreator<TwitterBot, Tweet> initializeCreator(CalculateBotProperties calculateBotProperties) {
    BigDecimal initialBotProbability = calculateBotProperties.getInitialBotProbability();
    Map<ModelParameter, Integer> modelBotFactorsMap = calculateBotProperties.getModelBotFactorsMap();
    Set<String> warningWords = calculateBotProperties.getWarningWords();
    return previousBotModel -> previousBotModel == null ?
        new CalculateTwitterBotModule(modelBotFactorsMap, warningWords,initialBotProbability) :
        new CalculateTwitterBotModule(previousBotModel, modelBotFactorsMap, warningWords, initialBotProbability);
  }
}
