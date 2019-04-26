package com.chengmao.mapdemo.track;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.chengmao.mapdemo.R;
import com.chengmao.mapdemo.bean.TrackListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xsy on 2019/4/20 0020.
 */

public class TrackDetailActivity extends AppCompatActivity {

    private TabLayout mTab;
    private ViewPager mVP;
    private List<String> titles;
    private TrackListBean.TrailBean track;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail);
        track = (TrackListBean.TrailBean) getIntent().getSerializableExtra("track");
        findViewById(R.id.fl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTab = findViewById(R.id.tab);
        mVP = findViewById(R.id.vp);
        mVP.setOffscreenPageLimit(3);
        titles = new ArrayList<>();
        titles.add("地图");
        titles.add("打点");
        List<Fragment> fragments = new ArrayList<>();
        TrackWebFragment trailFragment = new TrackWebFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("track", track);
        trailFragment.setArguments(bundle);
        PointFragment pointFragment = new PointFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("track", track);
        pointFragment.setArguments(bundle1);
        fragments.add(trailFragment);
        fragments.add(pointFragment);
        HomeVPAdapter homeVPAdapter = new HomeVPAdapter(getSupportFragmentManager(), fragments, titles);
        mVP.setAdapter(homeVPAdapter);
        mTab.setupWithViewPager(mVP);
    }
}
