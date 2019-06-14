package ru.kalcho.tracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import ru.kalcho.tracker.Main;
import ru.kalcho.tracker.model.Destination;
import ru.kalcho.tracker.model.User;

import java.util.List;

/**
 *
 */
public class DestinationService {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private Sql2o sql2o;
    private UserService userService;
    private String destinationUserPin;

    public DestinationService(Sql2o sql2o, UserService userService, String destinationUserPin) {
        this.sql2o = sql2o;
        this.userService = userService;
        this.destinationUserPin = destinationUserPin;
    }

    public void createDestination(String name, Float latitude, Float longitude) {
        try (Connection connection = sql2o.open()) {
            if (obtainDestination() != null) {
                throw new IllegalStateException();
            }
            connection.createQuery("insert into destination " +
                    "(name, latitude, longitude) " +
                    "values (:name, :latitude, :longitude)")
                    .addParameter("name", name)
                    .addParameter("latitude", latitude)
                    .addParameter("longitude", longitude)
                    .executeUpdate();
        }
    }

    public void changeDestination(String name, Float latitude, Float longitude) {
        removeDestination();
        createDestination(name, latitude, longitude);
    }

    public Destination obtainDestination() {
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(
                    "select name, latitude, longitude from destination")
                    .executeAndFetch(Destination.class)
                    .stream().findFirst().orElse(null);
        }
    }

    public void removeDestination() {
        try (Connection connection = sql2o.open()) {
            connection.createQuery("delete from destination").executeUpdate();
        }
    }

    public boolean isDestinationUserPin(String pin) {
        return pin.equals(destinationUserPin);
    }

    public Destination obtainDestinationUser(int activeTimeout) {
        List<User> destinationUsers = userService.findByPin(destinationUserPin, activeTimeout);
        if (destinationUsers.isEmpty()) {
            logger.warn("Destination user not found");
            return null;
        }
        if (destinationUsers.size() > 1) {
            logger.info("There is more than one destination user");
        }
        User destinationUser = destinationUsers.get(0);

        Destination destination = new Destination();
        destination.setName("user");
        destination.setLatitude(destinationUser.getLatitude());
        destination.setLongitude(destinationUser.getLongitude());
        return destination;
    }

}
