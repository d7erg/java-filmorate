package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.List;


public interface FilmStorage {

    List<Film> getFilms();

    Film getFilm(Long filmId);

    void create(Film film);

    void update(Film film);

    void deleteFilms();

}
