package ru.kalcho.tracker.service;

import ru.kalcho.tracker.model.CheckPoint;
import ru.kalcho.tracker.model.GameState;
import ru.kalcho.tracker.model.User;
import ru.kalcho.tracker.model.UserState;
import ru.kalcho.tracker.util.GeoUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 *
 */
public class GameService {

    private UserService userService;

    private CheckPointService checkPointService;

    private int activeTimeout;

    public GameService(UserService userService, CheckPointService checkPointService, int activeTimeout) {
        this.userService = userService;
        this.checkPointService = checkPointService;
        this.activeTimeout = activeTimeout;
    }

    public GameState obtainGameState(String pin) {
        GameState state = new GameState();

        List<CheckPoint> points = checkPointService.findAll();
        List<User> users = userService.findByPin(pin, activeTimeout);

        Map<User, CheckPoint> usersCheckPoints = new HashMap<>();

        for (User user : users) {
            CheckPoint match = findMatch(user, points, usersCheckPoints.values());
            usersCheckPoints.put(user, match);
        }

        state.setPlayers(users.stream()
                .map(u -> new UserState(u, usersCheckPoints.get(u) != null))
                .collect(toList())
        );

        return state;
    }

    private CheckPoint findMatch(User user, Collection<CheckPoint> points, Collection<CheckPoint> skipPoints) {
        for (CheckPoint point : points) {
            if (!skipPoints.contains(point) && userMatchCheckpoint(user, point)) {
                return point;
            }
        }
        return null;
    }

    private boolean userMatchCheckpoint(User user, CheckPoint checkPoint) {
        return GeoUtils.distance(user.getLatitude(), user.getLongitude(),
                checkPoint.getLatitude(), checkPoint.getLongitude()) < checkPoint.getRadius() ;
    }

}
