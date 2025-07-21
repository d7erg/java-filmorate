package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.MPA;
import ru.yandex.practicum.filmorate.storage.dal.rowmapper.MpaRowMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaRepository.class, MpaRowMapper.class})
class MpaRepositoryTest {

    private final MpaRepository mpaRepository;


    @Test
    void findAll_ShouldReturnAllMPA() {
        List<MPA> mpa = mpaRepository.findAll();

        assertThat(mpa).isNotEmpty();
        assertThat(mpa).extracting(MPA::getName).contains("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    void findById_ShouldReturnExistingMPA() {
        List<MPA> mpa = mpaRepository.findAll();
        MPA expectedMpa = mpa.getFirst();

        MPA actualMpa = mpaRepository.findById(expectedMpa.getId());

        assertThat(actualMpa)
                .usingRecursiveComparison()
                .isEqualTo(expectedMpa);
    }

    @Test
    void findById_ShouldThrowExceptionForNonExistingId() {
        assertThatThrownBy(() -> mpaRepository.findById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void existsById_ShouldReturnTrueForExistingId() {
        List<MPA> mpa = mpaRepository.findAll();
        Long existingId = mpa.getFirst().getId();

        assertThat(mpaRepository.existsById(existingId))
                .isTrue();
    }

    @Test
    void existsById_ShouldReturnFalseForNonExistingId() {
        assertThat(mpaRepository.existsById(999L))
                .isFalse();
    }
}
