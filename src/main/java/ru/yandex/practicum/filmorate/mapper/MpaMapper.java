package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.model.film.MPA;

public final class MpaMapper {
    public static MpaDto mapToMpaDto(MPA mpa) {
        return MpaDto.builder()
                .id(mpa.getId())
                .name(mpa.getName())
                .description(mpa.getDescription())
                .build();
    }
}
