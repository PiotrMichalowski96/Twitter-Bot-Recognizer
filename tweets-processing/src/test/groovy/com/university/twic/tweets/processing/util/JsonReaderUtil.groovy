package com.university.twic.tweets.processing.util

import lombok.experimental.UtilityClass
import org.apache.commons.io.FileUtils
import org.springframework.util.ResourceUtils

import java.nio.charset.StandardCharsets

@UtilityClass
class JsonReaderUtil {

    static String readFileAsString(String fileName) throws IOException {
        String path = 'classpath:' + fileName
        return FileUtils.readFileToString(ResourceUtils.getFile(path), StandardCharsets.UTF_8)
    }
}
