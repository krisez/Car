package cn.krisez.car.entity;

import com.amap.api.maps.model.LatLng;

public class CarRoute {

    /**
     * systime : 2016-08-03 16:34:16
     * lon : 106.499405
     * loctime : 1470213257000
     * address :
     * speed : 60.0
     * bearing : 221.36236572265625
     * provider : gps
     * accuracy : 12
     * lat : 29.616063
     */

    private String systime;
    private double lon;
    private long loctime;
    private String address;
    private double speed;
    private double bearing;
    private String provider;
    private int accuracy;
    private double lat;

    public CarRoute(double lon,double lat,double speed, double bearing) {
        this.lon = lon;
        this.speed = speed;
        this.bearing = bearing;
        this.lat = lat;
    }

    public String getSystime() {
        return systime;
    }

    public void setSystime(String systime) {
        this.systime = systime;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public long getLoctime() {
        return loctime;
    }

    public void setLoctime(long loctime) {
        this.loctime = loctime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public LatLng getLatLng(){
        return new LatLng(lat,lon);
    }


}
