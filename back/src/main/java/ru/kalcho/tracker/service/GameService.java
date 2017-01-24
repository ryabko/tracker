package ru.kalcho.tracker.service;

import ru.kalcho.tracker.model.GameState;
import ru.kalcho.tracker.model.UserState;

import static java.util.stream.Collectors.toList;

/**
 *
 */
public class GameService {

    private UserService userService;

    public GameService(UserService userService) {
        this.userService = userService;
    }

    public GameState obtainGameState(String pin) {
        GameState state = new GameState();

        state.setPlayers(userService.findByPin(pin).stream()
                .map(u -> new UserState(u, false))
                .collect(toList())
        );

        return state;
    }

}
