package ru.kalcho.tracker.controller;

import ru.kalcho.tracker.model.GameState;
import ru.kalcho.tracker.service.GameService;
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
    private GameService gameService;

    public UserController(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    public void start() {
        post("/api/users", "application/json", (request, response) -> {
            UserPayload payload = JsonUtils.jsonToObject(request.body(), UserPayload.class);

            UUID id = userService.createUser(payload.getPin(), LocalDateTime.now(), request.ip(),
                    payload.getLatitude(), payload.getLongitude(), false);
            GameState state = gameService.obtainGameState(payload.getPin());

            response.type("application/json; charset=UTF-8");
            return JsonUtils.objectToJSON(new UserAnswer(id, state));
        });
    }

}
