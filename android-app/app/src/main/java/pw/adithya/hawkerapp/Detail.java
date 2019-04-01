package pw.adithya.hawkerapp;

import com.google.android.gms.maps.model.Marker;

public class Detail implements Comparable<Detail> {
    private String name;
    private String description;
    private String shortAddr;
    private String longAddr;
    private String placeID;
    private String photoURL = "";
    private float lat =  0f, lon = 0f, distance;
    private int noOfStalls;
    private Marker marker = null;

    public String getShortAddr() {
        return shortAddr;
    }

    public void setShortAddr(String shortAddr) {
        this.shortAddr = shortAddr;
    }

    public String getLongAddr() {
        return longAddr;
    }

    public void setLongAddr(String longAddr) {
        this.longAddr = longAddr;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public int getNoOfStalls() {
        return noOfStalls;
    }

    public void setNoOfStalls(int noOfStalls) {
        this.noOfStalls = noOfStalls;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    @Override
    public int compareTo(Detail d) {
        if (distance < d.getDistance())
            return -1;
        else if (distance > d.getDistance())
            return 1;

        return 0;
    }
}
