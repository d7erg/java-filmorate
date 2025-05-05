package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    // Методы для работы с пользователями
    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public void create(User user) {
        userStorage.create(user);
    }

    public void update(User user) {
        userStorage.update(user);
    }

    public void deleteUsers() {
        userStorage.deleteUsers();
    }

    // Методы для работы с друзьями
    public void addFriend(Long userId, Long friendId) {
        log.info("Добавление друга для пользователя {}: {}", userId, friendId);
        User user = validateUser(userId);
        User friend = validateFriend(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }


    public List<User> getUserFriends(Long userId) {
        log.info("Получение списка друзей для пользователя {}", userId);
        User user = validateUser(userId);

        return user.getFriends()
                .stream()
                .map(userStorage::getUser)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void removeFriend(Long userId, Long friendId) {
        log.info("Удаление друга {} для пользователя {}", friendId, userId);
        User user = validateUser(userId);
        User friend = validateFriend(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }


    public List<User> getCommonFriends(Long userId, Long friendId) {
        log.info("Получение общих друзей для пользователей {} и {}", userId, friendId);
        User user = validateUser(userId);
        User friend = validateFriend(friendId);

        return user.getFriends()
                .stream()
                .filter(friend.getFriends()::contains)
                .map(userStorage::getUser)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private User validateUser(Long userId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден.");
        }
        return user;
    }

    private User validateFriend(Long friendId) {
        User friend = userStorage.getUser(friendId);
        if (friend == null) {
            throw new NotFoundException("Друг с ID " + friendId + " не найден.");
        }
        return friend;
    }
}
