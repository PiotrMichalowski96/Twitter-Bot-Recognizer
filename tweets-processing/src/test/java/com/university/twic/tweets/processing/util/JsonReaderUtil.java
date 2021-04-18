package com.university.twic.tweets.processing.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

@UtilityClass
public class JsonReaderUtil {

  public static String readFileAsString(String fileName) throws IOException {
    String path = "classpath:" + fileName;
    return FileUtils.readFileToString(ResourceUtils.getFile(path), StandardCharsets.UTF_8);
  }
}
