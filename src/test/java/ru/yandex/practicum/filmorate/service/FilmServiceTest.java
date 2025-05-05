package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.BaseTestSuite;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.test_data.FilmTestData;
import ru.yandex.practicum.filmorate.test_data.UserTestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest extends BaseTestSuite {

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        film = FilmTestData.validFilmWithAllFieldsFilled();
        anotherFilm = FilmTestData.validFilmWithoutDescription();

        user = UserTestData.validUserWithAllFieldsFilled();
        anotherUser = UserTestData.validUserWithoutName();

        filmService.create(film);
        filmService.create(anotherFilm);

        userStorage.create(user);
        userStorage.create(anotherUser);
    }

    @Test
    void shouldAddLike() {
        filmService.addLike(film.getId(), user.getId());

        Film updatedFilm = filmService.getFilm(film.getId());
        assertEquals(1, updatedFilm.getLikesCount());
        assertTrue(updatedFilm.getLikes().contains(user.getId()));
    }

    @Test
    void shouldRemoveLike() {
        filmService.addLike(film.getId(), user.getId());
        filmService.removeLike(film.getId(), user.getId());

        Film updatedFilm = filmService.getFilm(film.getId());
        assertEquals(0, updatedFilm.getLikesCount());
        assertFalse(updatedFilm.getLikes().contains(user.getId()));
    }

    @Test
    void shouldGetPopularFilms() {
        filmService.addLike(film.getId(), user.getId());
        filmService.addLike(film.getId(), anotherUser.getId());
        filmService.addLike(anotherFilm.getId(), user.getId());

        List<Film> popularFilms = filmService.getPopularFilms(1);
        assertEquals(1, popularFilms.size());
        assertEquals(film.getId(), popularFilms.getFirst().getId());
    }

    @Test
    void shouldThrowExceptionOnAddLikeForNonExistingFilm() {
        assertThrows(NotFoundException.class, () ->
                filmService.addLike(100L, user.getId())
        );
    }

    @Test
    void shouldThrowExceptionOnAddLikeForNonExistingUser() {
        assertThrows(NotFoundException.class, () ->
                filmService.addLike(film.getId(), 100L)
        );
    }


}