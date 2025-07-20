package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.model.film.Genre;

public final class GenreMapper {
    public static GenreDto mapToGenreDto(Genre genre) {
        return GenreDto.builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();
    }
}
