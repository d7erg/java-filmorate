package ru.yandex.practicum.filmorate.model.film;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;


@Data
@Builder
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;

    @Builder.Default
    private Set<Genre> genres = new LinkedHashSet<>();

    private MPA mpa;

    @Builder.Default
    private Set<Long> likes = new HashSet<>();

    public int getLikesCount() {
        return likes.size();
    }
}
