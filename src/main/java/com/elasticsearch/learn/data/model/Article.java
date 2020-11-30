package com.elasticsearch.learn.data.model;

import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;
import static org.springframework.data.elasticsearch.annotations.FieldType.Nested;
import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

@Data
@NoArgsConstructor
@Document(indexName = "blog")
public class Article {

  /**
   * id - generated as UUIDs
   */
  @Id
  private String id = UUID.randomUUID().toString();

  @MultiField(mainField = @Field(type = Text, fielddata = true), otherFields = { @InnerField(suffix = "verbatim", type = Keyword) })
  private String title;

  @Field(type = Nested, includeInParent = true)
  private List<Author> authors;

  @Field(type = Keyword)
  private String[] tags;

  public Article(String title) {
    this.title = title;
  }

  @Override
  public String toString() {
    return "Article{" + "id='" + id + '\'' + ", title='" + title + '\'' + ", authors=" + authors + ", tags=" + Arrays
      .toString(tags) + '}';
  }

}
