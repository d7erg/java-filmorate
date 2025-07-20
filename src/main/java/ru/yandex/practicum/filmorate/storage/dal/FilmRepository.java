package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dal.rowmapper.FilmRowMapper;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Primary
@Repository
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {

    private static final String INSERT =
            "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_ALL =
            "SELECT f.*, m.name AS mpa_name, m.description AS mpa_description " +
                    "FROM films f JOIN mpa m ON f.mpa_id = m.id";
    private static final String FIND_BY_ID =
            "SELECT f.*, m.name AS mpa_name, m.description AS mpa_description " +
                    "FROM films f JOIN mpa m ON f.mpa_id = m.id WHERE f.id = ?";
    private static final String UPDATE =
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
    private static final String DELETE_ALL = "DELETE FROM films";


    private final GenreRepository genreRepository;
    private final LikeRepository likeRepository;

    @Autowired
    public FilmRepository(JdbcTemplate jdbc,
                          FilmRowMapper mapper,
                          GenreRepository genreRepository,
                          LikeRepository likeRepository,
                          MpaRepository mpaRepository) {
        super(jdbc, mapper);
        this.genreRepository = genreRepository;
        this.likeRepository = likeRepository;
    }

    @Override
    public List<Film> getFilms() {
        List<Film> films = findMany(FIND_ALL);
        loadAdditionalData(films);
        return films;
    }

    @Override
    public Film getFilm(Long filmId) {
        Film film = findOne(FIND_BY_ID, filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + filmId + " не найден"));

        loadAdditionalData(film);
        return film;
    }

    @Override
    public void create(Film film) {
        long id = insert(
                INSERT,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
    }

    @Override
    public void update(Film film) {
        update(
                UPDATE,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
    }

    @Override
    public void deleteFilms() {
        jdbc.update(DELETE_ALL);
    }


    // Методы загрузки дополнительных данных
    private void loadAdditionalData(Film film) {
        film.setGenres(genreRepository.findByFilmId(film.getId()));
        film.setLikes(likeRepository.getLikes(film.getId()));
    }

    private void loadAdditionalData(List<Film> films) {
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

