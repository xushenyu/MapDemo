package com.chengmao.mapdemo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xsy on 2019/4/24 0024.
 */

public class PointBean implements Serializable {

    /**
     * id : 17460
     * name : test
     * type : 1
     * contacts : null
     * phone : null
     * desc : null
     * size : null
     * pic : ["http://tradecdn.5dev.cn/upload/data/upload/6/2019/04/24/e7236e86b76009b00a1ed969922f2cb2.jpg"]
     * coords : 116.441476,39.923353
     * coords_address : null
     * city : null
     * product : null
     * source : 3
     * uid : 2
     * state : 1
     * cdate : 2019-04-24 15:29:37
     * file_id : 0
     * status : 1
     * audit : 1
     * trail_id : 60
     */

    private String id;
    private String name;
    private String type;
    private Object contacts;
    private Object phone;
    private Object desc;
    private Object size;
    private String coords;
    private Object coords_address;
    private Object city;
    private Object product;
    private String source;
    private String uid;
    private String state;
    private String cdate;
    private String file_id;
    private String status;
    private String audit;
    private String trail_id;
    private List<String> pic;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getContacts() {
        return contacts;
    }

    public void setContacts(Object contacts) {
        this.contacts = contacts;
    }

    public Object getPhone() {
        return phone;
    }

    public void setPhone(Object phone) {
        this.phone = phone;
    }

    public Object getDesc() {
        return desc;
    }

    public void setDesc(Object desc) {
        this.desc = desc;
    }

    public Object getSize() {
        return size;
    }

    public void setSize(Object size) {
        this.size = size;
    }

    public String getCoords() {
        return coords;
    }

    public void setCoords(String coords) {
        this.coords = coords;
    }

    public Object getCoords_address() {
        return coords_address;
    }

    public void setCoords_address(Object coords_address) {
        this.coords_address = coords_address;
    }

    public Object getCity() {
        return city;
    }

    public void setCity(Object city) {
        this.city = city;
    }

    public Object getProduct() {
        return product;
    }

    public void setProduct(Object product) {
        this.product = product;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAudit() {
        return audit;
    }

    public void setAudit(String audit) {
        this.audit = audit;
    }

    public String getTrail_id() {
        return trail_id;
    }

    public void setTrail_id(String trail_id) {
        this.trail_id = trail_id;
    }

    public List<String> getPic() {
        return pic;
    }

    public void setPic(List<String> pic) {
        this.pic = pic;
    }
}
