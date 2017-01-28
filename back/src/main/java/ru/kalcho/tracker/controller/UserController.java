package ru.kalcho.tracker.controller;

import ru.kalcho.tracker.model.GameState;
import ru.kalcho.tracker.model.User;
import ru.kalcho.tracker.service.GameService;
import ru.kalcho.tracker.service.UserService;
import ru.kalcho.tracker.util.JsonUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static spark.Spark.*;

/**
 *
 */
public class UserController {

    private UserService userService;
    private GameService gameService;

    public UserController(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    public void init() {
        post("/api/users", "application/json", (request, response) -> {
            UserPayload payload = JsonUtils.jsonToObject(request.body(), UserPayload.class);

            if (payload.getId() != null) {
                UUID id = UUID.fromString(payload.getId());
                User user = userService.findById(id);
                if (user.getPin().equals(payload.getPin())) {
                    userService.updateUser(id, LocalDateTime.now(), payload.getLatitude(), payload.getLongitude());
                    GameState state = gameService.obtainGameState(payload.getPin());
                    return JsonUtils.objectToJSON(new UserAnswer(id, state));
                }
            }

            UUID id = userService.createUser(payload.getPin(), LocalDateTime.now(), request.ip(),
                    payload.getLatitude(), payload.getLongitude(), false);
            GameState state = gameService.obtainGameState(payload.getPin());

            return JsonUtils.objectToJSON(new UserAnswer(id, state));
        });

        put("/api/users", "application/json", (request, response) -> {
            UserPayload payload = JsonUtils.jsonToObject(request.body(), UserPayload.class);

            UUID id = UUID.fromString(payload.getId());
            User user = userService.findById(id);
            userService.updateUser(id, LocalDateTime.now(), payload.getLatitude(), payload.getLongitude());

            GameState state = gameService.obtainGameState(user.getPin());

            return JsonUtils.objectToJSON(new UserAnswer(id, state));
        });

        delete("/api/users", "application/json", (request, response) -> {
            UserPayload payload = JsonUtils.jsonToObject(request.body(), UserPayload.class);

            UUID id = UUID.fromString(payload.getId());
            userService.removeUser(id);

            return JsonUtils.objectToJSON(new UserAnswer(id, null));
        });

    }

}
