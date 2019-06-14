package ru.kalcho.tracker.controller;

import org.junit.Test;
import ru.kalcho.tracker.model.CheckPoint;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CheckPointsSerializerTest {

    @Test
    public void serialize() {
        CheckPoint checkPoint1 = new CheckPoint();
        checkPoint1.setId(1L);
        checkPoint1.setName("name1");
        checkPoint1.setLatitude(51.101010F);
        checkPoint1.setLongitude(39.202020F);
        checkPoint1.setRadius(50);
        checkPoint1.setGroupIndex(1);

        CheckPoint checkPoint2 = new CheckPoint();
        checkPoint2.setId(2L);
        checkPoint2.setName("name2");
        checkPoint2.setLatitude(51.202020F);
        checkPoint2.setLongitude(39.303030F);
        checkPoint2.setRadius(40);
        checkPoint2.setGroupIndex(2);

        String serialized = CheckPointsSerializer.serialize(Arrays.asList(checkPoint1, checkPoint2));
        assertEquals("name1,51.10101,39.20202,50,1\n" +
                "name2,51.20202,39.30303,40,2", serialized);
    }

    @Test
    public void deserialize() {
        List<CheckPoint> deserialized = CheckPointsSerializer.deserialize("name1,51.10101,39.20202,50,1\n" +
                "name2,51.20202,39.30303,40,2");

        assertNotNull(deserialized);
        assertEquals(2, deserialized.size());

        CheckPoint checkPoint1 = deserialized.get(0);
        assertEquals("name1", checkPoint1.getName());
        assertEquals(51.101010F, checkPoint1.getLatitude(), 0.000001);
        assertEquals(39.202020F, checkPoint1.getLongitude(), 0.000001);
        assertEquals(50, (int) checkPoint1.getRadius());
        assertEquals(1, (int) checkPoint1.getGroupIndex());

        CheckPoint checkPoint2 = deserialized.get(1);
        assertEquals("name2", checkPoint2.getName());
        assertEquals(51.202020F, checkPoint2.getLatitude(), 0.000001);
        assertEquals(39.303030F, checkPoint2.getLongitude(), 0.000001);
        assertEquals(40, (int) checkPoint2.getRadius());
        assertEquals(2, (int) checkPoint2.getGroupIndex());
    }

}