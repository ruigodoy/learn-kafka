package com.learnkafka.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.entity.BookEvent;
import com.learnkafka.repository.BookEventsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BookEventsService {

    @Autowired
    private BookEventsRepository bookEventsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;


    public void processLibraryEvent(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        BookEvent bookEvent = objectMapper.readValue(consumerRecord.value(), BookEvent.class);
        log.info("libraryEvent TESTETESTE: {} ", bookEvent);

        if (bookEvent.getBookEventId() != null && bookEvent.getBookEventId() == 0) {
            throw new RecoverableDataAccessException("Temporary Network Issue");
        }

        switch (bookEvent.getBookEventType()) {
            case NEW:
                log.info("libraryEvent TESTE: {} ", bookEvent);
                save(bookEvent);
                break;
            case UPDATE:
                validate(bookEvent);
                save(bookEvent);
                break;
            default:
                log.info("Invalid Library Event Type");
        }
    }

    private void validate(BookEvent bookEvent) {
        if (bookEvent.getBookEventId() == null) {
            throw new IllegalArgumentException("Library Event Id is missing");
        }

        Optional<BookEvent> libraryEventOptional = bookEventsRepository.findById(bookEvent.getBookEventId());
        if (!libraryEventOptional.isPresent()) {
            throw new IllegalArgumentException("Not a valid library Event");
        }
        log.info("Validation is successful for the library Event : {} ", libraryEventOptional.get());
    }

    private void save(BookEvent bookEvent) {
        log.info("entrou save()");
        bookEventsRepository.save(bookEvent);
        log.info("Successfully Persisted the libary Event {} ", bookEvent);
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

    public List<BookEvent> getLibrary(){
        return (List<BookEvent>) bookEventsRepository.findAll();
    }

    public Optional<BookEvent> getBookById(Integer id){
        Optional<BookEvent> book = bookEventsRepository.findById(id);
        if(book.isPresent()){
            book.get().getBook().setAvailable(Boolean.FALSE);
            return Optional.of(bookEventsRepository.save(book.get()));
        }else{
            return Optional.empty();
        }
    }
}