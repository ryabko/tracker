package ru.kalcho.tracker.controller;

import com.sun.deploy.util.StringUtils;
import ru.kalcho.tracker.model.CheckPoint;

import java.util.ArrayList;
import java.util.List;

public class CheckPointsSerializer {

    public static String serialize(List<CheckPoint> checkPoints) {
        List<String> lines = new ArrayList<>();
        for (CheckPoint checkPoint : checkPoints) {
            String line = checkPoint.getName() + "," +
                    checkPoint.getLatitude() + "," +
                    checkPoint.getLongitude() + "," +
                    checkPoint.getRadius() + "," +
                    checkPoint.getGroupIndex();
            lines.add(line);
        }
        return StringUtils.join(lines, "\n");
    }

    public static List<CheckPoint> deserialize(String serialized) {
        String[] lines = serialized.split("\n");
        List<CheckPoint> checkPoints = new ArrayList<>();
        for (String line : lines) {
            String[] fields = line.split(",");
            CheckPoint checkPoint = new CheckPoint();
            checkPoint.setName(fields[0].trim());
            checkPoint.setLatitude(Float.parseFloat(fields[1].trim()));
            checkPoint.setLongitude(Float.parseFloat(fields[2].trim()));
            checkPoint.setRadius(Integer.parseInt(fields[3].trim()));
            checkPoint.setGroupIndex(Integer.parseInt(fields[4].trim()));

            checkPoints.add(checkPoint);
        }
        return checkPoints;
    }

}
