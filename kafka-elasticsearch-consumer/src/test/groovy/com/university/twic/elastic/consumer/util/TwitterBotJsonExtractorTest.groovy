package com.university.twic.elastic.consumer.util

import org.apache.commons.io.FileUtils
import org.springframework.util.ResourceUtils
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

import java.nio.charset.StandardCharsets

@Title("Twitter Bot Json extractor unit tests")
@Narrative("It has to extract id form twitter json message")
class TwitterBotJsonExtractorTest extends Specification {

    def "should correctly extract tweet id from twitter json text message"() {
        given:
        String twitterBotJson = readJsonAsString('twitterBot.json')

        when:
        String resultId = TwitterBotJsonExtractor.extractIdFromTwitterBotModel(twitterBotJson)

        then:
        resultId == '1361708836935987201'
    }

    private static String readJsonAsString(String fileName) throws IOException {
        String path = 'classpath:' + fileName
        return FileUtils.readFileToString(ResourceUtils.getFile(path), StandardCharsets.UTF_8)
    }
}
