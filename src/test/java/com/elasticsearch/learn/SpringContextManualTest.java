package com.elasticsearch.learn;

import com.elasticsearch.learn.data.config.Config;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This Manual test requires: Elasticsearch instance running on localhost:9200.
 *
 * Check README for docker compose elasticsearch
 */
@ContextConfiguration(classes = Config.class)
public class SpringContextManualTest {

  @Test
  public void whenSpringContextIsBootstrapped_thenNoExceptions() {
  }
}
