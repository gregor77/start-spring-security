package com.rhyno.startsecurity.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/v1/user")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/{id}")
    public User getUser(@PathVariable long id) {
        return userService.getUser(id);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        if (!userService.getUser(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("not found with email=" + user.getEmail());
        }

        return userService.createUser(user);
    }
}
