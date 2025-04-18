package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ReleaseDateValidator implements ConstraintValidator<ValidReleaseDate, LocalDate> {
    private LocalDate minReleaseDate;

    @Override
    public void initialize(ValidReleaseDate constraintAnnotation) {
        try {
            minReleaseDate = LocalDate.parse(constraintAnnotation.minDate());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Неверный формат минимальной даты", e);
        }
    }

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        return releaseDate == null || !releaseDate.isBefore(minReleaseDate);
    }
}
