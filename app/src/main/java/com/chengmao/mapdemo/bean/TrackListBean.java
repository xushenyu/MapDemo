package com.chengmao.mapdemo.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xsy on 2019/4/20 0020.
 */

public class TrackListBean implements Serializable {

    /**
     * type : {"1":"步行","2":"骑行","3":"驾车","4":"其他"}
     * serviceId : 31223
     * terminalId : 76105421
     * trail : [{"trail_id":2,"name":"Sean在2019-04-18 16:48生成的轨迹","trackId":60,"type":1,"start_coords":"116.441467,39.923356","end_coords":"116.442372,39.924485","start":"2019-04-18 16:48","end":"2019-04-20 14:50"}]
     */

    private TypeBean type;
    private long serviceId;
    private long terminalId;
    private List<TrailBean> trail;

    public TypeBean getType() {
        return type;
    }

    public void setType(TypeBean type) {
        this.type = type;
    }

    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    public long getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(long terminalId) {
        this.terminalId = terminalId;
    }

    public List<TrailBean> getTrail() {
        return trail;
    }

    public void setTrail(List<TrailBean> trail) {
        this.trail = trail;
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

    public static class TrailBean implements Serializable {
        /**
         * trail_id : 2
         * name : Sean在2019-04-18 16:48生成的轨迹
         * trackId : 60
         * type : 1
         * start_coords : 116.441467,39.923356
         * end_coords : 116.442372,39.924485
         * start : 2019-04-18 16:48
         * end : 2019-04-20 14:50
         */

        private int trail_id;
        private String name;
        private int trackId;
        private int type;
        private String start_coords;
        private String end_coords;
        private String start;
        private String end;
        private long service_id;
        private long termianl_id;
        private String space;
        private String time;
        private String dotnum;

        public String getSpace() {
            return space;
        }

        public void setSpace(String space) {
            this.space = space;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getDotnum() {
            return dotnum;
        }

        public void setDotnum(String dotnum) {
            this.dotnum = dotnum;
        }

        public long getService_id() {
            return service_id;
        }

        public void setService_id(long service_id) {
            this.service_id = service_id;
        }

        public long getTermianl_id() {
            return termianl_id;
        }

        public void setTermianl_id(long termianl_id) {
            this.termianl_id = termianl_id;
        }

        public int getTrail_id() {
            return trail_id;
        }

        public void setTrail_id(int trail_id) {
            this.trail_id = trail_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getTrackId() {
            return trackId;
        }

        public void setTrackId(int trackId) {
            this.trackId = trackId;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getStart_coords() {
            return start_coords;
        }

        public void setStart_coords(String start_coords) {
            this.start_coords = start_coords;
        }

        public String getEnd_coords() {
            return end_coords;
        }

        public void setEnd_coords(String end_coords) {
            this.end_coords = end_coords;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }
    }
}
