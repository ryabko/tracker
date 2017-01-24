package ru.kalcho.tracker.service;

import org.sql2o.Connection;
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
                    "(id, pin, creation_date, update_date, ip, latitude, longitude, bot) " +
                    "values (:id, :pin, :creation_date, :update_date, :ip, :latitude, :longitude, :bot)")
                    .addParameter("id", id)
                    .addParameter("pin", pin)
                    .addParameter("creation_date", date)
                    .addParameter("update_date", date)
                    .addParameter("ip", ip)
                    .addParameter("latitude", latitude)
                    .addParameter("longitude", longitude)
                    .addParameter("bot", bot)
                    .executeUpdate();
            return id;
        }
    }

    public List<User> findAll() {
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(
                    "select id, pin, creation_date creationDate, update_date updateDate, " +
                            "ip, latitude, longitude, bot from users")
                    .executeAndFetch(User.class);
        }
    }

}
