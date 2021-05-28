package com.learnkafka.controller;


import com.learnkafka.entity.LibraryEvent;
import com.learnkafka.service.LibraryEventsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
public class LibraryEventsConsumerController {
    @Autowired
    LibraryEventsService libraryEventsService;

    @GetMapping("/v1/libraryevent")
    public List<LibraryEvent> getLibraryEvent() {
        return libraryEventsService.getLibrary();
    }

    @PostMapping("/v1/libraryevent")
    public ResponseEntity<?> rentBook(@RequestBody LibraryEvent libraryEvent) throws IOException {
        var hasBook = libraryEventsService.hasBook(libraryEvent.getLibraryEventId());
        if( hasBook.getStatusCode().value() != 200){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(libraryEvent);
        }else{
            libraryEventsService.save(libraryEvent);
            return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
        }
    }
}
