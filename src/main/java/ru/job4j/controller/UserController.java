package ru.job4j.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import ru.job4j.model.Person;
import ru.job4j.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final ObjectMapper objectMapper;

    private PersonService users;

    private BCryptPasswordEncoder encoder;

    private static final Logger LOGGER =
            LoggerFactory.getLogger(UserController.class.getSimpleName());

    public UserController(PersonService users, ObjectMapper objectMapper,
                          BCryptPasswordEncoder encoder) {
        this.users = users;
        this.objectMapper = objectMapper;
        this.encoder = encoder;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@RequestBody Person person) {
        if (person == null) {
            throw new NullPointerException("Person cannot be null");
        }
        person.setPassword(encoder.encode(person.getPassword()));
        var username = person.getLogin();
        var password = person.getPassword();
        if (username == null || password == null) {
            throw new NullPointerException("Username and password mustn't be empty");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        if (users.create(person).isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public List<Person> findAll() {
        return users.findAll();
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
        LOGGER.error(e.getLocalizedMessage());
    }
}