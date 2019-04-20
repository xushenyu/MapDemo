package com.chengmao.mapdemo.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xsy on 2019/4/19 0019.
 */

public class StartTrackBean {

    /**
     * type : {"1":"步行","2":"骑行","3":"驾车","4":"其他"}
     * serviceId : 31223
     * terminalId : 76105421
     * trackId : 60
     * trail_id : 2
     * trail_name : Sean在2019-04-18 16:48生成的轨迹
     * start_coords : null
     */

    private TypeBean type;
    private int serviceId;
    private int terminalId;
    private int trackId;
    private String trail_id;
    private String trail_name;
    private String start_coords;

    public TypeBean getType() {
        return type;
    }

    public void setType(TypeBean type) {
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

    public String getTrail_id() {
        return trail_id;
    }

    public void setTrail_id(String trail_id) {
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

    public static class TypeBean {
        /**
         * 1 : 步行
         * 2 : 骑行
         * 3 : 驾车
         * 4 : 其他
         */

        @SerializedName("1")
        private String _$1;
        @SerializedName("2")
        private String _$2;
        @SerializedName("3")
        private String _$3;
        @SerializedName("4")
        private String _$4;

        public String get_$1() {
            return _$1;
        }

        public void set_$1(String _$1) {
            this._$1 = _$1;
        }

        public String get_$2() {
            return _$2;
        }

        public void set_$2(String _$2) {
            this._$2 = _$2;
        }

        public String get_$3() {
            return _$3;
        }

        public void set_$3(String _$3) {
            this._$3 = _$3;
        }

        public String get_$4() {
            return _$4;
        }

        public void set_$4(String _$4) {
            this._$4 = _$4;
        }
    }
}
