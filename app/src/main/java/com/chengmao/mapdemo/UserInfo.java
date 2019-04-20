package com.chengmao.mapdemo;

import java.util.List;

/**
 * Created by xsy on 2019/2/25 0025.
 */

public class UserInfo {

    /**
     * openid : oh1Sh5ywJY0UOvtn5dFWQFr90mcg
     * nickname : Sean
     * sex : 1
     * language : zh_CN
     * city : 枣庄
     * province : 山东
     * country : 中国
     * headimgurl : http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIl03YHWlzIHrh7jFZ4vOXxqNnd0XMficUCbAUFZcFauy7bVBBjhW2ic7KCt2oy45quDWoE1gnlZb5w/132
     * privilege : []
     * unionid : oo-x51TYEQwDiKRXLbP_s--OLfz4
     */

    private String openid;
    private String nickname;
    private String sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;
    private String unionid;
    private List<?> privilege;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public List<?> getPrivilege() {
        return privilege;
    }

    public void setPrivilege(List<?> privilege) {
        this.privilege = privilege;
    }
}
