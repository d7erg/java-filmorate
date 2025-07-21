package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.ConflictException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.user.Friends;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dal.FriendshipRepository;
import ru.yandex.practicum.filmorate.storage.dal.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    // Методы для работы с пользователями
    public List<UserDto> getUsers() {
        log.debug("Получение списка всех пользователей");
        return userStorage.getUsers().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long userId) {
        log.debug("Получение пользователя с ID: {}", userId);
        User user = validateUser(userId);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto create(NewUserRequest request) {
        log.debug("Создание нового пользователя: {}", request);
        User user = UserMapper.mapToUser(request);
        userStorage.create(user);
        log.info("Создан новый пользователь с ID: {}", user.getId());
        return UserMapper.mapToUserDto(user);
    }

    public UserDto update(UpdateUserRequest request) {
        log.debug("Обновление пользователя с ID: {}", request.getId());
        User user = validateUser(request.getId());
        UserMapper.updateUserFromDto(request, user);
        userStorage.update(user);
        log.info("Обновлен пользователь с ID: {}", user.getId());
        return UserMapper.mapToUserDto(user);
    }

    public void deleteUser(Long userId) {
        log.debug("Удаление пользователя с ID: {}", userId);
        validateUser(userId);
        userStorage.delete(userId);
        log.info("Удален пользователь с ID: {}", userId);
    }

    // Методы для работы с друзьями
    public UserDto addFriend(Long userId, Long friendId) {
        log.debug("Добавление друга для пользователя: {} friend: {}", userId, friendId);
        User user = validateUser(userId);
        validateUser(friendId);

        if (friendshipRepository.friendshipExists(userId, friendId)) {
            throw new ConflictException("Запрос на дружбу уже отправлен");
        }

        if (userId.equals(friendId)) {
            throw new BadRequestException("Пользователь не может добавить сам себя в друзья");
        }

        friendshipRepository.addFriend(userId, friendId);
        user.getFriends().add(friendId);
        userStorage.update(user);
        log.info("Добавлен друг для пользователя: {} friend: {}", userId, friendId);
        return UserMapper.mapToUserDto(user);
    }

    public List<UserDto> getUserFriends(Long userId) {
        log.debug("Получение списка друзей для пользователя: {}", userId);
        validateUser(userId);
        List<Friends> friendships = friendshipRepository.getFriends(userId);

        return friendships.stream()
                .map(friendship -> validateUser(friendship.getFriendId()))
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getCommonFriends(Long userId, Long otherId) {
        log.debug("Получение общих друзей для пользователей: {} и {}", userId, otherId);
        validateUser(userId);
        validateUser(otherId);

        List<User> commonFriends = friendshipRepository.getCommonFriends(userId, otherId);

        return commonFriends.stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }


    public void removeFriend(Long userId, Long friendId) {
        log.debug("Удаление друга для пользователя: {} friend: {}", userId, friendId);
        User user = validateUser(userId);
        User friend = validateUser(friendId);

        friendshipRepository.removeFriend(userId, friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.update(user);
        userStorage.update(friend);
    }

    private User validateUser(Long userId) {
        return userStorage.getUser(userId);
    }
}

