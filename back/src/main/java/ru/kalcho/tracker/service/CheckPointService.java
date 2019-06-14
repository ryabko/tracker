package ru.kalcho.tracker.service;

import org.sql2o.Connection;
import org.sql2o.Sql2o;
import ru.kalcho.tracker.model.CheckPoint;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class CheckPointService {

    private Sql2o sql2o;

    private int changeIntervalInMinutes;

    public CheckPointService(Sql2o sql2o, int changeIntervalInMinutes) {
        this.sql2o = sql2o;
        this.changeIntervalInMinutes = changeIntervalInMinutes;
    }

    public void updateCheckPoints(List<CheckPoint> checkPoints) {
        try (Connection connection = sql2o.open()) {
            for (CheckPoint checkPoint : checkPoints) {
                createOrUpdateCheckPoint(connection, checkPoint.getName(), checkPoint.getLatitude(),
                        checkPoint.getLongitude(), checkPoint.getRadius(), checkPoint.getGroupIndex());
            }
            List<String> names = checkPoints.stream().map(CheckPoint::getName).collect(Collectors.toList());
            removeCheckPointsNotInNames(connection, names);
        }
    }

    public void createOrUpdateCheckPoint(Connection connection, String name, Float latitude, Float longitude,
                                         Integer radius, Integer groupIndex) {
        CheckPoint checkPoint = findByName(connection, name);
        if (checkPoint == null) {
            createCheckPoint(connection, name, latitude, longitude, radius, groupIndex);
        } else {
            updateCheckPoint(connection, name, latitude, longitude, radius, groupIndex);
        }
    }

    private CheckPoint findByName(Connection connection, String name) {
        return connection.createQuery(
                "select name, latitude, longitude, radius, group_index from check_points " +
                        "where name = :name")
                .addParameter("name", name)
                .addColumnMapping("group_index", "groupIndex")
                .executeAndFetch(CheckPoint.class)
                .stream().findFirst().orElse(null);
    }

    private Long createCheckPoint(Connection connection, String name,
                                  Float latitude, Float longitude, Integer radius, Integer groupIndex) {
        return (Long) connection.createQuery("insert into check_points " +
                "(name, latitude, longitude, radius, group_index) " +
                "values (:name, :latitude, :longitude, :radius, :groupIndex)")
                .addParameter("name", name)
                .addParameter("latitude", latitude)
                .addParameter("longitude", longitude)
                .addParameter("radius", radius)
                .addParameter("groupIndex", groupIndex)
                .executeUpdate()
                .getKey();
    }

    private void updateCheckPoint(Connection connection, String name,
                                  Float latitude, Float longitude, Integer radius, Integer groupIndex) {
        connection.createQuery("update check_points set " +
                "latitude = :latitude, longitude = :longitude, " +
                "radius = :radius, group_index = :groupIndex where name = :name")
                .addParameter("name", name)
                .addParameter("latitude", latitude)
                .addParameter("longitude", longitude)
                .addParameter("radius", radius)
                .addParameter("groupIndex", groupIndex)
                .executeUpdate();
    }

    private void removeCheckPointsNotInNames(Connection connection, Collection<String> names) {
        connection.createQuery("delete check_points where name not in (:names)")
                .addParameter("names", names)
                .executeUpdate();
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
                    "select id, name, latitude, longitude, radius, group_index from check_points")
                    .addColumnMapping("group_index", "groupIndex")
                    .executeAndFetch(CheckPoint.class);
        }
    }

    public List<CheckPoint> findForTeam(LocalDateTime teamStartDate) {
        try (Connection connection = sql2o.open()) {
            long minutesActive = teamStartDate.until(LocalDateTime.now(), ChronoUnit.MINUTES);
            long iteration = minutesActive / changeIntervalInMinutes;
            int groupsCount = getGroupsCount(connection);
            long groupIndex = iteration % groupsCount + 1;
            return connection.createQuery(
                    "select id, name, latitude, longitude, radius from check_points where group_index = :groupIndex")
                    .addParameter("groupIndex", groupIndex)
                    .executeAndFetch(CheckPoint.class);
        }
    }

    private int getGroupsCount(Connection connection) {
        return connection.createQuery(
                "select count(distinct group_index) from check_points")
                .executeScalar(Integer.class);
    }

}
