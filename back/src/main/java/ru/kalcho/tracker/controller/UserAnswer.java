package ru.kalcho.tracker.controller;

import ru.kalcho.tracker.model.TeamState;

import java.util.UUID;

/**
 *
 */
public class UserAnswer {

    private String id;
    private TeamState state;

    public UserAnswer(UUID id, TeamState state) {
        this.id = id != null ? id.toString() : null;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TeamState getState() {
        return state;
    }

    public void setState(TeamState state) {
        this.state = state;
    }

}
