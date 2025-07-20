package ru.yandex.practicum.filmorate.dto.mpa;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MpaRequest {
    @NotNull(message = "MPA ID обязателен")
    @Positive(message = "Некорректный ID MPA")
    private Long id;
}
