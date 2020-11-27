package com.elasticsearch.learn.data.model;

import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@NoArgsConstructor
public class Author {

  @Field(type = Text)
  private String name;

  public Author(String name) {
    this.name = name;
  }
  @Override
  public String toString() {
    return "Author{" + "name='" + name + '\'' + '}';
  }
}
