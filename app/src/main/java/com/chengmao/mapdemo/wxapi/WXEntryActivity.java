package com.chengmao.mapdemo.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.chengmao.mapdemo.Alert;
import com.chengmao.mapdemo.MainActivity;
import com.chengmao.mapdemo.MapApi;
import com.chengmao.mapdemo.UserInfo;
import com.chengmao.mapdemo.WXCaller;
import com.chengmao.mapdemo.WXToken;
import com.cysion.baselib.cache.ACache;
import com.cysion.baselib.net.Caller;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xsy on 2019/2/22 0022.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //如果没回调onResp，八成是这句没有写
        MainActivity.mWxApi.handleIntent(getIntent(), this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    //app发送消息给微信，处理返回消息的回调
    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {

            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        //拿到了微信返回的code,立马再去请求access_token
                        String code = ((SendAuth.Resp) resp).code;
                        //就在这个地方，用网络库什么的或者自己封的网络api，发请求去咯，注意是get请求
                        WXCaller.obj().load(MapApi.class).wxMsg("wxd54b71fe57741edc", "f337d8c5958eb66b9d85da29fa1c0ff5", code, "authorization_code").enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                String body = response.body();
                                Gson gson = new Gson();
                                WXToken wxToken = gson.fromJson(body, WXToken.class);
                                getUserInfo(wxToken);
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Alert.obj().loaded();
                                Toast.makeText(WXEntryActivity.this, "网络异常，登录失败", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                        break;

                    case RETURN_MSG_TYPE_SHARE:
//                        UIUtils.showToast("微信分享成功");
                        finish();
                        break;
                    default:
                        finish();
                        break;
                }
                break;
            default:
                finish();
                break;
        }
    }

    private void getUserInfo(final WXToken token) {
        WXCaller.obj().load(MapApi.class).getUserInfo(token.getAccess_token(), token.getOpenid(), "zh_CN").enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Gson gson = new Gson();
                UserInfo userInfo = gson.fromJson(response.body(), UserInfo.class);
                getSignature(token, userInfo);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Alert.obj().loaded();
                Toast.makeText(WXEntryActivity.this, "网络异常，验证失败", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void getSignature(WXToken token, final UserInfo info) {
        Caller.obj().load(MapApi.class).getSignature(info.getNickname(), info.getHeadimgurl(), info.getSex(),
                info.getCity(), info.getProvince(), info.getCountry(), info.getOpenid(),
                info.getUnionid(), token.getAccess_token(), token.getRefresh_token(), token.getExpires_in()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Alert.obj().loaded();
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONObject data = jsonObject.getJSONObject("data");
                    String signature = data.getString("signature");
                    ACache.get(WXEntryActivity.this).put("signature", signature);
                    ACache.get(WXEntryActivity.this).put("nickname", info.getNickname());
                    Toast.makeText(WXEntryActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(WXEntryActivity.this, "服务异常，信息读取失败", Toast.LENGTH_SHORT).show();
                }
                finish();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Alert.obj().loaded();
                Toast.makeText(WXEntryActivity.this, "网络异常，信息获取失败", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
