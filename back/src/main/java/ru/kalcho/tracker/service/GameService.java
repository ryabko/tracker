package ru.kalcho.tracker.service;

import ru.kalcho.tracker.model.GameState;
import ru.kalcho.tracker.model.UserState;

import static java.util.stream.Collectors.toList;

/**
 *
 */
public class GameService {

    private UserService userService;

    private int activeTimeout;

    public GameService(UserService userService, int activeTimeout) {
        this.userService = userService;
        this.activeTimeout = activeTimeout;
    }

    public GameState obtainGameState(String pin) {
        GameState state = new GameState();

        state.setPlayers(userService.findByPin(pin, activeTimeout).stream()
                .map(u -> new UserState(u, false))
                .collect(toList())
        );

        return state;
    }

}
