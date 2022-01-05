package com.university.twic.calculate.bot.math;

import java.math.BigDecimal;
import java.math.MathContext;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Probability {

  private static final BigDecimal PARAMETER = BigDecimal.valueOf(0.9);

  /**
   * This method calculate new CDF result of geometric distribution based on previous value
   *
   * @param previousProbability it is a previous value of CDF function
   * @return new value of CDF function of geometric distribution
   */
  public static BigDecimal increaseProbability(BigDecimal previousProbability) {
    BigDecimal difference = previousProbability.subtract(BigDecimal.ONE);
    BigDecimal product = difference.multiply(PARAMETER);
    return product.add(BigDecimal.ONE);
  }

  public static BigDecimal multipleIncreaseProbability(BigDecimal probability, int times) {
    for(int i = 0; i < times; i++) {
      probability = increaseProbability(probability);
    }
    return probability;
  }

  /**
   * This method calculate previous CDF value of geometric distribution based on actual value
   *
   * @param actualProbability it is a actual value of CDF function
   * @return previous value of CDF function of geometric distribution
   */
  public static BigDecimal decreaseProbability(BigDecimal actualProbability) {
    BigDecimal difference = actualProbability.subtract(BigDecimal.ONE);
    BigDecimal quotient = difference.divide(PARAMETER, MathContext.DECIMAL128);
    return quotient.add(BigDecimal.ONE);
  }

  public static BigDecimal multipleDecreaseProbability(BigDecimal probability, int times) {
    for(int i = 0; i < times; i++) {
      probability = decreaseProbability(probability);
    }
    return probability;
  }
}
