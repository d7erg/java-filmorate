package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody @Valid User user) {
        try {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }

            log.debug("Создается новый пользователь: {}", user);

            user.setId(getNextId());
            users.put(user.getId(), user);
            log.info("Пользователь успешно создан: {}", user);

            return user;
        } catch (Exception e) {
            log.error("Ошибка при создании пользователя: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
        try {
            if (users.containsKey(newUser.getId())) {
                User oldUser = users.get(newUser.getId());
                if (newUser.getName() == null || newUser.getName().isBlank()) {
                    newUser.setName(newUser.getLogin());
                }

                log.debug("Начинается обновление пользователя: {}", oldUser);

                oldUser.setEmail(newUser.getEmail());
                oldUser.setLogin(newUser.getLogin());
                oldUser.setName(newUser.getName());
                oldUser.setBirthday(newUser.getBirthday());
                log.info("Пользователь успешно обновлен: {}", oldUser);

                return oldUser;
            }
            log.warn("Пользователь с ID {} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с ID = " + newUser.getId() + " не найден");
        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя: {}", e.getMessage());
            throw e;
        }
    }

    @DeleteMapping
    public void deleteAllUsers() {
        users.clear();
    }

    private long getNextId() {
        return users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L) + 1;
    }
}
