package com.university.twic.tweets.processing.util

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.university.twic.calculate.bot.model.TwitterBot
import groovy.util.logging.Slf4j
import lombok.experimental.UtilityClass
import org.apache.commons.io.FileUtils
import org.springframework.util.ResourceUtils

import java.nio.charset.StandardCharsets

@Slf4j
@UtilityClass
class JsonReaderUtil {

    static String readFileAsString(String fileName) throws IOException {
        String path = 'classpath:' + fileName
        return FileUtils.readFileToString(ResourceUtils.getFile(path), StandardCharsets.UTF_8)
    }

    static TwitterBot extractTwitterBotFromJson(String jsonFilePath) {
        String json = readFileAsString(jsonFilePath)
        try {
            return new Gson().fromJson(json, TwitterBot.class)
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            log.warn("Couldn't convert Json File to TwitterUser object, Json content: {}", json)
            return null
        }
    }
}
