package com.university.twic.elastic.consumer.service;

import static com.university.twic.elastic.consumer.util.TwitterBotJsonExtractor.extractIdFromTwitterBotModel;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageListener {

  @Qualifier("ElasticSearch-Client")
  private final RestHighLevelClient client;

  @Value("${elasticsearch.index}")
  private final String indexName;

  @KafkaListener(topics = "${kafka.topic}", containerFactory = "kafkaListenerContainerFactory")
  public void twitterBotListener(String twitterBotJson) {
    logger.info(twitterBotJson);

    BulkRequest bulkRequest = new BulkRequest();

    String id = extractIdFromTwitterBotModel(twitterBotJson);

    IndexRequest indexRequest = new IndexRequest(indexName)
        .id(id)
        .source(twitterBotJson, XContentType.JSON);

    bulkRequest.add(indexRequest);

    try {
      BulkResponse bulkItemResponses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
    } catch (IOException e) {
      e.printStackTrace();
      logger.warn("Client couldn't perform bulk operation, bulk={}", bulkRequest.toString());
    }
  }
}
