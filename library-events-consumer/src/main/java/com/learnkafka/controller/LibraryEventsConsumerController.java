package com.learnkafka.controller;

import com.learnkafka.entity.LibraryEvent;
import com.learnkafka.service.LibraryEventsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class LibraryEventsConsumerController {
    @Autowired
    LibraryEventsService libraryEventsService;

    @GetMapping("/v1/libraryevent")
    public List<LibraryEvent> getLibraryEvent()  {
        return libraryEventsService.getLibrary();
    }
}
