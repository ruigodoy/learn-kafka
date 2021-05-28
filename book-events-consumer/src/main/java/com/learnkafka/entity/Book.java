package com.learnkafka.entity;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
@EqualsAndHashCode
@DynamoDBDocument
public class Book {

    private Integer bookId;

    private String bookName;

    private String bookAuthor;

    private Boolean available;

}