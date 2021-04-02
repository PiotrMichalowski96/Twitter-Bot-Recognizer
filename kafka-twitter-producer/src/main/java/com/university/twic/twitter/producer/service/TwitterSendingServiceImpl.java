package com.university.twic.twitter.producer.service;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.university.twic.twitter.producer.domain.twitter.TwitterProducer;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TwitterSendingServiceImpl implements TwitterSendingService {

  private final Authentication authentication;
  private final BlockingQueue<String> msgQueue;
  private final TwitterProducer twitterProducer;

  @Override
  public void startSendingTweetsProcess(List<String> searchTerms) {
    logger.info("Setup");

    Client client = createTwitterClient(msgQueue, searchTerms);

    client.connect();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      logger.info("Stopping application");
      logger.info("Shutting down client from twitter...");
      client.stop();
      logger.info("done!");
    }));

    while (!client.isDone()) {
      String msg = null;
      try {
        msg = msgQueue.poll(5, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
        client.stop();
      }
      if(msg != null) {
        logger.info(msg);
        twitterProducer.send(msg);
      }
    }
    logger.info("End of application");
  }

  private Client createTwitterClient(BlockingQueue<String> msgQueue, List<String> searchTerms) {
    Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
    StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
    hosebirdEndpoint.trackTerms(searchTerms);

    ClientBuilder builder = new ClientBuilder()
        .name("Hosebird-Client-01")
        .hosts(hosebirdHosts)
        .authentication(authentication)
        .endpoint(hosebirdEndpoint)
        .processor(new StringDelimitedProcessor(msgQueue));

    return builder.build();
  }
}
