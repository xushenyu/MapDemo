package com.chengmao.mapdemo;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.track.AMapTrackClient;
import com.amap.api.track.ErrorCode;
import com.amap.api.track.OnTrackLifecycleListener;
import com.amap.api.track.TrackParam;
import com.amap.api.track.query.model.AddTrackRequest;
import com.amap.api.track.query.model.AddTrackResponse;
import com.chengmao.mapdemo.bean.StartTrackBean;
import com.chengmao.mapdemo.track.SimpleOnTrackLifecycleListener;
import com.chengmao.mapdemo.track.SimpleOnTrackListener;
import com.chengmao.mapdemo.track.TrackListActivity;
import com.cysion.baselib.cache.ACache;
import com.cysion.baselib.net.Caller;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AMap.OnMyLocationChangeListener {
    private static final String CHANNEL_ID_SERVICE_RUNNING = "CHANNEL_ID_SERVICE_RUNNING";
    private TextureMapView mMapView;
    private MyLocationStyle myLocationStyle;
    private double longitude;
    private double latitude;
    public static IWXAPI mWxApi;
    private Button pathBtn;
    private AMap aMap;
    private TextView tv_name;
    private TextView tv_desc;
    private TextView tv_start_time;
    private TextView tv_duration;
    private TextView tv_distance;
    private AMapTrackClient aMapTrackClient;
    private boolean isServiceRunning;
    private boolean isGatherRunning;
    private StartTrackBean startTrackBean;
    private long trackId;
    private OnTrackLifecycleListener onTrackListener = new SimpleOnTrackLifecycleListener() {
        @Override
        public void onBindServiceCallback(int status, String msg) {
        }

        @Override
        public void onStartTrackCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.START_TRACK_SUCEE || status == ErrorCode.TrackListen.START_TRACK_SUCEE_NO_NETWORK) {
                // 成功启动
                Log.e("flag--", "onStartTrackCallback(MainActivity.java:78)-->>" + "启动服务成功");
                isServiceRunning = true;
                startGather();
                btnStatus();
            } else if (status == ErrorCode.TrackListen.START_TRACK_ALREADY_STARTED) {
                // 已经启动
                isServiceRunning = true;
                startGather();
                btnStatus();
            } else {
            }
        }

        @Override
        public void onStopTrackCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.STOP_TRACK_SUCCE) {
                // 成功停止
                Toast.makeText(MainActivity.this, "停止服务成功", Toast.LENGTH_SHORT).show();
                isServiceRunning = false;
                isGatherRunning = false;
                submitEndTrack();
                btnStatus();
            } else {
            }
        }

        @Override
        public void onStartGatherCallback(int status, String msg) {
            Log.e("flag--", "onStartGatherCallback(MainActivity.java:109)-->>" + msg);
            if (status == ErrorCode.TrackListen.START_GATHER_SUCEE) {
//                Toast.makeText(MainActivity.this, "定位采集开启成功", Toast.LENGTH_SHORT).show();
                isGatherRunning = true;
                btnStatus();
            } else if (status == ErrorCode.TrackListen.START_GATHER_ALREADY_STARTED) {
//                Toast.makeText(MainActivity.this, "定位采集已经开启", Toast.LENGTH_SHORT).show();
                isGatherRunning = true;
                btnStatus();
            } else {
            }
        }

        @Override
        public void onStopGatherCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.STOP_GATHER_SUCCE) {
                Toast.makeText(MainActivity.this, "定位采集停止成功", Toast.LENGTH_SHORT).show();
                isGatherRunning = false;
                aMapTrackClient.stopTrack(new TrackParam(startTrackBean.getServiceId(), startTrackBean.getTerminalId()), onTrackListener);
            } else {
            }
        }
    };

    private void submitEndTrack() {
        String signature = ACache.get(this).getAsString("signature");
        Caller.obj().load(MapApi.class).end(signature, longitude + "," + latitude,
                startTrackBean.getTrail_id(), startTrackBean.getTrail_name(), "", "1", "1").enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        Log.e("flag--", "onResponse(MainActivity.java:139)-->>" + "结束成功");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = findViewById(R.id.mapView);
        pathBtn = findViewById(R.id.locationbtn);
        tv_name = findViewById(R.id.tv_name);
        tv_desc = findViewById(R.id.tv_desc);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_duration = findViewById(R.id.tv_duration);
        tv_distance = findViewById(R.id.tv_distance);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        initMap();
        //AppConst.WEIXIN.APP_ID是指你应用在微信开放平台上的AppID，记得替换。
        mWxApi = WXAPIFactory.createWXAPI(this, "wxd54b71fe57741edc", true);
        // 将该app注册到微信
        mWxApi.registerApp("wxd54b71fe57741edc");
        initClick();
    }

    private void getStartTrack() {
        String signature = ACache.get(this).getAsString("signature");
        Caller.obj().load(MapApi.class).start(signature, longitude + "," + latitude).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        Gson gson = new Gson();
                        startTrackBean = gson.fromJson(data.toString(), StartTrackBean.class);
                        if (startTrackBean == null) {
                            Toast.makeText(MainActivity.this, "数据返回异常，轨迹开启失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        startTrack();
                    } else {
                        Toast.makeText(MainActivity.this, "网络异常，轨迹开启失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "网络异常，轨迹开启失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void startGather() {
        aMapTrackClient.setTrackId(trackId);
        aMapTrackClient.startGather(onTrackListener);
    }

    private void startTrack() {
        aMapTrackClient.addTrack(new AddTrackRequest(startTrackBean.getServiceId(), startTrackBean.getTerminalId()), new SimpleOnTrackListener() {
            @Override
            public void onAddTrackCallback(AddTrackResponse addTrackResponse) {
                if (addTrackResponse.isSuccess()) {
                    // trackId需要在启动服务后设置才能生效，因此这里不设置，而是在startGather之前设置了track id
                    trackId = addTrackResponse.getTrid();
                    TrackParam trackParam = new TrackParam(startTrackBean.getServiceId(), startTrackBean.getTerminalId());
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        trackParam.setNotification(createNotification());
                    }
                    aMapTrackClient.startTrack(trackParam, onTrackListener);
                } else {
                    Toast.makeText(MainActivity.this, "网络请求失败，" + addTrackResponse.getErrorMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void initClick() {
        pathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String signature = ACache.get(MainActivity.this).getAsString("signature");
                if (TextUtils.isEmpty(signature)) {
                    wxLogin();
                    return;
                }
                if (isServiceRunning && isGatherRunning) {
                    aMapTrackClient.stopGather(onTrackListener);
                } else {
                    Alert.obj().loading(MainActivity.this);
                    getStartTrack();
                }
            }
        });
        findViewById(R.id.tv_track_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String signature = ACache.get(MainActivity.this).getAsString("signature");
                if (TextUtils.isEmpty(signature)) {
                    wxLogin();
                    return;
                }
                startActivity(new Intent(MainActivity.this, TrackListActivity.class));
            }
        });
    }

    private void initMap() {
        // 不要使用Activity作为Context传入
        aMapTrackClient = new AMapTrackClient(getApplicationContext());
        aMapTrackClient.setInterval(5, 30);

        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14));

        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
        myLocationStyle.interval(2000);
        myLocationStyle.showMyLocation(true);//设置定位蓝点的icon图标方法，需要用到BitmapDescriptor类对象作为参数。
        myLocationStyle.radiusFillColor(Color.parseColor("#33999999"));
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setOnMyLocationChangeListener(this);
    }

    public void mark(View view) {
        String signature = ACache.get(this).getAsString("signature");
        if (TextUtils.isEmpty(signature)) {
            wxLogin();
            return;
        }
        if (longitude == 0.0 || latitude == 0.0) {
            Toast.makeText(this, "网络异常，还未获取到位置信息，请稍后重试！", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, MarkActivity.class);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        startActivity(intent, ActivityOptions.
                makeSceneTransitionAnimation(this, view, "mark").toBundle());
    }

    public void wxLogin() {
        if (!mWxApi.isWXAppInstalled()) {
            Toast.makeText(this, "你还未安装微信客户端", Toast.LENGTH_SHORT).show();
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        mWxApi.sendReq(req);
    }

    private void btnStatus() {
        pathBtn.setText(isServiceRunning && isGatherRunning ? "轨迹结束" : "轨迹开始");
        tv_desc.setText(isServiceRunning && isGatherRunning ? "轨迹记录中" : "轨迹已结束");
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onMyLocationChange(Location location) {
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
    }

    /**
     * 在8.0以上手机，如果app切到后台，系统会限制定位相关接口调用频率
     * 可以在启动轨迹上报服务时提供一个通知，这样Service启动时会使用该通知成为前台Service，可以避免此限制
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private Notification createNotification() {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_SERVICE_RUNNING, "app service", NotificationManager.IMPORTANCE_LOW);
            nm.createNotificationChannel(channel);
            builder = new Notification.Builder(getApplicationContext(), CHANNEL_ID_SERVICE_RUNNING);
        } else {
            builder = new Notification.Builder(getApplicationContext());
        }
        Intent nfIntent = new Intent(MainActivity.this, MainActivity.class);
        nfIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        builder.setContentIntent(PendingIntent.getActivity(MainActivity.this, 0, nfIntent, 0))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("橙猫门窗运行中")
                .setContentText("橙猫门窗运行中");
        Notification notification = builder.build();
        return notification;
    }

}
