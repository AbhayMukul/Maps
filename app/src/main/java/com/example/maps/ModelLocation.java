package com.example.maps;

import java.util.Date;

public class ModelLocation {
    double Latitude;
    double Longitude;
    String Time;

    public ModelLocation() {
    }

    public ModelLocation(double latitude, double longitude, String time) {
        Latitude = latitude;
        Longitude = longitude;
        Time = time;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
