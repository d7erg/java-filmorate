package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@RequestBody @Valid Film film) {
        log.debug("Создается новый фильм: {}", film);

        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Фильм успешно создан: {}", film);

        return film;
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film newFilm) {

        if (newFilm.getId() == null) {
            log.warn("Ошибка валидации: ID не указан для обновления");
            throw new ValidationException("ID должен быть указан");
        }

        if (films.containsKey(newFilm.getId())) {
            log.debug("Обновление фильма с ID: {}", newFilm.getId());

            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());

            log.info("Фильм успешно обновлен: {}", oldFilm);

            return oldFilm;
        }

        log.warn("Фильм с ID {} не найден", newFilm.getId());
        throw new NotFoundException("Фильм с ID = " + newFilm.getId() + " не найден");

    }

    @DeleteMapping
    public void deleteAllFilms() {
        films.clear();
    }

    private long getNextId() {
        return films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L) + 1;
    }

}
