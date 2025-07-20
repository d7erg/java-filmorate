package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.film.MPA;
import ru.yandex.practicum.filmorate.storage.dal.MpaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaRepository mpaRepository;

    public List<MpaDto> getAllMpa() {
        log.debug("Получение всех рейтингов MPA");
        return mpaRepository.findAll().stream()
                .map(MpaMapper::mapToMpaDto)
                .collect(Collectors.toList());
    }

    public MpaDto getMpaById(Long id) {
        log.debug("Получение рейтинга MPA с ID: {}", id);
        MPA mpa = mpaRepository.findById(id);
        return MpaMapper.mapToMpaDto(mpa);
    }
}

