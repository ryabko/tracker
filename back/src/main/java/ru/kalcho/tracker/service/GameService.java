package ru.kalcho.tracker.service;

import ru.kalcho.tracker.model.CheckPoint;
import ru.kalcho.tracker.model.GameState;
import ru.kalcho.tracker.model.User;
import ru.kalcho.tracker.model.UserState;
import ru.kalcho.tracker.util.GeoUtils;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 *
 */
public class GameService {

    private UserService userService;

    private CheckPointService checkPointService;

    private DestinationService destinationService;
    
    private int activeTimeout;

    public GameService(UserService userService, CheckPointService checkPointService,
                       DestinationService destinationService, int activeTimeout) {
        this.userService = userService;
        this.checkPointService = checkPointService;
        this.destinationService = destinationService;
        this.activeTimeout = activeTimeout;
    }

    public GameState obtainGameState(String pin) {
        GameState state = new GameState();

        List<User> users = userService.findByPin(pin, activeTimeout);

        LocalDateTime teamStartDate = userService.findTeamStartDate(pin);
        List<CheckPoint> points = checkPointService.findForTeam(teamStartDate);

        Map<User, CheckPoint> usersCheckPoints = new HashMap<>();

        for (User user : users) {
            CheckPoint match = findMatch(user, points, usersCheckPoints.values());
            usersCheckPoints.put(user, match);
        }

        boolean isDestinationPlayer = destinationService.isDestinationUserPin(pin);
        state.setDestinationPlayer(isDestinationPlayer);

        if (!isDestinationPlayer) {
            state.setCheckPoints(points);
        } else {
            state.setCheckPoints(Collections.emptyList());
        }
        state.setPlayers(users.stream()
                .map(u -> new UserState(u, usersCheckPoints.get(u) != null))
                .collect(toList())
        );

        if (points.size() > 0 && points.size() == usersCheckPoints.values().stream().filter(Objects::nonNull).count()) {
            state.setDestination(destinationService.obtainDestinationUser(activeTimeout));
        }

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
