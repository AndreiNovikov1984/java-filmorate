package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class User {
    private int id;
    private String login;
    private String name;
    private String email;
    private LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();
//    final private Map<Integer,Boolean> friends = new HashMap<>();

}

