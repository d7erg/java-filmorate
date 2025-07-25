package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.genre.GenreRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dal.GenreRepository;
import ru.yandex.practicum.filmorate.storage.dal.LikeRepository;
import ru.yandex.practicum.filmorate.storage.dal.MpaRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;
    private final LikeRepository likeRepository;


    public List<FilmDto> getFilms() {
        log.debug("Получение списка всех фильмов");
        List<Film> films = filmStorage.getFilms();
        loadAdditionalData(films);
        return films.stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto getFilm(Long filmId) {
        log.info("Получение фильма с ID: {}", filmId);
        Film film = filmStorage.getFilm(filmId);
        loadAdditionalData(film);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto create(NewFilmRequest request) {
        log.debug("Создание нового фильма: {}", request);
        Film film = FilmMapper.mapToFilm(request);
        film.setMpa(mpaRepository.findById(request.getMpa().getId()));

        filmStorage.create(film);
        log.info("Создан новый фильм с ID: {}", film.getId());

        Set<Long> genreIds = validateGenres(request.getGenres());
        genreRepository.saveGenres(film.getId(), genreIds);
        film.setGenres(genreRepository.findByFilmId(film.getId()));

        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto update(UpdateFilmRequest request) {
        log.debug("Обновление фильма с ID: {}", request.getId());
        Film film = filmStorage.getFilm(request.getId());
        FilmMapper.updateFilmFromRequest(request, film);

        film.setMpa(mpaRepository.findById(request.getMpa().getId()));

        Set<Long> genreIds = validateGenres(request.getGenres());
        genreRepository.updateGenres(film.getId(), genreIds);
        film.setGenres(genreRepository.findByFilmId(film.getId()));

        filmStorage.update(film);
        log.info("Обновлен фильм с ID: {}", film.getId());

        return FilmMapper.mapToFilmDto(film);
    }

    public void deleteFilms() {
        filmStorage.deleteFilms();
        log.info("Все фильмы успешно удалены");
    }

    public void addLike(Long filmId, Long userId) {
        log.debug("Добавление лайка пользователю {} для фильма {}", userId, filmId);
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);
        film.getLikes().add(userId);
        likeRepository.addLike(filmId, userId);
        log.info("Лайк успешно добавлен пользователю {} для фильма {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        log.debug("Удаление лайка пользователя {} для фильма {}", userId, filmId);
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);
        film.getLikes().remove(userId);
        likeRepository.removeLike(filmId, userId);
        log.info("Лайк удален у пользователя {} для фильма {}", userId, filmId);
    }

    public List<FilmDto> getPopularFilms(int count) {
        log.debug("Получение популярных фильмов (количество: {})", count);
        List<Film> allFilms = filmStorage.getFilms();
        loadAdditionalData(allFilms);

        return allFilms.stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    private Set<Long> validateGenres(Set<GenreRequest> genres) {
        if (genres == null) {
            return Collections.emptySet();
        }

        Set<Long> requestedIds = genres.stream()
                .map(GenreRequest::getId)
                .collect(Collectors.toSet());

        Set<Long> existingIds = genreRepository.getExistingIds(requestedIds);

        if (existingIds.size() != requestedIds.size()) {
            Set<Long> missingIds = new HashSet<>(requestedIds);
            missingIds.removeAll(existingIds);
            throw new NotFoundException("Не найдены жанры с ID: " + missingIds);
        }

        return requestedIds;
    }

    private void loadAdditionalData(Film film) {
        film.setGenres(genreRepository.findByFilmId(film.getId()));
        film.setLikes(likeRepository.getLikes(film.getId()));
    }

    private void loadAdditionalData(List<Film> films) {
        if (films.isEmpty()) {
            return;
        }

        Set<Long> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toSet());

        Map<Long, Set<Genre>> genres = genreRepository.findGenresForFilms(filmIds);
        Map<Long, Set<Long>> likes = likeRepository.findLikesForFilms(filmIds);

        films.forEach(film -> {
            film.setGenres(genres.getOrDefault(film.getId(), Collections.emptySet()));
            film.setLikes(likes.getOrDefault(film.getId(), Collections.emptySet()));
        });
    }
}