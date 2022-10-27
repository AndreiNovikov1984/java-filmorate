package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;
    private static final Logger log = LoggerFactory.getLogger("MpaService");

    public Collection<Mpa> getMpas() {      // метод получения списка всех рейтингов
        return mpaStorage.getMpas();
    }

    public Mpa getMpaById(int id) {         // метод получения рейтинга по ID
        if (id <= 0) {
            log.warn("Передан некорректный ID {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз.");
        }
        return mpaStorage.getMpaById(id);
    }
}
