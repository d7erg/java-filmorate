package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.user.Friends;
import ru.yandex.practicum.filmorate.model.user.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.dal.rowmapper.FriendsRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.rowmapper.UserRowMapper;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FriendshipRepository.class, FriendsRowMapper.class, UserRepository.class, UserRowMapper.class})
class FriendshipRepositoryTest {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM friends");
        userRepository.deleteUsers();

        // Создание тестовых пользователей
        user1 = User.builder()
                .email("user1@test.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1990, 5, 15))
                .build();

        user2 = User.builder()
                .email("user2@test.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(1991, 7, 17))
                .build();

        user3 = User.builder()
                .email("user3@test.com")
                .login("user3")
                .name("User Three")
                .birthday(LocalDate.of(1993, 8, 19))
                .build();

        userRepository.create(user1);
        userRepository.create(user2);
        userRepository.create(user3);
    }

    @Test
    void addFriend_ShouldCreateFriendship() {
        friendshipRepository.addFriend(user1.getId(), user2.getId());

        assertThat(friendshipRepository.friendshipExists(user1.getId(), user2.getId()))
                .isTrue();


        List<Friends> friends = friendshipRepository.getFriends(user1.getId());
        assertThat(friends)
                .hasSize(1)
                .extracting(Friends::getStatus)
                .contains(FriendshipStatus.valueOf("REQUESTED"));
    }

    @Test
    void removeFriend_ShouldDeleteFriendship() {
        friendshipRepository.addFriend(user1.getId(), user2.getId());
        friendshipRepository.removeFriend(user1.getId(), user2.getId());

        assertThat(friendshipRepository.friendshipExists(user1.getId(), user2.getId()))
                .isFalse();
    }

    @Test
    void getFriends_ShouldReturnCorrectList() {
        friendshipRepository.addFriend(user1.getId(), user2.getId());
        friendshipRepository.addFriend(user1.getId(), user3.getId());

        List<Friends> friends = friendshipRepository.getFriends(user1.getId());
        assertThat(friends)
                .hasSize(2)
                .extracting(Friends::getFriendId)
                .contains(user2.getId(), user3.getId());
    }

    @Test
    void getCommonFriends_ShouldReturnCorrectResult() {
        friendshipRepository.addFriend(user1.getId(), user2.getId());
        friendshipRepository.addFriend(user2.getId(), user1.getId());
        friendshipRepository.addFriend(user1.getId(), user3.getId());
        friendshipRepository.addFriend(user2.getId(), user3.getId());

        List<User> commonFriends = friendshipRepository.getCommonFriends(user1.getId(), user2.getId());

        assertThat(commonFriends)
                .hasSize(1)
                .extracting(User::getId)
                .contains(user3.getId());
    }

}