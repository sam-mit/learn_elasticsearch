package com.elasticsearch.learn.data.config;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
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

//  @Bean
//  RestHighLevelClient client() {
//    ClientConfiguration clientConfiguration = ClientConfiguration.builder()
//      .connectedTo("vpc-dal-dev1-blue-spaebqqcj62mlv6vf2hrpbpa3e.eu-west-1.es.amazonaws.com:9200")
//      .build();
//
//    return RestClients.create(clientConfiguration)
//      .rest();
//  }

  @Bean
  public RestHighLevelClient defaultHighLevelClient() {
    try {
      System.out.println("creating Elasticsearch high level client :" +  url.toString());
      InetAddress address = InetAddress.getByName(url.getHost());
      RestClientBuilder builder = RestClient
        .builder(new HttpHost(address, address.getHostName(), url.getPort(), url.getProtocol()));
      builder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(30000).setSocketTimeout(60000));
//      builder.setMaxRetryTimeoutMillis(60000);
      return new RestHighLevelClient(builder);
    } catch (final UnknownHostException | MalformedURLException err) {
      throw new IllegalStateException("Unable to create high level elasticsearch rest client.", err);
    }
  }

  @Bean
  public ElasticsearchOperations elasticsearchTemplate() {
    return new ElasticsearchRestTemplate(defaultHighLevelClient());
  }
}
