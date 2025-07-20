package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<GenreDto> genres;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private MpaDto mpa;
}

