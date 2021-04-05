package com.university.twic.tweets.processing.twitter.bot.math;

import static com.university.twic.tweets.processing.util.BigDecimalAssertionUtil.assertBigDecimal;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class ProbabilityTest {

  @Test
  public void shouldCalculateCorrectIncreasedProbabilityValue() {
    //given
    BigDecimal probability = BigDecimal.valueOf(0.3);
    BigDecimal expectedResultProbability = BigDecimal.valueOf(0.37);

    //when
    BigDecimal result = Probability.increaseProbability(probability);

    //then
    assertBigDecimal(result, expectedResultProbability, BigDecimal.valueOf(0.001));
  }

  @Test
  public void shouldCalculateCorrectDecreaseProbabilityValue() {
    //given
    BigDecimal probability = BigDecimal.valueOf(0.5);
    BigDecimal expectedResultProbability = BigDecimal.valueOf(0.4444444444444444444444444444444444);

    //when
    BigDecimal result = Probability.decreaseProbability(probability);

    //then
    assertBigDecimal(result, expectedResultProbability, BigDecimal.valueOf(0.001));
  }

  @Test
  public void shouldPerformIncreasingManyTimes() {
    //given
    BigDecimal probability = BigDecimal.valueOf(0.5);
    int times = 3;
    BigDecimal expectedResultProbability = BigDecimal.valueOf(0.6355);

    //when
    BigDecimal result = Probability.multipleIncreaseProbability(probability, times);

    //then
    assertBigDecimal(result, expectedResultProbability, BigDecimal.valueOf(0.001));
  }

}
