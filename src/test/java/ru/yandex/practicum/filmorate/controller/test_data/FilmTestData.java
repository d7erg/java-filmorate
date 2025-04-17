package ru.yandex.practicum.filmorate.controller.test_data;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmTestData {

    // Валидные данные
    public static Film validFilmWithAllFieldsFilled() {
        return Film.builder()
                .name("Аватар")
                .description("Научно-фантастический фильм о планете Пандора")
                .releaseDate(LocalDate.of(2009, 12, 18))
                .duration(162)
                .build();
    }

    public static Film validFilmWithoutDescription() {
        return Film.builder()
                .name("Терминатор")
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(107)
                .build();
    }

    // Невалидные данные
    public static Film invalidFilmWithEmptyName() {
        return Film.builder()
                .name("")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();
    }

    public static Film invalidFilmWithTooLongDescription() {
        return Film.builder()
                .name("Длинное описание")
                .description("a".repeat(201)) // 201 символ
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();
    }

    public static Film invalidFilmWithInvalidReleaseDate() {
        return Film.builder()
                .name("Неверный релиз")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1895, 12, 27)) // раньше минимальной даты
                .duration(120)
                .build();
    }

    public static Film invalidFilmWithNegativeDuration() {
        return Film.builder()
                .name("Отрицательная длительность")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(-120)
                .build();
    }

    public static Film invalidFilmWithZeroDuration() {
        return Film.builder()
                .name("Нулевая длительность")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(0)
                .build();
    }

    // Данные для обновления
    public static Film filmForUpdate(Long existingId) {
        return Film.builder()
                .id(existingId)
                .name("Аватар 2")
                .description("Продолжение истории о планете Пандора")
                .releaseDate(LocalDate.of(2022, 12, 16))
                .duration(190)
                .build();
    }

    public static Film filmWithNonExistingId() {
        return Film.builder()
                .id(999L)
                .name("Не существующий фильм")
                .description("Описание несуществующего фильма")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();
    }

    public static Film filmWithoutIdForUpdate() {
        return Film.builder()
                .name("Обновление без ID")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();
    }
}

