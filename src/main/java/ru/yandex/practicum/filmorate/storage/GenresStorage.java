package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genres;

import java.util.Collection;


public interface GenresStorage {

    Collection<Genres> getGenres();

    Genres getGenreById(int id);
}
