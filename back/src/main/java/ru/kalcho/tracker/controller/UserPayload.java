package ru.kalcho.tracker.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
public class UserPayload {

    private String pin;

    @JsonProperty("lat")
    private Float latitude;

    @JsonProperty("long")
    private Float longitude;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
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
