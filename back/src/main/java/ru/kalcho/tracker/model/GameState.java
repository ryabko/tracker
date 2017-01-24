package ru.kalcho.tracker.model;

import java.util.List;

/**
 *
 */
public class GameState {

    private List<UserState> players;

    public List<UserState> getPlayers() {
        return players;
    }

    public void setPlayers(List<UserState> players) {
        this.players = players;
    }

}
