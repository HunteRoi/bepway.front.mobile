package com.henallux.bepway.model;

import java.io.Serializable;

public class Coordinate implements Serializable {
    private double latitude;
    private double longitude;

    public Coordinate(){}

    public Coordinate(double latitude, double longitude) {
       setLatitude(latitude);
       setLongitude(longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
