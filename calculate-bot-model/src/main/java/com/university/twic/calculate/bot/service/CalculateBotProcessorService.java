package com.university.twic.calculate.bot.service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CalculateBotProcessorService<T, U> {

  private final CalculateBotModuleCreator<T, U> calculateBotModuleCreator;

  public T calculateBot(T previousBotModel, U newEvent) {
    CalculateBotModule<T, U> calculateBotModule = calculateBotModuleCreator.createCalculateBotModule(previousBotModel);
    return calculateBotModule.calculateBotModel(newEvent);
  }

  public T initializeBot() {
    CalculateBotModule<T, U> calculateBotModule = calculateBotModuleCreator.createCalculateBotModule(null);
    return calculateBotModule.initializeBotModel();
  }
}
