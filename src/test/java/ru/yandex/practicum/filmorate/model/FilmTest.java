package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.BaseTestSuite;
import ru.yandex.practicum.filmorate.controller.test_data.FilmTestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmTest extends BaseTestSuite {

    private Film film;

    @Test
    void shouldPassWhenAllFieldsAreValid() {
        film = FilmTestData.validFilmWithAllFieldsFilled();
        filmViolations = validate(film);
        assertEquals(0, filmViolations.size());
    }

    @Test
    void shouldPassWhenDescriptionIsMissing() {
        film = FilmTestData.validFilmWithoutDescription();
        filmViolations = validate(film);
        assertEquals(0, filmViolations.size());
    }

    @Test
    void shouldFailWhenNameIsEmpty() {
        film = FilmTestData.invalidFilmWithEmptyName();
        filmViolations = validate(film);
        assertEquals(1, filmViolations.size());
        assertTrue(filmViolations.iterator().next().getMessage().contains("Название фильма не может быть пустым"));
    }

    @Test
    void shouldFailWhenDescriptionIsTooLong() {
        film = FilmTestData.invalidFilmWithTooLongDescription();
        filmViolations = validate(film);
        assertEquals(1, filmViolations.size());
        assertTrue(filmViolations.iterator().next().getMessage().contains("Описание не может превышать 200 символов"));
    }

    @Test
    void shouldFailWhenReleaseDateIsBeforeMinimum() {
        film = FilmTestData.invalidFilmWithInvalidReleaseDate();
        filmViolations = validate(film);
        assertEquals(1, filmViolations.size());
        assertTrue(filmViolations.iterator().next().getMessage()
                .contains("Дата релиза должна быть не раньше 28 декабря 1895 года"));
    }

    @Test
    void shouldFailWhenDurationIsNegative() {
        film = FilmTestData.invalidFilmWithNegativeDuration();
        filmViolations = validate(film);
        assertEquals(1, filmViolations.size());
        assertTrue(filmViolations.iterator().next().getMessage()
                .contains("Продолжительность фильма должна быть положительным числом"));
    }

    @Test
    void shouldFailWhenDurationIsZero() {
        film = FilmTestData.invalidFilmWithZeroDuration();
        filmViolations = validate(film);
        assertEquals(1, filmViolations.size());
        assertTrue(filmViolations.iterator().next().getMessage()
                .contains("Продолжительность фильма должна быть положительным числом"));
    }


}