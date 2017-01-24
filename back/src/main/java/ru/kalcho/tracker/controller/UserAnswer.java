package ru.kalcho.tracker.controller;

import ru.kalcho.tracker.model.GameState;

import java.util.UUID;

/**
 *
 */
public class UserAnswer {

    private String id;
    private GameState state;

    public UserAnswer(UUID id, GameState state) {
        this.id = id != null ? id.toString() : null;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

}
