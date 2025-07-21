package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.dal.rowmapper.UserRowMapper;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserRepository.class, UserRowMapper.class})
class UserRepositoryTest {

    private final UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        userRepository.deleteUsers();

        user1 = User.builder()
                .email("user1@example.com")
                .login("user1_login")
                .name("User One")
                .birthday(LocalDate.of(1990, 5, 15))
                .build();

        user2 = User.builder()
                .email("user2@test.org")
                .login("user2_login")
                .name("User Two")
                .birthday(LocalDate.of(2000, 12, 31))
                .build();

        userRepository.create(user1);
        userRepository.create(user2);
    }

    @Test
    void create_ShouldSaveUserWithGeneratedId() {
        assertThat(user1.getId()).isNotNull();
        assertThat(user2.getId()).isNotNull();
        assertThat(user1.getId()).isNotEqualTo(user2.getId());
    }

    @Test
    void getUser_ShouldReturnCorrectUser() {
        User result1 = userRepository.getUser(user1.getId());
        assertThat(result1)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(user1);

        User result2 = userRepository.getUser(user2.getId());
        assertThat(result2)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(user2);
    }

    @Test
    void getUsers_ShouldReturnAllUsers() {
        List<User> users = userRepository.getUsers();
        assertThat(users)
                .hasSize(2)
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder("user1@example.com", "user2@test.org");
    }

    @Test
    void update_ShouldModifyExistingUser() {
        // Обновление данных
        user1.setLogin("updated_login");
        user1.setName("Updated Name");
        userRepository.update(user1);

        User updatedUser = userRepository.getUser(user1.getId());
        assertThat(updatedUser)
                .hasFieldOrPropertyWithValue("login", "updated_login")
                .hasFieldOrPropertyWithValue("name", "Updated Name");
    }

    @Test
    void delete_ShouldRemoveUser() {
        userRepository.delete(user1.getId());

        assertThatThrownBy(() -> userRepository.getUser(user1.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("не найден");

        assertThat(userRepository.getUsers()).hasSize(1);
    }

    @Test
    void getUser_ShouldThrowExceptionForNonExistingId() {
        Long nonExistingId = 999L;
        assertThatThrownBy(() -> userRepository.getUser(nonExistingId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void create_ShouldThrowExceptionForDuplicateEmail() {
        User duplicateUser = User.builder()
                .email("user1@example.com") // Существующий email
                .login("new_login")
                .name("Test Name")
                .birthday(LocalDate.now())
                .build();

        assertThatThrownBy(() -> userRepository.create(duplicateUser))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessageContaining("email");
    }


    @Test
    void update_ShouldHandleEmptyName() {
        user2.setName("");
        userRepository.update(user2);

        User updatedUser = userRepository.getUser(user2.getId());
        assertThat(updatedUser.getName()).isEmpty();
    }

    @Test
    void getUsers_ShouldReturnEmptyListForEmptyTable() {
        userRepository.deleteUsers();
        assertThat(userRepository.getUsers()).isEmpty();
    }
}
