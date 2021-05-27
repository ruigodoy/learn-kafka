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
@DynamoDBTable(tableName = "library_event")
public class LibraryEvent {

    @DynamoDBHashKey
    private Integer libraryEventId;

    @DynamoDBAttribute
    @DynamoDBTypeConvertedEnum
    private LibraryEventType libraryEventType;

    @DynamoDBAttribute
    private Book book;

}