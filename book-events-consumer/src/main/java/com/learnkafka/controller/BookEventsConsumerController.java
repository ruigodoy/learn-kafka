package com.learnkafka.controller;

import com.learnkafka.entity.BookEvent;
import com.learnkafka.service.BookEventsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class BookEventsConsumerController {
    @Autowired
    BookEventsService bookEventsService;

    @GetMapping("/v1/bookevent")
    public List<BookEvent> getBookEvent()  {
        return bookEventsService.getLibrary();
    }


    @GetMapping("/v1/bookevent/{id}")
    public ResponseEntity<BookEvent> getBookEventById(@PathVariable Integer id)  {
        return bookEventsService.getBookById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
