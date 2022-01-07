package com.university.twic.calculate.bot.math

import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

import static com.university.twic.calculate.bot.util.BigDecimalAssertionUtil.assertBigDecimal

@Title("Specification for probability math functions")
@Narrative("It has to calculate probability values")
class ProbabilityTest extends Specification {

    def "should calculate correct increased probability value"() {
        given:
        BigDecimal probability = BigDecimal.valueOf(0.3)
        BigDecimal expectedResultProbability = BigDecimal.valueOf(0.37)

        when:
        BigDecimal result = Probability.increaseProbability(probability)

        then:
        assertBigDecimal(result, expectedResultProbability, BigDecimal.valueOf(0.001))
    }

    def "should calculate correct decreased probability value"() {
        given:
        BigDecimal probability = BigDecimal.valueOf(0.5)
        BigDecimal expectedResultProbability = BigDecimal.valueOf(0.4444444444444444444444444444444444)

        when:
        BigDecimal result = Probability.decreaseProbability(probability)

        then:
        assertBigDecimal(result, expectedResultProbability, BigDecimal.valueOf(0.001))
    }

    def "should perform increasing probability value many times"() {
        given:
        BigDecimal probability = BigDecimal.valueOf(0.5)
        int times = 3
        BigDecimal expectedResultProbability = BigDecimal.valueOf(0.6355)

        when:
        BigDecimal result = Probability.multipleIncreaseProbability(probability, times)

        then:
        assertBigDecimal(result, expectedResultProbability, BigDecimal.valueOf(0.001))
    }
}
