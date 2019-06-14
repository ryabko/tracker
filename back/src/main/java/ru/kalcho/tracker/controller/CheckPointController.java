package ru.kalcho.tracker.controller;

import ru.kalcho.tracker.model.CheckPoint;
import ru.kalcho.tracker.service.CheckPointService;

import java.util.List;

import static spark.Spark.*;

public class CheckPointController {

    private CheckPointService checkPointService;

    public CheckPointController(CheckPointService checkPointService) {
        this.checkPointService = checkPointService;
    }

    public void init() {
        get("/api/check-points", "text/plain", (request, response) -> {
            List<CheckPoint> checkPoints = checkPointService.findAll();
            return CheckPointsSerializer.serialize(checkPoints);
        });

        put("/api/check-points", "text/plain", (request, response) -> {
            List<CheckPoint> checkPoints = CheckPointsSerializer.deserialize(request.body());
            checkPointService.updateCheckPoints(checkPoints);
            return "OK";
        });
    }
}
