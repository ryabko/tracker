package ru.kalcho.tracker;

import org.sql2o.Sql2o;
import org.sql2o.quirks.NoQuirks;
import ru.kalcho.sql2o.LocalDateTimeConverter;
import ru.kalcho.sql2o.UUIDConverter;
import ru.kalcho.tracker.controller.UserController;
import ru.kalcho.tracker.service.CheckPointService;
import ru.kalcho.tracker.service.DestinationService;
import ru.kalcho.tracker.service.GameService;
import ru.kalcho.tracker.service.UserService;

import java.time.LocalDateTime;
import java.util.UUID;

import static spark.Spark.*;

/**
 *
 */
public class Main {

    public static void main(String[] args) {
        Sql2o sql2o = new Sql2o("jdbc:mysql://" + System.getenv("DB_HOST") + "/" + System.getenv("DB_NAME"),
                System.getenv("DB_USERNAME"), System.getenv("DB_PASSWORD"), new NoQuirks() {{
            converters.put(LocalDateTime.class, new LocalDateTimeConverter());
            converters.put(UUID.class, new UUIDConverter());
        }});

        UserService userService = new UserService(sql2o);
        CheckPointService checkPointService = new CheckPointService(sql2o);
        DestinationService destinationService = new DestinationService(sql2o);
        GameService gameService = new GameService(userService, checkPointService, destinationService, 60);

        new UserController(userService, gameService).init();

        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> response.type("application/json; charset=UTF-8"));

        before((request, response) -> {
            String origin = request.headers("Origin");
            if (origin != null) {
                response.header("Access-Control-Allow-Origin", origin);
            }
        });

        exception(Exception.class, (exception, request, response) -> {
            exception.printStackTrace();
        });
    }

}
