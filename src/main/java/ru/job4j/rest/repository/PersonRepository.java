package ru.job4j.rest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.rest.domain.Person;

@Repository
public interface PersonRepository extends CrudRepository<Person, Integer> {
}