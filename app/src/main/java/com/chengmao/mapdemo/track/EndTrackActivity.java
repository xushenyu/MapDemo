package com.chengmao.mapdemo.track;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.chengmao.mapdemo.Alert;
import com.chengmao.mapdemo.MapApi;
import com.chengmao.mapdemo.R;
import com.chengmao.mapdemo.Util;
import com.chengmao.mapdemo.bean.EndEvent;
import com.chengmao.mapdemo.bean.LocationBean;
import com.chengmao.mapdemo.bean.StartTrackBean;
import com.cysion.baselib.Box;
import com.cysion.baselib.base.BaseActivity;
import com.cysion.baselib.cache.ACache;
import com.cysion.baselib.net.Caller;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xsy on 2019/4/23 0023.
 */

public class EndTrackActivity extends BaseActivity {

    private LocationBean start_location;
    private LocationBean end_location;
    private StartTrackBean startTrackBean;
    private EditText et_name;
    private TextView tv_type;
    private TextView tv_start_point;
    private TextView tv_end_point;
    private TextView tv_time;
    private OptionsPickerView wayPicker;
    private List<String> list = new ArrayList<>();
    private int mType = 1;
    private String name;
    private EditText et_desc;
    private String space;
    private long time;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_end_track;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        Intent intent = getIntent();
        start_location = (LocationBean) intent.getSerializableExtra("start_location");
        end_location = (LocationBean) intent.getSerializableExtra("end_location");
        startTrackBean = (StartTrackBean) intent.getSerializableExtra("track_bean");
        space = intent.getStringExtra("space");
        time = intent.getLongExtra("time", 0);
        et_name = findViewById(R.id.et_name);
        tv_type = findViewById(R.id.tv_type);
        tv_start_point = findViewById(R.id.tv_start_point);
        tv_end_point = findViewById(R.id.tv_end_point);
        tv_time = findViewById(R.id.tv_time);
        et_desc = findViewById(R.id.et_desc);
        tv_time.setText(Util.mills2Years(System.currentTimeMillis()));
        tv_start_point.setText(start_location.getAddress());
        tv_end_point.setText(end_location.getAddress());
        geoCoder();
        initListener();
        try {
            JSONObject jsonObject = new JSONObject(startTrackBean.getType().toString());
            String[] split = jsonObject.toString().split("[,]");
            for (int i = 0; i < split.length; i++) {
                list.add(jsonObject.getString((i + 1) + ""));
            }
            tv_type.setText(list.get(0));
        } catch (JSONException e) {
        }
    }

    private void geoCoder() {
    }

    private void initListener() {
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.tv_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitEndTrack();
            }
        });
        findViewById(R.id.ll_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wayPicker == null) {
                    wayPicker = new OptionsPickerBuilder(self, new OnOptionsSelectListener() {
                        @Override
                        public void onOptionsSelect(int options1, int options2, int options3, View v) {
                            tv_type.setText(list.get(options1));
                            mType = options1 + 1;
                        }
                    }).setContentTextSize(18).setCancelColor(Color.GRAY)
                            .setSubmitColor(Box.color(R.color.colorPrimary)).setSelectOptions(mType - 1).build();
                    wayPicker.setPicker(list);
                }
                wayPicker.show();
            }
        });
    }

    private void submitEndTrack() {
        Alert.obj().loading(this);
        name = et_name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            name = startTrackBean.getTrail_name();
        }
        String signature = ACache.get(this).getAsString("signature");
        Caller.obj().load(MapApi.class).end(signature, end_location.getLongitude() + "," + end_location.getLatitude(),
                startTrackBean.getTrail_id() + "", name, et_desc.getText().toString().trim(), mType + "", space, time, "1").enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Alert.obj().loaded();
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        finish();
                        EventBus.getDefault().post(new EndEvent());
                    }
                } catch (JSONException e) {
                    Toast.makeText(self, "数据返回异常，轨迹保存失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Alert.obj().loaded();
                Toast.makeText(self, "网络异常，轨迹保存失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
