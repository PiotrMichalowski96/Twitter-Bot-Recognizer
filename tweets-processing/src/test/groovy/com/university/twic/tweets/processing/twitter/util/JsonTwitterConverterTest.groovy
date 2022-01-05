package com.university.twic.tweets.processing.twitter.util

import com.university.twic.calculate.bot.model.Tweet
import com.university.twic.calculate.bot.model.TwitterUser
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

import java.time.LocalDateTime

import static com.university.twic.tweets.processing.util.JsonReaderUtil.readFileAsString
import static org.assertj.core.api.Assertions.assertThat

@Title("Tests for twitter message json converter")
@Narrative("It has to convert twitter json message to Java object")
class JsonTwitterConverterTest extends Specification {

    def "should extract user from tweet"() {
        given:
        String sampleTweetJson = readFileAsString('sampleTweet.json')
        TwitterUser expectedTwitterUser = getExpectedTwitterUser()

        when:
        TwitterUser resultTwitterUser = JsonTwitterConverter.extractUserFromTweetJson(sampleTweetJson)

        then:
        resultTwitterUser.properties == expectedTwitterUser.properties
    }

    def "should return twitter with null fields if wrong json tweet"() {
        given:
        String wrongTweetJson = '{user:{}}'

        when:
        TwitterUser resultTwitterUser = JsonTwitterConverter.extractUserFromTweetJson(wrongTweetJson)

        then:
        assertThat(resultTwitterUser).hasAllNullFieldsOrProperties()
    }

    def "should extract correct tweet"() {
        given:
        String sampleTweetJson = readFileAsString('sampleTweet.json')
        Tweet expectedTweet = getExpectedTweet()

        when:
        Tweet resultTweet = JsonTwitterConverter.extractTweetFromJson(sampleTweetJson)

        then:
        resultTweet.user.properties == expectedTweet.user.properties
        resultTweet.text == expectedTweet.text
        resultTweet.createdTime == expectedTweet.createdTime
    }

    private static TwitterUser getExpectedTwitterUser() {
        return TwitterUser.builder()
                .id(1127819079744729088L)
                .name('Airdropcu90')
                .screenName('airdropcu90')
                .verified(false)
                .followersCount(27L)
                .friendsCount(347L)
                .favouritesCount(264L)
                .statusesCount(641L)
                .createdAt('Mon May 13 06:13:17 +0000 2019')
                .defaultProfile(true)
                .defaultProfileImage(false)
                .build();
    }

    private static Tweet getExpectedTweet() {
        TwitterUser expectedTwitterUser = getExpectedTwitterUser()
        String expectedText = 'RT @AirdropStario: \ud83d\udca7Twinci Airdrop \ud83d\udca7\n\n\ud83c\udfc6 Task:          \u2795 $15 worth of  TRX\n\n                            \u2795 $50 worth of TWIN\n\n\ud83d\udd1b Airdrop Link\u2026'
        LocalDateTime expectedDateTime = LocalDateTime.of(2021, 4, 2, 11, 49, 15)
        return new Tweet(expectedTwitterUser, expectedText, expectedDateTime)
    }
}
