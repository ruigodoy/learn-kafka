package com.learnkafka.entity;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;


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
    private String initialDate;

    @DynamoDBAttribute
    private String finalDate;

    @DynamoDBAttribute
    private Integer bookId;

}