package ru.yandex.practicum.filmorate.model.user;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Friends {
    private Long userId;
    private Long friendId;
    private FriendshipStatus status;
}
