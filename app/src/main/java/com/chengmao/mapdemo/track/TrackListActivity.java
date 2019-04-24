package com.chengmao.mapdemo.track;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chengmao.mapdemo.MapApi;
import com.chengmao.mapdemo.R;
import com.chengmao.mapdemo.bean.TrackListBean;
import com.cysion.baselib.cache.ACache;
import com.cysion.baselib.net.Caller;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xsy on 2019/4/20 0020.
 */

public class TrackListActivity extends AppCompatActivity {

    private RecyclerView rv_track;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);
        rv_track = findViewById(R.id.rv_track);
        findViewById(R.id.fl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_track.setLayoutManager(layoutManager);
        String signature = ACache.get(this).getAsString("signature");
        Caller.obj().load(MapApi.class).trackList(signature).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONObject data = jsonObject.getJSONObject("data");
                    Gson gson = new Gson();
                    final TrackListBean trackListBean = gson.fromJson(data.toString(), TrackListBean.class);
                    if (trackListBean.getTrail() != null) {
                        TackAdapter tackAdapter = new TackAdapter(TrackListActivity.this, trackListBean.getTrail());
                        tackAdapter.setOnItemClickListenet(new TackAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(TrackListBean.TrailBean bean) {
                                bean.setService_id(trackListBean.getServiceId());
                                bean.setTermianl_id(trackListBean.getTerminalId());
                                Intent intent = new Intent(TrackListActivity.this, TrackDetailActivity.class);
                                intent.putExtra("track", bean);
                                startActivity(intent);
                            }
                        });
                        rv_track.setAdapter(tackAdapter);
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
}
