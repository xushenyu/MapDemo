package com.chengmao.mapdemo;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by xsy on 2019/2/22 0022.
 */

public interface MapApi {
    @FormUrlEncoded
    @POST("dot/?l=tool.add")
    Call<String> setMark(@FieldMap Map<String, String> params);

    @GET("dot/?l=tool.getType")
    Call<String> getType(@Query("signature") String signature);

    @GET("oauth2/access_token?")
    Call<String> wxMsg(@Query("appid") String appid, @Query("secret") String secret, @Query("code") String code, @Query("grant_type") String grant_type);

    @GET("userinfo?")
    Call<String> getUserInfo(@Query("access_token") String access_token, @Query("openid") String openid, @Query("lang") String lang);

    @GET("passport/?l=app.wechat_reg")
    Call<String> getSignature(@Query("username") String username, @Query("avatar") String avatar, @Query("sex") String sex,
                              @Query("city") String city, @Query("province") String province,
                              @Query("country") String country, @Query("openid") String openid,
                              @Query("unionid") String unionid, @Query("access_token") String access_token,
                              @Query("refresh_token") String refresh_token, @Query("expires_in") String expires_in);

    @GET("amap/?l=api.start")
    Call<String> start(@Query("signature") String signature, @Query("start_coords") String start_coords);

    @GET("amap/?l=api.end")
    Call<String> end(@Query("signature") String signature, @Query("end_coords") String end_coords,
                     @Query("trail_id") String trail_id, @Query("name") String name,
                     @Query("desc") String desc, @Query("type") String type, @Query("space") float space, @Query("time") long time, @Query("json") String json);

    @GET("amap/?l=api.trail")
    Call<String> trackList(@Query("signature") String signature, @Query("pg") int pg);

    @GET("dot/?l=tool.getFoot")
    Call<String> pointList(@Query("signature") String signature, @Query("trail_id") String trail_id, @Query("pg") int pg);
}
