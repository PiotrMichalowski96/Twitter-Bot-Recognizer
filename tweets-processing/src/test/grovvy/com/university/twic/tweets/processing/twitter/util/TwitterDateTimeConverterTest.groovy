package com.university.twic.tweets.processing.twitter.util

import spock.lang.Specification

import java.time.format.DateTimeParseException

import static com.university.twic.tweets.processing.twitter.util.TwitterDateTimeConverter.convertTwitterDateTime;
import spock.lang.Narrative
import spock.lang.Title

import java.time.LocalDateTime

@Title("Tests for twitter date time format converter")
@Narrative("It has to convert twitter date time")
class TwitterDateTimeConverterTest extends Specification{

    def "should convert string to correct date"() {
        given:
        String dateTimeText = 'Mon May 13 06:13:17 +0000 2019'

        when:
        LocalDateTime resultDateTime = convertTwitterDateTime(dateTimeText)

        then:
        resultDateTime == LocalDateTime.of(2019, 5, 13, 6, 13, 17)
    }

    def "should throw exception if wrong format text"() {
        given:
        String wrongDateTimeText = 'Monday May 13 06:13:17 +0000 2019'

        when:
        convertTwitterDateTime(wrongDateTimeText)

        then:
        thrown(DateTimeParseException)
    }
}
