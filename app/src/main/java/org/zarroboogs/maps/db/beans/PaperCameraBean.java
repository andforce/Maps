package org.zarroboogs.maps.db.beans;

/**
 * Created by wangdiyuan on 15-7-13.
 */
public class PaperCameraBean {
    private int id;
    private String name;
    private String direction;
    private double latitude = 0.0f;
    private double longtitude = 0.0f;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }
}
