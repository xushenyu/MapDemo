package com.chengmao.mapdemo.bean;

import java.io.Serializable;

/**
 * Created by xsy on 2019/4/23 0023.
 */

public class LocationBean implements Serializable {
    private String latitude;
    private String longitude;
    private String address;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
