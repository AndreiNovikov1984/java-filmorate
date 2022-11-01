package ru.yandex.practicum.filmorate.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Service
public class Validation {
    private static final Logger log = LoggerFactory.getLogger("Validation");


    public static boolean validationFilm(Film film) {
        boolean answer = false;
        if ((film.getName() == null) || (film.getName().equals("")) || (film.getName().equals("null"))) {
            log.warn("Пустое название");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Название фильма не может быть пустым.");
        }
        if (film.getDescription().length() >= 200) {
            log.warn("Слишком длинное описание фильма");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Число символов описания фильма не должно превышать 200.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза фильма слишком рано");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата релиза фильма не может быть ранее 28.12.1895.");
        }
        if (film.getDuration() < 0) {
            log.warn("Отрицательная длительность фильма");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Длительность фильма не может быть меньше 0.");
        }
        if (film.getId() < 0) {
            log.warn("Некорректный id фильма в запросе - {}", film.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз.");
        }
        if (film.getMpa() == null) {
            log.warn("Некорректный запрос, не указан рейтинг фильма - {}", film.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный id. Попробуйте еще раз.");
        }
        answer = true;
        return answer;
    }

    public static boolean validationUser(User user) {
        boolean answer = false;
        if ((user.getEmail() == null) || (user.getEmail().equals("")) || (user.getEmail().equals("null")) ||
                (!user.getEmail().contains("@"))) {
            log.warn("Ошибка в email - {}", user.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail не корректный. Попробуйте еще раз.");
        }
        if ((user.getLogin() == null) || (user.getLogin().contains(" ")) || (user.getLogin().equals("null")) ||
                (user.getLogin().equals(""))) {
            log.warn("Ошибка в логине - {}", user.getLogin());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Логин не может быть пустым или содержать пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения из будущего");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата рождения не может быть позже текущей даты.");
        }
        if (user.getId() < 0) {
            log.warn("Некорректный id пользователя в запросе - {}", user.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз.");
        }
        if ((user.getName() == null) || (user.getName().equals("")) || (user.getName().equals("null"))) {
            user.setName(user.getLogin());
        }
        answer = true;
        return answer;
    }
}

