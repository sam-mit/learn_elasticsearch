package com.elasticsearch.learn.data.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.elasticsearch.learn.data.repository")
@ComponentScan(basePackages = { "com.elasticsearch.learn.data" })
public class Config {

  @Bean
  RestHighLevelClient client() {
    ClientConfiguration clientConfiguration = ClientConfiguration.builder()
      .connectedTo("localhost:9200")
      .build();

    return RestClients.create(clientConfiguration)
      .rest();
  }

  @Bean
  public ElasticsearchOperations elasticsearchTemplate() {
    return new ElasticsearchRestTemplate(client());
  }
}
