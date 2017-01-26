package ru.kalcho.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 */
public class Destination {

    @JsonIgnore
    private String name;
    private Float latitude;
    private Float longitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }
}
