package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.dal.rowmapper.GenreRowMapper;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GenreRepository {
    private final GenreRowMapper genreRowMapper;
    private final NamedParameterJdbcTemplate jdbc;

    private static final String FIND_BY_FILM_ID =
            "SELECT g.* FROM film_genres fg JOIN genres g ON fg.genre_id = g.id WHERE fg.film_id = ?";
    private static final String FIND_GENRES_FOR_FILMS = "SELECT fg.film_id, g.* FROM film_genres fg " +
            "JOIN genres g ON fg.genre_id = g.id " +
            "WHERE fg.film_id IN (:filmIds)";
    private static final String FIND_EXISTING_GENRES_IDS = "SELECT id FROM genres WHERE id IN (:ids)";
    private static final String FIND_ALL_GENRES = "SELECT * FROM genres ORDER BY id";
    private static final String FIND_BY_ID = "SELECT * FROM genres WHERE id = ?";
    private static final String EXISTS_BY_ID = "SELECT EXISTS(SELECT 1 FROM genres WHERE id = ?)";
    private static final String INSERT_GENRE = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_GENRES = "DELETE FROM film_genres WHERE film_id = ?";


    public List<Genre> getAllGenres() {
        return jdbc.getJdbcOperations().query(FIND_ALL_GENRES, genreRowMapper);
    }

    public Optional<Genre> findById(Long id) {
        try {
            return Optional.ofNullable(
                    jdbc.getJdbcOperations().queryForObject(FIND_BY_ID, genreRowMapper, id)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Set<Genre> findByFilmId(Long filmId) {
        return new LinkedHashSet<>(jdbc.getJdbcOperations().query(FIND_BY_FILM_ID, genreRowMapper, filmId));
    }

    public Map<Long, Set<Genre>> findGenresForFilms(Set<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return Collections.emptyMap();
        }

        SqlParameterSource params = new MapSqlParameterSource("filmIds", filmIds);

        return jdbc.query(FIND_GENRES_FOR_FILMS, params, rs -> {
            Map<Long, Set<Genre>> result = new HashMap<>();
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                Genre genre = genreRowMapper.mapRow(rs, rs.getRow());
                result.computeIfAbsent(filmId, k -> new LinkedHashSet<>()).add(genre);
            }
            return result;
        });
    }

    public void saveGenres(Long filmId, Set<Long> genreIds) {
        List<Object[]> batchArgs = genreIds.stream()
                .map(genreId -> new Object[]{filmId, genreId})
                .collect(Collectors.toList());

        jdbc.getJdbcOperations().batchUpdate(INSERT_GENRE, batchArgs);
    }

    public void updateGenres(Long filmId, Set<Long> newGenreIds) {
        jdbc.getJdbcOperations().update(DELETE_GENRES, filmId);

        if (newGenreIds != null && !newGenreIds.isEmpty()) {
            saveGenres(filmId, newGenreIds);
        }
    }

    public boolean existsById(Long id) {
        return Boolean.TRUE.equals(
                jdbc.getJdbcOperations().queryForObject(EXISTS_BY_ID, Boolean.class, id)
        );
    }

    public Set<Long> getExistingIds(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptySet();
        }

        SqlParameterSource params = new MapSqlParameterSource("ids", ids);
        return new HashSet<>(jdbc.queryForList(FIND_EXISTING_GENRES_IDS, params, Long.class));
    }
}


