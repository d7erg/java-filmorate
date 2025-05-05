package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.BaseTestSuite;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.test_data.FilmTestData;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmStorageTest extends BaseTestSuite {

    @Autowired
    private FilmStorage filmStorage;

    @BeforeEach
    void setUp() {
        film = FilmTestData.validFilmWithAllFieldsFilled();
        anotherFilm = FilmTestData.validFilmWithoutDescription();

        filmStorage.create(film);
        filmStorage.create(anotherFilm);
    }

    @Test
    public void shouldCreateFilm() {
        Film createdFilm = filmStorage.getFilm(film.getId());
        assertNotNull(createdFilm);
        assertEquals(film, createdFilm);
    }

    @Test
    public void shouldGetAllFilms() {
        List<Film> allFilms = filmStorage.getFilms();

        assertEquals(2, allFilms.size());
        assertTrue(allFilms.contains(film));
        assertTrue(allFilms.contains(anotherFilm));
    }

    @Test
    public void shouldGetFilmById() {
        assertNotNull(filmStorage.getFilm(film.getId()));
    }

    @Test
    public void shouldUpdateFilm() {
        Film updatedFilm = Film.builder()
                .id(film.getId())
                .name("Updated Name")
                .description("Updated Description")
                .releaseDate(LocalDate.of(2025, 5, 4))
                .duration(120)
                .build();

        filmStorage.update(updatedFilm);

        Film resultFilm = filmStorage.getFilm(film.getId());
        assertEquals("Updated Name", resultFilm.getName());
        assertEquals("Updated Description", resultFilm.getDescription());
        assertEquals(LocalDate.of(2025, 5, 4), resultFilm.getReleaseDate());
        assertEquals(120, resultFilm.getDuration());
    }

    @Test
    public void shouldDeleteFilms() {
        filmStorage.deleteFilms();
        assertNull(filmStorage.getFilm(film.getId()));
        assertNull(filmStorage.getFilm(anotherFilm.getId()));
    }

    @Test
    public void shouldAutoGenerateIdOnCreate() {
        Film newFilm = Film.builder()
                .name("New Film")
                .description("New Description")
                .releaseDate(LocalDate.of(2024, 1, 1))
                .duration(90)
                .build();

        filmStorage.create(newFilm);
        assertNotNull(newFilm.getId());
        assertTrue(newFilm.getId() > 0);
    }

}