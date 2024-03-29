package com.learnkafka.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.entity.LibraryEvent;
import com.learnkafka.repository.LibraryEventsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class LibraryEventsService {

    @Autowired
    private LibraryEventsRepository libraryEventsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;


    public void processLibraryEvent(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        LibraryEvent libraryEvent = objectMapper.readValue(consumerRecord.value(), LibraryEvent.class);
        log.info("libraryEvent : {} ", libraryEvent);

        if (libraryEvent.getLibraryEventId() != null && libraryEvent.getLibraryEventId() == 0) {
            throw new RecoverableDataAccessException("Temporary Network Issue");
        }

        save(libraryEvent);
    }

    public void save(LibraryEvent libraryEvent) {
        libraryEventsRepository.save(libraryEvent);
        log.info("Successfully Persisted the libary Event {} ", libraryEvent);
    }

    public void handleRecovery(ConsumerRecord<Integer, String> record) {
        Integer key = record.key();
        String message = record.value();

        kafkaTemplate.sendDefault(key, message);

        ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.sendDefault(key, message);
        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key, message, ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key, message, result);
            }
        });
    }

    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Error Sending the Message and the exception is {}", ex.getMessage());
        try {
            throw ex;
        } catch (Throwable throwable) {
            log.error("Error in OnFailure: {}", throwable.getMessage());
        }
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("Message Sent SuccessFully for the key : {} and the value is {} , partition is {}", key, value, result.getRecordMetadata().partition());
    }

    public List<LibraryEvent> getLibrary(){
        return (List<LibraryEvent>) libraryEventsRepository.findAll();
    }

    public ResponseEntity<?> hasBook(Integer id) throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("http://localhost:8082/v1/bookevent/" + id);
        CloseableHttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity);

        if(response.getStatusLine().getStatusCode() != 200){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }else{

            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
    }
}