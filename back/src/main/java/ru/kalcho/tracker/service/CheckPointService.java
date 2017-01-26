package ru.kalcho.tracker.service;

import org.sql2o.Connection;
import org.sql2o.Sql2o;
import ru.kalcho.tracker.model.CheckPoint;

import java.util.List;
import java.util.UUID;

/**
 *
 */
public class CheckPointService {

    private Sql2o sql2o;

    public CheckPointService(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public Long createCheckPoint(String name, Float latitude, Float longitude, Integer radius) {
        try (Connection connection = sql2o.open()) {
            return (Long) connection.createQuery("insert into check_points " +
                    "(name, latitude, longitude, radius) " +
                    "values (:name, :latitude, :longitude, :radius)")
                    .addParameter("name", name)
                    .addParameter("latitude", latitude)
                    .addParameter("longitude", longitude)
                    .addParameter("radius", radius)
                    .executeUpdate()
            .getKey();
        }
    }

    public void removeCheckPoint(Long id) {
        try (Connection connection = sql2o.open()) {
            connection.createQuery("delete from check_points where id = :id")
                    .addParameter("id", id)
                    .executeUpdate();
        }
    }

    public List<CheckPoint> findAll() {
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(
                    "select id, name, latitude, longitude, radius from check_points")
                    .executeAndFetch(CheckPoint.class);
        }
    }

}
