package ru.job4j.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.job4j.domain.Person;
import ru.job4j.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Service
@AllArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = personRepository.findByLogin(username);
        if (person == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(person.getLogin(), person.getPassword(), emptyList());
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Person create(Person person) {
        return personRepository.save(person);
    }

    public Optional<Person> findById(int id) {
        return personRepository.findById(id);
    }

    public Person findByLogin(String login) {
        return personRepository.findByLogin(login);
    }

    public boolean save(Person person) {
        var res = personRepository.findById(person.getId());
        if (res.isPresent()) {
            personRepository.save(person);
            return true;
        }
        return false;
    }

    public boolean delete(Person person) {
        var res = personRepository.findById(person.getId());
        if (res.isPresent()) {
            personRepository.delete(person);
            return true;
        }
        return false;
    }
}
