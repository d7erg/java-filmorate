package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class LikeRepository {
    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedParameterJdbc;

    private static final String INSERT_LIKE = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    private static final String FIND_LIKES_BY_FILM = "SELECT user_id FROM film_likes WHERE film_id = ?";
    private static final String FIND_LIKES_FOR_FILMS =
            "SELECT film_id, user_id FROM film_likes WHERE film_id IN (:filmIds)";


    public void addLike(Long filmId, Long userId) {
        jdbc.update(INSERT_LIKE, filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        jdbc.update(DELETE_LIKE, filmId, userId);
    }

    public Set<Long> getLikes(Long filmId) {
        return new HashSet<>(jdbc.queryForList(FIND_LIKES_BY_FILM, Long.class, filmId));
    }

    public Map<Long, Set<Long>> findLikesForFilms(Set<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return Collections.emptyMap();
        }

        SqlParameterSource params = new MapSqlParameterSource("filmIds", filmIds);

        return namedParameterJdbc.query(FIND_LIKES_FOR_FILMS, params, rs -> {
            Map<Long, Set<Long>> result = new HashMap<>();
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                Long userId = rs.getLong("user_id");
                result.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
            }
            return result;
        });
    }

}

