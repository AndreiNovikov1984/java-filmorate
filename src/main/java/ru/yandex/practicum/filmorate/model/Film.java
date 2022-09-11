package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;

@Data
public class Film {
    @Getter
    @Setter
    private static int identificator = 0;
    private int id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final Integer duration;

}
