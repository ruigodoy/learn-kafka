package com.learnkafka.repository;

import com.learnkafka.entity.BookEvent;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@EnableScan
public interface BookEventsRepository extends CrudRepository<BookEvent, Integer> {

    Optional<BookEvent> findById(Integer id);
}