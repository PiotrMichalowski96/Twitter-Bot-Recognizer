package com.university.twic.calculate.bot.service;

/**
 * Interface for calculating new bot model data based on previous bot model state and received new event
 */
public interface CalculateBotModule<T, U> {
  T initializeBotModel();
  T calculateBotModel(U newEvent);
}
