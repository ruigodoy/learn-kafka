package com.learnkafka.repository;

import com.learnkafka.entity.LibraryEvent;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@EnableScan
public interface LibraryEventsRepository extends CrudRepository<LibraryEvent, Integer> {

    Optional<LibraryEvent> findById(Integer id);
}