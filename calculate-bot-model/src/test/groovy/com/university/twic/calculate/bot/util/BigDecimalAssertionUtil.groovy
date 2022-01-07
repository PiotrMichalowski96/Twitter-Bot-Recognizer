package com.university.twic.calculate.bot.util

import lombok.experimental.UtilityClass
import org.assertj.core.api.Assertions

@UtilityClass
class BigDecimalAssertionUtil {

    static void assertBigDecimal(BigDecimal actualValue, BigDecimal expectedValue, BigDecimal accuracy) {
        BigDecimal compare = actualValue.subtract(expectedValue)
        BigDecimal compareAbsolute = compare.abs()
        Assertions.assertThat(compareAbsolute <=> accuracy).isNotPositive()
    }
}
