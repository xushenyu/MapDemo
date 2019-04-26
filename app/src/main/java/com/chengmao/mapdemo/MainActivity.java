package com.chengmao.mapdemo;

import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.track.AMapTrackClient;
import com.amap.api.track.ErrorCode;
import com.amap.api.track.OnTrackLifecycleListener;
import com.amap.api.track.TrackParam;
import com.chengmao.mapdemo.bean.EndEvent;
import com.chengmao.mapdemo.bean.LocationBean;
import com.chengmao.mapdemo.bean.StartTrackBean;
import com.chengmao.mapdemo.track.EndTrackActivity;
import com.chengmao.mapdemo.track.SimpleOnTrackLifecycleListener;
import com.chengmao.mapdemo.track.TrackListActivity;
import com.cysion.baselib.base.BaseActivity;
import com.cysion.baselib.cache.ACache;
import com.cysion.baselib.net.Caller;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements AMap.OnMyLocationChangeListener {
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
    private boolean isPausing;
    private StartTrackBean startTrackBean;
    private OnTrackLifecycleListener onTrackListener = new SimpleOnTrackLifecycleListener() {
        @Override
        public void onBindServiceCallback(int status, String msg) {
        }

        @Override
        public void onStartTrackCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.START_TRACK_SUCEE || status == ErrorCode.TrackListen.START_TRACK_SUCEE_NO_NETWORK) {
                // 成功启动
                isServiceRunning = true;
                isPausing = false;
                startGather();
                btnStatus();
            } else if (status == ErrorCode.TrackListen.START_TRACK_ALREADY_STARTED) {
                // 已经启动
                isServiceRunning = true;
                isPausing = false;
                startGather();
                btnStatus();
            } else {
                Alert.obj().loaded();
            }
        }

        @Override
        public void onStopTrackCallback(int status, String msg) {
            Alert.obj().loaded();
            if (status == ErrorCode.TrackListen.STOP_TRACK_SUCCE) {
                // 成功停止
                Toast.makeText(MainActivity.this, "轨迹暂停", Toast.LENGTH_SHORT).show();
                isServiceRunning = false;
                isGatherRunning = false;
                isPausing = true;
                btnStatus();
            }
        }

        @Override
        public void onStartGatherCallback(int status, String msg) {
            Alert.obj().loaded();
            if (status == ErrorCode.TrackListen.START_GATHER_SUCEE) {
                isGatherRunning = true;
                isPausing = false;
                btnStatus();
            } else if (status == ErrorCode.TrackListen.START_GATHER_ALREADY_STARTED) {
                isGatherRunning = true;
                isPausing = false;
                btnStatus();
            } else {
            }
        }

        @Override
        public void onStopGatherCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.STOP_GATHER_SUCCE) {
                isGatherRunning = false;
                isPausing = true;
                aMapTrackClient.stopTrack(new TrackParam(startTrackBean.getServiceId(), startTrackBean.getTerminalId()), onTrackListener);
            } else {
                Alert.obj().loaded();
            }
        }
    };
    private long startTime;
    private long currentTime = 0;
    private Location oldLocation;
    private float distance;
    private Location startLocation;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            currentTime = currentTime + 1000;
            tv_duration.setText(Util.mill2Hour(currentTime));
        }
    };
    private Timer timer;
    private Button btn_end;
    private LinearLayout ll_track;
    private String trailId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mMapView = findViewById(R.id.mapView);
        pathBtn = findViewById(R.id.locationbtn);
        tv_name = findViewById(R.id.tv_name);
        tv_desc = findViewById(R.id.tv_desc);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_duration = findViewById(R.id.tv_duration);
        tv_distance = findViewById(R.id.tv_distance);
        btn_end = findViewById(R.id.btn_end);
        ll_track = findViewById(R.id.ll_track);
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
                        JSONObject type = data.getJSONObject("type");
                        Gson gson = new Gson();
                        startTrackBean = gson.fromJson(data.toString(), StartTrackBean.class);
                        if (startTrackBean == null) {
                            Toast.makeText(MainActivity.this, "数据返回异常，轨迹开启失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        startTrackBean.setType(type.toString());
                        trailId = startTrackBean.getTrail_id();
                        startTrack();
                    } else {
                        Toast.makeText(MainActivity.this, "网络异常，轨迹开启失败", Toast.LENGTH_SHORT).show();
                        Alert.obj().loaded();
                    }
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "网络异常，轨迹开启失败", Toast.LENGTH_SHORT).show();
                    Alert.obj().loaded();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Alert.obj().loaded();
            }
        });
    }

    private void startGather() {
        aMapTrackClient.setTrackId(startTrackBean.getTrackId());
        aMapTrackClient.startGather(onTrackListener);
    }

    private void startTrack() {
        TrackParam trackParam = new TrackParam(startTrackBean.getServiceId(), startTrackBean.getTerminalId());
        trackParam.setTrackId(startTrackBean.getTrackId());
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            trackParam.setNotification(createNotification());
        }
        aMapTrackClient.startTrack(trackParam, onTrackListener);
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
                    pauseRecordTrack();
                } else {
                    Alert.obj().loading(MainActivity.this);
                    getStartTrack();
                    startTime = System.currentTimeMillis();
                    tv_start_time.setText(Util.mills2Date(startTime));
                    timer = new Timer();
                    TimerTask timerTask = new TimerTask() {

                        @Override
                        public void run() {
                            mHandler.sendEmptyMessage(111);
                        }
                    };
                    timer.schedule(timerTask, 0, 1000);
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
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEndAct();
            }
        });
    }

    private void startEndAct() {
        if (startLocation == null || oldLocation == null) {
            Toast.makeText(MainActivity.this, "行程太短，无法记录轨迹，再走走", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(MainActivity.this, EndTrackActivity.class);
        LocationBean startBean = new LocationBean();
        startBean.setLongitude(startLocation.getLongitude() + "");
        startBean.setLatitude(startLocation.getLatitude() + "");
        startBean.setAddress(getAddress(startLocation));
        LocationBean endBean = new LocationBean();
        endBean.setLongitude(oldLocation.getLongitude() + "");
        endBean.setLatitude(oldLocation.getLatitude() + "");
        endBean.setAddress(getAddress(oldLocation));
        intent.putExtra("start_location", startBean);
        intent.putExtra("end_location", endBean);
        intent.putExtra("track_bean", startTrackBean);
        intent.putExtra("space", fixDistance(distance));
        intent.putExtra("time", currentTime / 1000);
        startActivity(intent);
    }

    private void initMap() {
        // 不要使用Activity作为Context传入
        aMapTrackClient = new AMapTrackClient(getApplicationContext());
        aMapTrackClient.setInterval(2, 20);

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
        intent.putExtra("trail_id", trailId);
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
        if (isServiceRunning && isGatherRunning) {
            pathBtn.setText("轨迹暂停");
        } else {
            if (isPausing) {
                pathBtn.setText("轨迹继续");
            } else {
                pathBtn.setText("轨迹开始");
            }
        }
        ll_track.setVisibility(!isPausing && !isServiceRunning && !isGatherRunning ? View.GONE : View.VISIBLE);
        tv_desc.setText(isServiceRunning && isGatherRunning ? "轨迹记录中" : "轨迹已结束");
        btn_end.setVisibility(isPausing ? View.VISIBLE : View.GONE);
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
        aMapTrackClient.stopGather(onTrackListener);
    }

    @Override
    public void onMyLocationChange(Location location) {
        String nickname = ACache.get(this).getAsString("nickname");
        if (!TextUtils.isEmpty(nickname))
            tv_name.setText(nickname);
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            if (isServiceRunning && isGatherRunning) {
                if (oldLocation != null) {
                    float f = AMapUtils.calculateLineDistance(new LatLng(oldLocation.getLatitude(), oldLocation.getLongitude()), new LatLng(latitude, longitude));
                    distance = distance + f;
//                    Toast.makeText(self, f + "====" + fixDistance(f) + "====" + fixDistance(distance), Toast.LENGTH_SHORT).show();
                    Log.e("flag--","onMyLocationChange(MainActivity.java:423)-->>"+f + "====" + fixDistance(f) + "====" + fixDistance(distance));
                } else {
                    startLocation = location;
                }
                oldLocation = location;
                tv_distance.setText(fixDistance(distance));
            }
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

    private String getAddress(Location location) {
        String[] split = location.toString().split("[#]");
        for (int i = 0; i < split.length; i++) {
            if (split[i].startsWith("address")) {
                String[] split1 = split[i].split("[=]");
                if (split1.length > 1) {
                    return split1[1];
                }
            }
        }
        return "";
    }

    //仅用来声明默认接收方法的，实际不应该接收任何消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fromEventBus(EndEvent event) {
        currentTime = 0;
        distance = 0;
        trailId = "";
        isPausing = false;
        tv_distance.setText("0.0");
        btnStatus();
    }

    //暂停
    private void pauseRecordTrack() {
        aMapTrackClient.stopGather(onTrackListener);
        timer.cancel();
        timer = null;
    }

    private String fixDistance(float f) {
        BigDecimal d1 = new BigDecimal(Double.toString(f));
        BigDecimal d2 = new BigDecimal(Integer.toString(1));
        // 四舍五入,保留2位小数
        return d1.divide(d2,1,BigDecimal.ROUND_HALF_UP).toString();
//        BigDecimal num = new BigDecimal(f);
//        DecimalFormat df = new DecimalFormat("0.0");
//        String res = df.format(num);
    }
}
