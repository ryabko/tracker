package ru.kalcho.tracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Sql2o;
import org.sql2o.quirks.NoQuirks;
import ru.kalcho.sql2o.LocalDateTimeConverter;
import ru.kalcho.sql2o.UUIDConverter;
import ru.kalcho.tracker.controller.CheckPointController;
import ru.kalcho.tracker.controller.UserController;
import ru.kalcho.tracker.service.CheckPointService;
import ru.kalcho.tracker.service.DestinationService;
import ru.kalcho.tracker.service.GameService;
import ru.kalcho.tracker.service.UserService;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static spark.Spark.*;

/**
 *
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Sql2o sql2o = new Sql2o("jdbc:mysql://" + System.getenv("DB_HOST") + "/" + System.getenv("DB_NAME"),
                System.getenv("DB_USERNAME"), System.getenv("DB_PASSWORD"), new NoQuirks() {{
            converters.put(LocalDateTime.class, new LocalDateTimeConverter());
            converters.put(UUID.class, new UUIDConverter());
        }});

        String checkPointsChangeIntervalString = System.getenv("CHECK_POINTS_CHANGE_INTERVAL");
        if (checkPointsChangeIntervalString == null) {
            checkPointsChangeIntervalString = "1";
        }
        int checkPointsChangeInterval = Integer.parseInt(checkPointsChangeIntervalString);

        String destinationUserPin = System.getenv("DESTINATION_USER_PIN");

        UserService userService = new UserService(sql2o);
        CheckPointService checkPointService = new CheckPointService(sql2o, checkPointsChangeInterval);
        DestinationService destinationService = new DestinationService(sql2o, userService, destinationUserPin);
        GameService gameService = new GameService(userService, checkPointService, destinationService, 10);

        new UserController(userService, gameService).init();
        new CheckPointController(checkPointService).init();

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

        after((request, response) ->
                logger.info(new Date() + "{" + request.requestMethod() + request.uri() + request.body() +
                        " - " + response.status() + " " + response.body())
        );

        before((request, response) -> response.type("application/json; charset=UTF-8"));

        before((request, response) -> {
            String origin = request.headers("Origin");
            if (origin != null) {
                response.header("Access-Control-Allow-Origin", origin);
            }
        });

        exception(Exception.class, (e, request, response) -> {
            logger.error(e.getMessage(), e);
        });
    }

}
