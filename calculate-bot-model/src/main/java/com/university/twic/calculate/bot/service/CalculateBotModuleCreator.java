package com.university.twic.calculate.bot.service;

@FunctionalInterface
public interface CalculateBotModuleCreator<T, U> {
  CalculateBotModule<T, U> createCalculateBotModule(T previousBotModel);
}
