package ru.kalcho.tracker.model;

import java.util.List;

/**
 *
 */
public class GameState {

    private boolean destinationPlayer;

    private List<UserState> players;

    private List<CheckPoint> checkPoints;

    private Destination destination;

    public boolean isDestinationPlayer() {
        return destinationPlayer;
    }

    public void setDestinationPlayer(boolean destinationPlayer) {
        this.destinationPlayer = destinationPlayer;
    }

    public List<UserState> getPlayers() {
        return players;
    }

    public void setPlayers(List<UserState> players) {
        this.players = players;
    }

    public List<CheckPoint> getCheckPoints() {
        return checkPoints;
    }

    public void setCheckPoints(List<CheckPoint> checkPoints) {
        this.checkPoints = checkPoints;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }
}
