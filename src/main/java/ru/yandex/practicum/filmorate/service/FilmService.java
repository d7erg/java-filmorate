package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilm(Long filmId) {
        return filmStorage.getFilm(filmId);
    }

    public void create(Film film) {
        filmStorage.create(film);
    }

    public void update(Film film) {
        filmStorage.update(film);
    }

    public void deleteFilms() {
        filmStorage.deleteFilms();
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Добавление лайка пользователю {} для фильма {}", userId, filmId);
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);

        if (film == null) {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }

        if (user == null) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден.");
        }

        film.addLike(userId);
        log.info("Лайк успешно добавлен пользователю {} для фильма {}", userId, filmId);

    }

    public void removeLike(Long filmId, Long userId) {
        log.info("Удаление лайка пользователя {} для фильма {}", userId, filmId);
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);

        if (film == null) {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }

        if (user == null) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден.");
        }

        film.removeLike(userId);
        log.info("Лайк удален у пользователя {} для фильма {}", userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получение популярных фильмов (количество: {})", count);
        List<Film> allFilms = filmStorage.getFilms();

        log.debug("Количество фильмов в базе: {}", allFilms.size());

        return allFilms.stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

}
