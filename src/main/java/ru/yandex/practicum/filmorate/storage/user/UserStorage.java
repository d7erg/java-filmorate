package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;


public interface UserStorage {

    List<User> getUsers();

    User getUser(Long userId);

    void create(User user);

    void update(User user);

    void deleteUsers();

}
