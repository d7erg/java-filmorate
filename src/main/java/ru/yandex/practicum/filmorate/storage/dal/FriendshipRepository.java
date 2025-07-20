package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.user.Friends;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.dal.rowmapper.FriendsRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.rowmapper.UserRowMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendshipRepository {

    private final JdbcTemplate jdbc;

    private static final String ADD_FRIEND = "INSERT INTO friends(user_id, friend_id, status_id) VALUES (?, ?, ?)";
    private static final String REMOVE_FRIEND = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String STATUS_ID = "SELECT id FROM friendship_status WHERE name = 'REQUESTED'";
    private static final String FRIENDSHIP_EXISTS =
            "SELECT EXISTS(SELECT 1 FROM friends WHERE user_id = ? AND friend_id = ?)";
    private static final String GET_FRIENDS = """
                SELECT f.user_id, f.friend_id, fs.name AS status_name
                FROM friends f
                JOIN friendship_status fs ON f.status_id = fs.id
                WHERE f.user_id = ?
            """;
    private static final String COMMON_FRIENDS = """
                SELECT u.*
                FROM users u
                JOIN friends f1 ON u.id = f1.friend_id AND f1.user_id = ?
                JOIN friends f2 ON u.id = f2.friend_id AND f2.user_id = ?
            """;


    public void addFriend(Long userId, Long friendId) {
        Short statusId = jdbc.queryForObject(STATUS_ID, Short.class);
        jdbc.update(ADD_FRIEND, userId, friendId, statusId);
    }

    public void removeFriend(Long userId, Long friendId) {
        jdbc.update(REMOVE_FRIEND, userId, friendId);
    }

    public List<Friends> getFriends(Long userId) {
        return jdbc.query(GET_FRIENDS, new FriendsRowMapper(), userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        return jdbc.query(
                COMMON_FRIENDS,
                new UserRowMapper(),
                userId,
                otherId
        );
    }

    public boolean friendshipExists(Long userId, Long friendId) {
        return Boolean.TRUE.equals(
                jdbc.queryForObject(FRIENDSHIP_EXISTS, Boolean.class, userId, friendId)
        );
    }
}

