package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dal.rowmapper.FilmRowMapper;

import java.sql.Date;
import java.util.List;

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


    @Autowired
    public FilmRepository(JdbcTemplate jdbc, FilmRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> getFilms() {
        return findMany(FIND_ALL);
    }

    @Override
    public Film getFilm(Long filmId) {
        return findOne(FIND_BY_ID, filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + filmId + " не найден"));
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

}

