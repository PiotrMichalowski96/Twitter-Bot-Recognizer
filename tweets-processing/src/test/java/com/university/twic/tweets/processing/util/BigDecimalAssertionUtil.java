package com.university.twic.tweets.processing.util;

import java.math.BigDecimal;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

@UtilityClass
public class BigDecimalAssertionUtil {

  public static void assertBigDecimal(BigDecimal actualValue, BigDecimal expectedValue, BigDecimal accuracy) {
    BigDecimal compare = actualValue.subtract(expectedValue);
    BigDecimal compareAbsolute = compare.abs();

    Assertions.assertThat(compareAbsolute.compareTo(accuracy)).isNotPositive();
  }
}
