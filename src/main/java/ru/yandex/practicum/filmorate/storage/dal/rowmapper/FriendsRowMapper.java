package ru.yandex.practicum.filmorate.storage.dal.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.user.Friends;
import ru.yandex.practicum.filmorate.model.user.FriendshipStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendsRowMapper implements RowMapper<Friends> {
    @Override
    public Friends mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Friends.builder()
                .userId(rs.getLong("user_id"))
                .friendId(rs.getLong("friend_id"))
                .status(FriendshipStatus.valueOf(rs.getString("status_name")))
                .build();
    }
}


