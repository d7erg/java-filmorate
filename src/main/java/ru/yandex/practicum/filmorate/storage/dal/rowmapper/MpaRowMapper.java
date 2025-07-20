package ru.yandex.practicum.filmorate.storage.dal.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaRowMapper implements RowMapper<MPA> {
    @Override
    public MPA mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new MPA(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description")
        );
    }
}

