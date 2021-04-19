package com.university.twic.elastic.consumer.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

public class TwitterBotJsonExtractorTest {

  @Test
  public void shouldExtractCorrectIdFromJson() throws IOException {
    //given
    String twitterBotJson = readJsonAsString("twitterBot.json");
    String expectedId = "1361708836935987201";

    //when
    String resultId = TwitterBotJsonExtractor.extractIdFromTwitterBotModel(twitterBotJson);

    //then
    assertThat(resultId).isEqualTo(expectedId);
  }

  private String readJsonAsString(String fileName) throws IOException {
    String path = "classpath:" + fileName;
    return FileUtils.readFileToString(ResourceUtils.getFile(path), StandardCharsets.UTF_8);
  }
}
