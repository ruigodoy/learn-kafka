package com.learnkafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.domain.BookEvent;
import com.learnkafka.domain.BookEventType;
import com.learnkafka.producer.LibraryEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class BookEventsController {

    @Autowired
    LibraryEventProducer libraryEventProducer;

    @GetMapping("/v1/bookevent")
    public ResponseEntity<?> getBookEvent() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("http://localhost:8082/v1/bookevent");
        CloseableHttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/v1/bookevent")
    public ResponseEntity<BookEvent> postBookEvent(@RequestBody @Valid BookEvent bookEvent) throws JsonProcessingException, ExecutionException, InterruptedException {

        bookEvent.setBookEventType(BookEventType.NEW);
        bookEvent.getBook().setAvailable(Boolean.TRUE);
        libraryEventProducer.sendBookEventApproach(bookEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookEvent);
    }

    @PutMapping("/v1/bookevent")
    public ResponseEntity<?> putLibraryEvent(@RequestBody @Valid BookEvent bookEvent) throws JsonProcessingException, ExecutionException, InterruptedException {
        if(bookEvent.getBookEventId()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the LibraryEventId");
        }

        bookEvent.setBookEventType(BookEventType.UPDATE);
        libraryEventProducer.sendBookEventApproach(bookEvent);
        return ResponseEntity.status(HttpStatus.OK).body(bookEvent);
    }
}
