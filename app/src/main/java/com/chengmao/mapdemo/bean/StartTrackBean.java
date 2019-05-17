package com.chengmao.mapdemo.bean;

import java.io.Serializable;

/**
 * Created by xsy on 2019/4/19 0019.
 */

public class StartTrackBean implements Serializable {

    /**
     * type : {"1":"步行","2":"骑行","3":"驾车","4":"其他"}
     * serviceId : 31223
     * terminalId : 76105421
     * trackId : 60
     * trail_id : 2
     * trail_name : Sean在2019-04-18 16:48生成的轨迹
     * start_coords : null
     */

    private Object type;
    private int serviceId;
    private int terminalId;
    private int trackId;
    private int trail_id;
    private String trail_name;
    private String start_coords;

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(int terminalId) {
        this.terminalId = terminalId;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public int getTrail_id() {
        return trail_id;
    }

    public void setTrail_id(int trail_id) {
        this.trail_id = trail_id;
    }

    public String getTrail_name() {
        return trail_name;
    }

    public void setTrail_name(String trail_name) {
        this.trail_name = trail_name;
    }

    public String getStart_coords() {
        return start_coords;
    }

    public void setStart_coords(String start_coords) {
        this.start_coords = start_coords;
    }
}
