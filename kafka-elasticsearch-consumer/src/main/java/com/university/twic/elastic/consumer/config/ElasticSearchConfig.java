package com.university.twic.elastic.consumer.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {

  @Value("${elasticsearch.hostname}")
  private String hostname;
  @Value("${elasticsearch.username}")
  private String username;
  @Value("${elasticsearch.password}")
  private String password;

  @Bean(name = "ElasticSearch-Client")
  public RestHighLevelClient createClient() {

    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

    RestClientBuilder builder = RestClient.builder(
        new HttpHost(hostname, 443, "https"))
        .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder
            .setDefaultCredentialsProvider(credentialsProvider));

    return new RestHighLevelClient(builder);
  }
}
