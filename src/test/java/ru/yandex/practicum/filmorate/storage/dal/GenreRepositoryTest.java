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
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.dal.rowmapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.rowmapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.rowmapper.MpaRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmRepository.class, FilmRowMapper.class, MpaRepository.class, MpaRowMapper.class,
        GenreRepository.class, GenreRowMapper.class, LikeRepository.class})
class GenreRepositoryTest {

    private final GenreRepository genreRepository;
    private final FilmRepository filmRepository;
    private final MpaRepository mpaRepository;
    private final JdbcTemplate jdbcTemplate;

    private Film film;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM film_genres");
        filmRepository.deleteFilms();

        film = Film.builder()
                .name("Фильм 1")
                .description("Описание фильма 1")
                .releaseDate(LocalDate.now())
                .duration(120)
                .mpa(mpaRepository.findById(1L))
                .build();

        filmRepository.create(film);
    }

    @Test
    void getAllGenres_ShouldReturnAllPreloadedGenres() {
        List<Genre> genres = genreRepository.getAllGenres();

        assertThat(genres)
                .hasSize(6)
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder(
                        "Комедия",
                        "Драма",
                        "Мультфильм",
                        "Триллер",
                        "Документальный",
                        "Боевик"
                );
    }

    @Test
    void findById_ShouldReturnExistingGenre() {
        Optional<Genre> genre = genreRepository.findById((long) 1);
        assertThat(genre).isPresent();
    }

    @Test
    void findById_ShouldReturnEmptyForNonExistingId() {
        Optional<Genre> genre = genreRepository.findById(999L);
        assertThat(genre).isEmpty();
    }

    @Test
    void existsById_ShouldReturnTrueForExistingId() {
        assertThat(genreRepository.existsById((long) 1))
                .isTrue();
    }

    @Test
    void saveGenres_ShouldCreateCorrectLinks() {
        Set<Long> genreIds = Set.of(1L, 3L, 5L);
        genreRepository.saveGenres(film.getId(), genreIds);

        Set<Genre> genres = genreRepository.findByFilmId(film.getId());
        assertThat(genres)
                .hasSize(3)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(1L, 3L, 5L);
    }


    @Test
    void updateGenres_ShouldUpdateLinks() {
        genreRepository.saveGenres(film.getId(), Set.of(1L, 2L));
        genreRepository.updateGenres(film.getId(), Set.of(3L, 4L));

        Set<Genre> genres = genreRepository.findByFilmId(film.getId());
        assertThat(genres)
                .hasSize(2)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(3L, 4L);
    }


}