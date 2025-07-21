package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.MPA;
import ru.yandex.practicum.filmorate.storage.dal.rowmapper.MpaRowMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaRepository {
    private final JdbcTemplate jdbc;
    private final MpaRowMapper mpaRowMapper;

    private static final String FIND_ALL =
            "SELECT id, name, description FROM mpa";
    private static final String FIND_BY_ID =
            "SELECT id, name, description FROM mpa WHERE id = ?";
    private static final String EXISTS_BY_ID =
            "SELECT EXISTS(SELECT 1 FROM mpa WHERE id = ?)";

    public List<MPA> findAll() {
        return jdbc.query(FIND_ALL, mpaRowMapper);
    }

    public MPA findById(Long id) {
        try {
            return jdbc.queryForObject(FIND_BY_ID, mpaRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("MPA с ID=" + id + " не найден");
        }
    }

    public boolean existsById(Long id) {
        return Boolean.TRUE.equals(
                jdbc.queryForObject(EXISTS_BY_ID, Boolean.class, id)
        );
    }
}

