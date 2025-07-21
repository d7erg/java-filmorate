package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.dal.rowmapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.rowmapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.rowmapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.rowmapper.UserRowMapper;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmRepository.class, FilmRowMapper.class, MpaRepository.class, MpaRowMapper.class,
        LikeRepository.class, UserRepository.class, UserRowMapper.class, GenreRepository.class, GenreRowMapper.class})
class LikeRepositoryTest {

    private final LikeRepository likeRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final MpaRepository mpaRepository;
    private final JdbcTemplate jdbcTemplate;

    private Film film1;
    private Film film2;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM film_likes");
        filmRepository.deleteFilms();
        userRepository.deleteUsers();

        film1 = Film.builder()
                .name("Фильм 1")
                .description("Описание фильма 1")
                .releaseDate(LocalDate.now())
                .duration(120)
                .mpa(mpaRepository.findById(1L))
                .build();

        film2 = Film.builder()
                .name("Фильм 2")
                .description("Описание фильма 2")
                .releaseDate(LocalDate.now().minusDays(1))
                .duration(90)
                .mpa(mpaRepository.findById(1L))
                .build();

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

        user3 = User.builder()
                .email("user3@test.org")
                .login("user3_login")
                .name("User Three")
                .birthday(LocalDate.of(2000, 12, 30))
                .build();

        filmRepository.create(film1);
        filmRepository.create(film2);
        userRepository.create(user1);
        userRepository.create(user2);
        userRepository.create(user3);
    }

    @Test
    void addLike_ShouldCreateLike() {
        likeRepository.addLike(film1.getId(), user1.getId());

        Set<Long> likes = likeRepository.getLikes(film1.getId());
        assertThat(likes).contains(user1.getId());
    }

    @Test
    void removeLike_ShouldDeleteLike() {
        likeRepository.addLike(film1.getId(), user1.getId());
        likeRepository.removeLike(film1.getId(), user1.getId());

        Set<Long> likes = likeRepository.getLikes(film1.getId());
        assertThat(likes).isEmpty();
    }

    @Test
    void getLikes_ShouldReturnAllLikesForFilm() {
        likeRepository.addLike(film1.getId(), user1.getId());
        likeRepository.addLike(film1.getId(), user2.getId());

        Set<Long> likes = likeRepository.getLikes(film1.getId());
        assertThat(likes)
                .hasSize(2)
                .contains(user1.getId(), user2.getId());
    }

    @Test
    void findLikesForFilms_ShouldReturnCorrectMap() {
        likeRepository.addLike(film1.getId(), user1.getId());
        likeRepository.addLike(film1.getId(), user2.getId());
        likeRepository.addLike(film2.getId(), user3.getId());

        Map<Long, Set<Long>> likesMap = likeRepository.findLikesForFilms(
                Set.of(film1.getId(), film2.getId()));

        assertThat(likesMap)
                .hasSize(2)
                .containsKeys(film1.getId(), film2.getId());

        assertThat(likesMap.get(film1.getId()))
                .isNotNull()
                .contains(user1.getId(), user2.getId());

        assertThat(likesMap.get(film2.getId()))
                .isNotNull()
                .contains(user3.getId());
    }


}