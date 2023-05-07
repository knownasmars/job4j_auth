package ru.job4j.controller;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import ru.job4j.model.Operation;
import ru.job4j.model.Person;
import ru.job4j.dto.PersonDTO;
import ru.job4j.service.PersonService;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@RestController
@RequestMapping("/person")
@AllArgsConstructor
public class PersonController {

    private final PersonService persons;

    private BCryptPasswordEncoder encoder;

    @GetMapping("/")
    public ResponseEntity<List<Person>> findAll() {
        return ResponseEntity.ok(persons.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = persons.findById(id);
        return new ResponseEntity<>(
                person.orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Person is not found."
                        )
                ),
                HttpStatus.OK
        );
    }

    @PostMapping("/")
    @Validated(Operation.OnCreate.class)
    public ResponseEntity<Person> create(@Valid @RequestBody Person person) {
        person.setPassword(encoder.encode(person.getPassword()));
        return new ResponseEntity<>(
                this.persons.create(person).get(),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@Valid @RequestBody Person person) {
        person.setPassword(encoder.encode(person.getPassword()));
        if (persons.save(person)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        if (this.persons.delete(person)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PatchMapping("/password")
    public Person newPassword(@Valid @RequestBody PersonDTO personDTO)
            throws InvocationTargetException, IllegalAccessException {
        var personOptional = persons.findById(personDTO.getId());
        if (personOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        var person = personOptional.get();
        person.setPassword(encoder.encode(personDTO.getPassword()));
        persons.save(person);
        return person;
    }
}