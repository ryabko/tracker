package ru.kalcho.tracker.model;

/**
 *
 */
public class UserState {

    private User user;

    private boolean target;

    public UserState(User user, boolean target) {
        this.user = user;
        this.target = target;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isTarget() {
        return target;
    }

    public void setTarget(boolean target) {
        this.target = target;
    }

}
