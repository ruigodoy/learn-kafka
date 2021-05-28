package com.learnkafka.entity;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
@EqualsAndHashCode
@DynamoDBTable(tableName = "book_event")
public class BookEvent {

    @DynamoDBHashKey
    private Integer bookEventId;

    @DynamoDBAttribute
    @DynamoDBTypeConvertedEnum
    private BookEventType bookEventType;

    @DynamoDBAttribute
    private Book book;

}