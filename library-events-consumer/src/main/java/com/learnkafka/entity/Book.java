package com.learnkafka.entity;


import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.*;

import java.io.Serializable;

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