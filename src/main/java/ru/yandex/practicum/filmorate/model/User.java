package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
public class User {
    @Getter
    @Setter
    private static int identificator = 0;
    private int id;
    private final String login;
    private String name;
    private final String email;
    private final LocalDate birthday;
}

