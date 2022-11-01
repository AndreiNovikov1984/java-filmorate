package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaServiceTest {
    private final MpaService mpaService;
    private Mpa mpa;

    @Test
    @Sql(scripts = {"file:src/test/resources/test.sql"})
    public void getAllMpas() {
        Collection<Mpa> mpas = mpaService.getMpas();
        assertNotNull(mpas);
        assertEquals(5, mpas.size(), "Ошибка");
    }

    @Test
    public void getMpasbyId() {
        mpa = mpaService.getMpaById(1);
        assertNotNull(mpa);
        assertEquals(1, mpa.getId(), "Данные не получены");
        assertEquals("G", mpa.getName(), "Данные не получены");
    }

    @Test
    public void getMpasbyIncorrectId() {
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            mpaService.getMpaById(-1);
        });
        Assertions.assertEquals("404 NOT_FOUND \"Некорректный id. Попробуйте еще раз.\"", exeption.getMessage());
    }
}
