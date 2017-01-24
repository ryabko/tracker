package ru.kalcho.tracker.util;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 *
 */
public class JsonUtilsTest {

    @Test
    public void testJsonToObject() throws Exception {
//        Task expected = sampleTask();
//        Task actual = JsonUtils.jsonToObject(sampleJson(), Task.class);
//        assertEquals(expected.getId(), actual.getId());
//        assertEquals(expected.getTitle(), actual.getTitle());
//        assertEquals(expected.getCreationTime(), actual.getCreationTime());
//        assertEquals(expected.getScheduledTime(), actual.getScheduledTime());
//        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void testObjectToJSON() throws Exception {
//        assertEquals(sampleJson(), JsonUtils.objectToJSON(sampleTask()));
    }

//    private String sampleJson() {
//        return "{\n" +
//                "  \"id\" : 6,\n" +
//                "  \"title\" : \"помыть посуду\",\n" +
//                "  \"creationTime\" : \"2015-06-23T23:58:33\",\n" +
//                "  \"scheduledTime\" : \"2015-06-24T00:01:28\",\n" +
//                "  \"status\" : \"DONE\"\n" +
//                "}";
//    }

//    private Task sampleTask() {
//        Task task = new Task();
//        task.setId(6L);
//        task.setTitle("помыть посуду");
//        task.setCreationTime(LocalDateTime.parse("2015-06-23T23:58:33"));
//        task.setScheduledTime(LocalDateTime.parse("2015-06-24T00:01:28"));
//        task.setStatus(TaskStatus.DONE);
//        return task;
//    }

}