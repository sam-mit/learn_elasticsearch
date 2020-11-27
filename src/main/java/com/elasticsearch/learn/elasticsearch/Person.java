package com.elasticsearch.learn.elasticsearch;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
  private int age;

  private String fullName;

  private Date dateOfBirth;
  @Override
  public String toString() {
    return "Person [age=" + age + ", fullName=" + fullName + ", dateOfBirth=" + dateOfBirth + "]";
  }

}
