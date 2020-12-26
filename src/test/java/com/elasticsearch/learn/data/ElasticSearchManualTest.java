package com.elasticsearch.learn.data;



import static java.util.Arrays.asList;
import static org.elasticsearch.index.query.Operator.AND;
import static org.elasticsearch.index.query.QueryBuilders.fuzzyQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.regexpQuery;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


import com.elasticsearch.learn.SpringContextManualTest;
import com.elasticsearch.learn.data.config.Config;
import com.elasticsearch.learn.data.model.Article;
import com.elasticsearch.learn.data.model.Author;
import com.elasticsearch.learn.data.repository.ArticleRepository;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.test.context.ContextConfiguration;

/**
 * This Manual test requires: Elasticsearch instance running on localhost:9200.
 *
 * see read me for dockercompose for manual connection to ES
 */
@ContextConfiguration(classes = Config.class)
@SpringBootTest(classes = SpringContextManualTest.class)
public class ElasticSearchManualTest {

  @Autowired
  private ElasticsearchRestTemplate elasticsearchTemplate;

  @Autowired
  private RestHighLevelClient client;

  @Autowired
  private ArticleRepository articleRepository;

  private final Author johnSmith = new Author("John Smith");
  private final Author johnDoe = new Author("John Doe");

  @BeforeEach
  public void before() {
    Article article = new Article("Spring Data Elasticsearch");
    article.setAuthors(asList(johnSmith, johnDoe));
    article.setTags(new String[]{"elasticsearch", "spring data"});
    articleRepository.save(article);

    article = new Article("Search engines");
    article.setAuthors(Collections.singletonList(johnDoe));
    article.setTags(new String[]{"search engines", "tutorial"});
    articleRepository.save(article);

    article = new Article("Second Article About Elasticsearch");
    article.setAuthors(Collections.singletonList(johnSmith));
    article.setTags(new String[]{"elasticsearch", "spring data"});
    articleRepository.save(article);

    article = new Article("Elasticsearch Tutorial");
    article.setAuthors(Collections.singletonList(johnDoe));
    article.setTags(new String[]{"elasticsearch"});
    articleRepository.save(article);
  }

  @AfterEach
  public void after() throws IOException {
    articleRepository.deleteAll();
    client.close();
  }

  @Test
  public void givenArticleService_findAll() {
    articleRepository.findAll().forEach( article -> {
      assertNotNull(article);
      System.out.println(article);
    });
  }
  @Test
  public void givenArticleService_whenSaveArticle_thenIdIsAssigned() {
    final List<Author> authors = asList(new Author("John Smith"), johnDoe);

    Article article = new Article("Making Search Elastic");
    article.setAuthors(authors);

    article = articleRepository.save(article);
    assertNotNull(article.getId());
  }

  @Test
  public void givenPersistedArticles_whenSearchByAuthorsName_thenRightFound() {
    final Page<Article> articleByAuthorName = articleRepository.findByAuthorsName(johnSmith.getName(), PageRequest.of(0, 10));

    assertEquals(2L, articleByAuthorName.getTotalElements());
  }

  @Test
  public void givenCustomQuery_whenSearchByAuthorsName_thenArticleIsFound() {
    final Page<Article> articleByAuthorName = articleRepository.findByAuthorsNameUsingCustomQuery("Smith", PageRequest.of(0, 10));
    assertEquals(2L, articleByAuthorName.getTotalElements());
  }

  @Test
  public void givenTagFilterQuery_whenSearchByTag_thenArticleIsFound() {
    final Page<Article> articleByAuthorName = articleRepository.findByFilteredTagQuery("elasticsearch", PageRequest.of(0, 10));
    assertEquals(3L, articleByAuthorName.getTotalElements());
  }

  @Test
  public void givenTagFilterQuery_whenSearchByAuthorsName_thenArticleIsFound() {
    final Page<Article> articleByAuthorName = articleRepository.findByAuthorsNameAndFilteredTagQuery("Doe", "elasticsearch", PageRequest.of(0, 10));
    assertEquals(2L, articleByAuthorName.getTotalElements());
  }

  @Test
  public void givenPersistedArticles_whenUseRegexQuery_thenRightArticlesFound() {
    final Query searchQuery = new NativeSearchQueryBuilder().withFilter(regexpQuery("title", ".*data.*"))
      .build();

    final SearchHits<Article> articles = elasticsearchTemplate.search(searchQuery, Article.class, IndexCoordinates.of("blog"));

    assertEquals(1, articles.getTotalHits());
  }

  @Test
  public void givenSavedDoc_whenTitleUpdated_thenCouldFindByUpdatedTitle() {
    final Query searchQuery = new NativeSearchQueryBuilder().withQuery(fuzzyQuery("title", "serch"))
      .build();
    final SearchHits<Article> articles = elasticsearchTemplate.search(searchQuery, Article.class, IndexCoordinates.of("blog"));

    assertEquals(1, articles.getTotalHits());

    final Article article = articles.getSearchHit(0)
      .getContent();
    final String newTitle = "Getting started with Search Engines";
    article.setTitle(newTitle);
    articleRepository.save(article);

    assertEquals(newTitle, articleRepository.findById(article.getId())
      .get()
      .getTitle());
  }

  @Test
  public void givenSavedDoc_whenDelete_thenRemovedFromIndex() {
    final String articleTitle = "Spring Data Elasticsearch";

    final Query searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("title", articleTitle).minimumShouldMatch("75%"))
      .build();
    final SearchHits<Article> articles = elasticsearchTemplate.search(searchQuery, Article.class, IndexCoordinates.of("blog"));

    assertEquals(1, articles.getTotalHits());
    final long count = articleRepository.count();

    articleRepository.delete(articles.getSearchHit(0)
      .getContent());

    assertEquals(count - 1, articleRepository.count());
  }

  @Test
  public void givenSavedDoc_whenOneTermMatches_thenFindByTitle() {
    final Query searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("title", "Search engines").operator(AND))
      .build();
    final SearchHits<Article> articles = elasticsearchTemplate.search(searchQuery, Article.class, IndexCoordinates.of("blog"));
    assertEquals(1, articles.getTotalHits());
  }
}

