package ru.kalcho.tracker.util;

import static java.lang.Math.*;

/**
 *
 */
public class GeoUtils {

    public static final int DEGREE_LENGTH = 111278;

    public static final int EARTH_RADIUS = 6372795;

    public static int distance(Float latitude1, Float longitude1,
                               Float latitude2, Float longitude2) {
        return (int) (DEGREE_LENGTH * acos(sin(latitude1) * sin(latitude2) +
                        cos(latitude1) * cos(latitude2) * cos(longitude2 - longitude1)));
    }

    public static void main(String[] args) {
        System.out.println(distance(10f, 10f, 9f, 10f));
        System.out.println(distance(51.661571f, 39.208168f, 51.664440f, 39.211429f));
        System.out.println(distance2(51.661571f, 39.208168f, 51.664440f, 39.211429f));
    }

    public static int distance2(Float lat1, Float long1, Float lat2, Float long2) {
        return (int) (DEGREE_LENGTH * 2 * asin(sqrt(pow(sin((lat2 - lat1) / 2), 2)
                + cos(lat1) * cos(lat2) * pow(sin((long2 - long1) / 2), 2))));
    }

}
