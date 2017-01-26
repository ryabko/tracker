package ru.kalcho.tracker.model;

import java.util.List;

/**
 *
 */
public class GameState {

    private List<UserState> players;

    private Destination destination;

    public List<UserState> getPlayers() {
        return players;
    }

    public void setPlayers(List<UserState> players) {
        this.players = players;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }
}
