package com.university.twic.tweets.processing.twitter.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.junit.jupiter.api.Test;

public class TwitterDateTimeConverterTest {

  @Test
  public void shouldConvertStringToCorrectDate() {
    //given
    String dateTimeText = "Mon May 13 06:13:17 +0000 2019";
    LocalDateTime expectedDateTime = LocalDateTime.of(2019, 5, 13, 6, 13, 17);

    //when
    LocalDateTime resultDateTime = TwitterDateTimeConverter.convertTwitterDateTime(dateTimeText);

    //then
    assertThat(resultDateTime).isEqualTo(expectedDateTime);
  }

  @Test
  public void shouldThrowExceptionIfWrongFormatText() {
    //given
    String wrongDateTimeText = "Monday May 13 06:13:17 +0000 2019";

    //whenThen
    assertThatThrownBy(() -> TwitterDateTimeConverter.convertTwitterDateTime(wrongDateTimeText))
      .isInstanceOf(DateTimeParseException.class);
  }
}
