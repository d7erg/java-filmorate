package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.Comparator;
import java.util.stream.Collectors;

public final class FilmMapper {

    public static Film mapToFilm(NewFilmRequest request) {
        return Film.builder()
                .name(request.getName())
                .description(request.getDescription())
                .releaseDate(request.getReleaseDate())
                .duration(request.getDuration())
                .build();
    }

    public static FilmDto mapToFilmDto(Film film) {
        return FilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(MpaMapper.mapToMpaDto(film.getMpa()))
                .genres(film.getGenres().stream()
                        .sorted(Comparator.comparing(Genre::getId))
                        .map(GenreMapper::mapToGenreDto)
                        .collect(Collectors.toList()))
                .build();

    }

    public static void updateFilmFromRequest(UpdateFilmRequest request, Film film) {
        if (request.getName() != null) {
            film.setName(request.getName());
        }
        if (request.getDescription() != null) {
            film.setDescription(request.getDescription());
        }
        if (request.getReleaseDate() != null) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.getDuration() != null) {
            film.setDuration(request.getDuration());
        }
    }
}

