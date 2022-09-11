package ru.yandex.practicum.filmorate.controllers;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FilmControllerTest {
    private static GsonBuilder gsonBuilder;
    private static Gson gson;
    private HttpClient client = HttpClient.newHttpClient();
    ;
    private URI uri = URI.create("http://localhost:8080/films");
    private HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    private FilmController filmController;
    Film film;

    @BeforeAll
    public static void beforeAll() {
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
        gson = gsonBuilder.create();
    }

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
    }

    @Test
    public void getAllFilms() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        assertNotNull(response, "Данные не получены");
    }


    @Test
    public void postFilm() throws Exception {
        film = new Film("Vasiliy", "Vasiliy..", LocalDate.of(1999, 9, 9), 190);
        film.setId(1);
        String test = gson.toJson(film);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(test))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        assertEquals(test, response.body(), "Данные не получены");
    }

    @Test
    public void postFilmNameEmpty() throws Exception {
        film = new Film("", "Vasiliy..", LocalDate.of(1999, 9, 9), 190);
        String test = gson.toJson(film);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(test))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(400, response.statusCode());
    }

    @Test
    public void postFilmDescriptionLong() throws Exception {
        film = new Film("Vasiliy", "Vasiliy Vasilievich Vasiliev get to meet Ivan Ivanovich Ivanov. " +
                "They are meeting in the Ivanov city on the Vasilievskaya street. They were very happy to meet each " +
                "other. And they lived happily ever after", LocalDate.of(1999, 9, 9), 190);
        String test = gson.toJson(film);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(test))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(400, response.statusCode());
    }

    @Test
    public void postFilmReleaseDateEarly() throws Exception {
        film = new Film("Vasiliy", "Vasiliy..", LocalDate.of(1099, 9, 9), 190);
        String test = gson.toJson(film);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(test))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(400, response.statusCode());
    }

    @Test
    public void postFilmDurationNegative() throws Exception {
        film = new Film("Vasiliy", "Vasiliy..", LocalDate.of(1999, 9, 9), -20);
        String test = gson.toJson(film);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(test))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(400, response.statusCode());
    }

    @Test
    public void putFilm() throws Exception {
        film = new Film("Vasiliy", "Vasiliy..", LocalDate.of(1999, 9, 9), 190);
        film.setId(1);
        String test = gson.toJson(film);
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(test))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        assertEquals(test, response.body(), "Данные не получены");
    }

    @Test
    public void putFilmIdIncorrect() throws Exception {
        film = new Film("Vasiliy", "Vasiliy..", LocalDate.of(1999, 9, 9), 190);
        film.setId(-1);
        String test = gson.toJson(film);
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(test))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(404, response.statusCode());
    }
}
