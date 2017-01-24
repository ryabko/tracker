package ru.kalcho.tracker.controller;

import ru.kalcho.tracker.service.UserService;
import ru.kalcho.tracker.util.JsonUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static spark.Spark.post;

/**
 *
 */
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void start() {
        post("/api/users", "application/json", (request, response) -> {
            UserPayload payload = JsonUtils.jsonToObject(request.body(), UserPayload.class);

            UUID id = userService.createUser(payload.getPin(), LocalDateTime.now(), request.ip(),
                    payload.getLatitude(), payload.getLongitude(), false);
            response.type("application/json; charset=UTF-8");

            UserAnswer answer = new UserAnswer(id, null);
            return JsonUtils.objectToJSON(answer);
        });
    }

}
