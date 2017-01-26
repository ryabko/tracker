package ru.kalcho.tracker.service;

import org.sql2o.Connection;
import org.sql2o.Sql2o;
import ru.kalcho.tracker.model.Destination;

/**
 *
 */
public class DestinationService {

    private Sql2o sql2o;

    public DestinationService(Sql2o sql2o) {
        this.sql2o = sql2o;
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

}
