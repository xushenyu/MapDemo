package com.chengmao.mapdemo.track;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chengmao.mapdemo.Alert;
import com.chengmao.mapdemo.MapApi;
import com.chengmao.mapdemo.R;
import com.chengmao.mapdemo.bean.TrackListBean;
import com.cysion.baselib.cache.ACache;
import com.cysion.baselib.net.Caller;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xsy on 2019/4/20 0020.
 */

public class TrackListActivity extends AppCompatActivity {

    private RecyclerView rv_track;
    private MySmartRefreshLayout smart_refresh;
    private int page = 1;
    private TackAdapter tackAdapter;
    private List<TrackListBean.TrailBean> mList = new ArrayList<>();
    private TrackListBean trackListBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);
        rv_track = findViewById(R.id.rv_track);
        smart_refresh = findViewById(R.id.smart_refresh);
        smart_refresh.setEnableLoadMore(true);
        smart_refresh.setEnableAutoLoadMore(true);
        rv_track.setNestedScrollingEnabled(false);
        tackAdapter = new TackAdapter(TrackListActivity.this, mList);
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
        findViewById(R.id.fl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_track.setLayoutManager(layoutManager);
        smart_refresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMore();
            }
        });
        smart_refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getData();
            }
        });
        Alert.obj().loading(this);
        getData();
    }

    private void loadMore() {
        String signature = ACache.get(this).getAsString("signature");
        Caller.obj().load(MapApi.class).trackList(signature, page).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Alert.obj().loaded();
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONObject data = jsonObject.getJSONObject("data");
                    Gson gson = new Gson();
                    trackListBean = gson.fromJson(data.toString(), TrackListBean.class);
                    if (trackListBean != null && trackListBean.getTrail() != null)
                        mList.addAll(trackListBean.getTrail());
                    tackAdapter.notifyDataSetChanged();
                    setLoadMoreEnable();
                    page++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                smart_refresh.finishLoadMore();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Alert.obj().loaded();
                smart_refresh.finishRefresh();
            }
        });
    }

    private void getData() {
        page = 1;
        String signature = ACache.get(this).getAsString("signature");
        Caller.obj().load(MapApi.class).trackList(signature, page).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Alert.obj().loaded();
                smart_refresh.finishRefresh();
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONObject data = jsonObject.getJSONObject("data");
                    Gson gson = new Gson();
                    trackListBean = gson.fromJson(data.toString(), TrackListBean.class);
                    if (trackListBean.getTrail() == null) {
                        return;
                    }
                    mList.clear();
                    mList.addAll(trackListBean.getTrail());
                    tackAdapter.notifyDataSetChanged();
                    setLoadMoreEnable();
                    page++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Alert.obj().loaded();
                smart_refresh.finishRefresh();
            }
        });
    }

    private void setLoadMoreEnable() {
        if (mList.size() > page * 9) {
            smart_refresh.setEnableLoadMore(true);
        } else {
            smart_refresh.setEnableLoadMore(false);
        }
    }
}
