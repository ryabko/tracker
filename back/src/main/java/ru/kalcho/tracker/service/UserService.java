package ru.kalcho.tracker.service;

import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.kalcho.tracker.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 *
 */
public class UserService {

    private Sql2o sql2o;

    public UserService(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public UUID createUser(String pin, LocalDateTime date, String ip, Float latitude, Float longitude, boolean bot) {
        try (Connection connection = sql2o.open()) {
            UUID id = UUID.randomUUID();
            connection.createQuery("insert into users " +
                    "(id, pin, creation_date, update_date, ip, latitude, longitude, bot, removed) " +
                    "values (:id, :pin, :creation_date, :update_date, :ip, :latitude, :longitude, :bot, :removed)")
                    .addParameter("id", id.toString())
                    .addParameter("pin", pin)
                    .addParameter("creation_date", date)
                    .addParameter("update_date", date)
                    .addParameter("ip", ip)
                    .addParameter("latitude", latitude)
                    .addParameter("longitude", longitude)
                    .addParameter("bot", bot)
                    .addParameter("removed", false)
                    .executeUpdate();
            return id;
        }
    }

    public void updateUser(UUID id, LocalDateTime date, Float latitude, Float longitude) {
        try (Connection connection = sql2o.open()) {
            connection.createQuery("update users set update_date = :update_date, " +
                    "latitude = :latitude, longitude = :longitude where id = :id")
                    .addParameter("id", id.toString())
                    .addParameter("update_date", date)
                    .addParameter("latitude", latitude)
                    .addParameter("longitude", longitude)
                    .executeUpdate();
        }
    }

    public void removeUser(UUID id) {
        try (Connection connection = sql2o.open()) {
            connection.createQuery("update users set removed = :removed where id = :id")
                    .addParameter("id", id.toString())
                    .addParameter("removed", true)
                    .executeUpdate();
        }
    }

    public User findById(UUID id) {
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(
                    "select id, pin, creation_date creationDate, update_date updateDate, " +
                            "ip, latitude, longitude, bot from users where id = :id")
                    .addParameter("id", id.toString())
                    .executeAndFetch(User.class)
                    .stream().findFirst().orElse(null);
        }
    }

    public List<User> findByPin(String pin, int activeTimeout) {
        try (Connection connection = sql2o.open()) {
            StringBuilder sql = new StringBuilder("select id, pin, creation_date creationDate, " +
                    "update_date updateDate, ip, latitude, longitude, bot from users " +
                    "where pin = :pin and removed = :removed");
            if (activeTimeout > 0) {
                sql.append(" and (update_date > date_add(now(), interval -:timeout second) or bot = :bot)");
            }
            Query query = connection.createQuery(sql.toString())
                    .addParameter("pin", pin)
                    .addParameter("removed", false);
            if (activeTimeout > 0) {
                query.addParameter("timeout", activeTimeout);
                query.addParameter("bot", 1);
            }
            return query.executeAndFetch(User.class);
        }
    }

    public LocalDateTime findTeamStartDate(String pin) {
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(
                    "select min(creation_date) teamStartDate from users where pin = :pin")
                    .addParameter("pin", pin)
                    .executeAndFetch(LocalDateTime.class)
                    .stream().findFirst().orElse(null);
        }
    }

//    public List<User> findAll() {
//        try (Connection connection = sql2o.open()) {
//            return connection.createQuery(
//                    "select id, pin, creation_date creationDate, update_date updateDate, " +
//                            "ip, latitude, longitude, bot from users")
//                    .executeAndFetch(User.class);
//        }
//    }

}
