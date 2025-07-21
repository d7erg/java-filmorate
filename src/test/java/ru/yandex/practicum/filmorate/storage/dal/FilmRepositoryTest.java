package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.dal.rowmapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.rowmapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.rowmapper.MpaRowMapper;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmRepository.class, FilmRowMapper.class, MpaRepository.class, MpaRowMapper.class,
        GenreRepository.class, GenreRowMapper.class, LikeRepository.class})
class FilmRepositoryTest {

    private final FilmRepository filmRepository;
    private final MpaRepository mpaRepository;
    private final JdbcTemplate jdbcTemplate;

    private Film film1;
    private Film film2;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM films");

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

        filmRepository.create(film1);
        filmRepository.create(film2);
    }

    @Test
    void create_ShouldSaveAndGetFilm() {
        assertThat(film1.getId()).isNotNull();

        Film savedFilm = filmRepository.getFilm(film1.getId());
        assertThat(savedFilm)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(film1);
    }

    @Test
    void getFilms_ShouldReturnAllFilms() {
        List<Film> films = filmRepository.getFilms();
        assertThat(films)
                .hasSize(2)
                .extracting(Film::getName)
                .containsExactlyInAnyOrder("Фильм 1", "Фильм 2");
    }

    @Test
    void update_ShouldModifyExistingFilm() {
        film2.setName("Обновленное название");
        film2.setDescription("Новое описание");
        filmRepository.update(film2);

        Film updatedFilm = filmRepository.getFilm(film2.getId());
        assertThat(updatedFilm)
                .hasFieldOrPropertyWithValue("name", "Обновленное название")
                .hasFieldOrPropertyWithValue("description", "Новое описание");
    }

    @Test
    void delete_ShouldRemoveAllFilms() {
        filmRepository.deleteFilms();
        assertThat(filmRepository.getFilms()).isEmpty();
    }

    @Test
    void getFilm_ShouldThrowExceptionForNonExistingId() {
        assertThatThrownBy(() -> filmRepository.getFilm(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("не найден");
    }

}