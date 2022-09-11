package ru.yandex.practicum.filmorate.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class UserControllerTest {
    private static GsonBuilder gsonBuilder;
    private static Gson gson;
    private HttpClient client = HttpClient.newHttpClient();
    ;
    private URI uri = URI.create("http://localhost:8080/users");
    private HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    private UserController userController;
    User user;

    @BeforeAll
    public static void beforeAll() {
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
        gson = gsonBuilder.create();
    }

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
    }

    @Test
    public void getAllUsers() throws IOException, InterruptedException {
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
    public void postUser() throws IOException, InterruptedException {
        user = new User("Vasilek", "vasya@vasiliy.ru", LocalDate.of(1999, 9, 9));
        user.setName("Vasiliy");
        user.setId(1);
        String test = gson.toJson(user);
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
    public void postUserEmailEmpty() throws IOException, InterruptedException {
        user = new User("Vasilek", "", LocalDate.of(1999, 9, 9));
        user.setName("Vasiliy");
        user.setId(1);
        String test = gson.toJson(user);
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
    public void postUserEmailWithout() throws IOException, InterruptedException {
        user = new User("Vasilek", "vasya-vasiliy.ru", LocalDate.of(1999, 9, 9));
        user.setName("Vasiliy");
        user.setId(1);
        String test = gson.toJson(user);
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
    public void postUserLoginEmpty() throws IOException, InterruptedException {
        user = new User("", "vasya@vasiliy.ru", LocalDate.of(1999, 9, 9));
        user.setName("Vasiliy");
        user.setId(1);
        String test = gson.toJson(user);
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
    public void postUserLoginWithSpace() throws IOException, InterruptedException {
        user = new User("Vasil ek", "vasya@vasiliy.ru", LocalDate.of(1999, 9, 9));
        user.setName("Vasiliy");
        user.setId(1);
        String test = gson.toJson(user);
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
    public void postUserWithoutName() throws IOException, InterruptedException {
        user = new User("Vasilek", "vasya@vasiliy.ru", LocalDate.of(1999, 9, 9));
        String test = gson.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(test))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        User user1 = gson.fromJson(response.body(), User.class);
        assertEquals("Vasilek", user1.getName(), "Данные не получены");
    }

    @Test
    public void postUserBirthdayInFuture() throws IOException, InterruptedException {
        user = new User("Vasilek", "vasya@vasiliy.ru", LocalDate.of(2099, 9, 9));
        user.setName("Vasiliy");
        String test = gson.toJson(user);
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
    public void putUser() throws IOException, InterruptedException {
        user = new User("Vasilek", "vasya@vasiliy.ru", LocalDate.of(1999, 9, 9));
        user.setName("Vasiliy");
        user.setId(1);
        String test = gson.toJson(user);
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
    public void putUserIdIncorrect() throws IOException, InterruptedException {
        user = new User("Vasilek", "vasya@vasiliy.ru", LocalDate.of(1999, 9, 9));
        user.setName("Vasiliy");
        user.setId(-1);
        String test = gson.toJson(user);
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