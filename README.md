# Twitter-Bot-Recognizer
[![<PiotrMichalowski96>](https://circleci.com/gh/PiotrMichalowski96/Twitter-Bot-Recognizer.svg?style=svg)](https://circleci.com/gh/PiotrMichalowski96/Twitter-Bot-Recognizer)

Project for University Subject

Use case is to identify real bot accounts on Twitter

## Tech stack
Java-related technologies such as Apache Kafka, Java 11, Spring Boot, Groovy, Spock Unit tests, AssertJ, Elastic Search

## Software architecture
Project is separated into 3 microservices:
1. Kafka-Twitter-Producer is connecting to Twitter Stream API and sending Tweets in json format to Kafka topic.
2. Tweet-Processing is implemented using Kafka Streams processing - it calculates probability that account is a bot based on tweets. Twitter account bot data is sent to another Kafka topic.
3. Kafka-Elasticsearch-Consumer is reading from Kafka topic and sending Twitter account data (with bot probability) to remote ELK cluster.

![TwitterBotApp-Microservices](https://user-images.githubusercontent.com/57149032/148589991-9990e877-f96a-4012-9ca9-1658ffb7ad46.png)

## Code introduction
Code is separeted into 5 modules.

### Kafka-Twitter-Producer
It is module that represents Kafka-Twitter-Producer microservice described before. Arguments can be added with a words that received tweets have to contain.

### Tweets-Processing
It is module that represents Tweet-Processing microservice described before. This module is using another 'Calculate-bot-model' module to analyzed tweets and calculate Twitter bot account probability.

### Calculate-Bot-Model
It is module that represents library used to calculate that Twitter account is a bot. Library is implemented to the interfaces so the 'bot probability math model' can be easily replaced by different one.

### Twitter-Java-Model
It is module that contains Java model / domain classes related to Twitter - Twitter data model.

### Kafka-Elasticsearch-Consumer
It is module that represents Kafka-Elasticsearch-Consumer microservice described before.

## How to run it

### Prerequisites
Following things need to be installed: Java 11 JDK, Maven, Kafka.

### Running commands for application

At firts run Zookeeper and Kafka brokers.

Kafka-Twitter-Producer microservice:
```java -cp kafka-twitter-producer-0.0.1-SNAPSHOT.jar -Dloader.main=com.university.twic.twitter.producer.KafkaTwitterProducerApplication org.springframework.boot.loader.PropertiesLauncher <words that tweet contain>```


Tweets-Processing microservice:
```java -cp tweets-processing-0.0.1-SNAPSHOT.jar -Dloader.main=com.university.twic.tweets.processing.TweetsProcessingApplication```

Kafka-Elasticsearch-Consumer microservice:
```java -cp kafka-elasticsearch-consumer-0.0.1-SNAPSHOT.jar -Dloader.main=com.university.twic.elastic.consumer.KafkaElasticsearchConsumerApplication org.springframework.boot.loader.PropertiesLauncher```

### Example ELK query:
```/twitter-bots/_search```

```
{
  "query": {
    "bool": {
      "must": { "match_all": {} },
      "filter": {
        "range": {
          "botProbability": {
            "gte": 0.7,
            "lte": 0.8
          }
        }
      }
    }
  }
}


{
  "query": {
    "term": {
      "twitterUser.screenName":{
      	"value": <Twitter User Name>
      }
    }
  }
}
```
