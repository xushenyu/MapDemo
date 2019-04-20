package com.chengmao.mapdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cysion.baselib.cache.ACache;
import com.cysion.baselib.net.Caller;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xsy on 2019/2/22 0022.
 */

public class StoreTypeActivity extends AppCompatActivity {

    private RecyclerView rv_type;
    private List<String> mList;
    private TypeAdapter typeAdapter;
    private String item;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);
        final int page = getIntent().getIntExtra("page", 1);
        item = getIntent().getStringExtra("item");
        findViewById(R.id.fl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rv_type = findViewById(R.id.rv_type);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_type.setLayoutManager(layoutManager);
        mList = new ArrayList<>();
        typeAdapter = new TypeAdapter(mList, StoreTypeActivity.this,item);
        rv_type.setAdapter(typeAdapter);
        typeAdapter.setOnItemClickListener(new TypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent();
                intent.putExtra("name", mList.get(position));
                intent.putExtra("position", position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        Caller.obj().load(MapApi.class).getType(ACache.get(this).getAsString("signature")).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                List<String> list = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (page == 2){
                        JSONObject size = data.getJSONObject("size");
                        String[] split = size.toString().split("[,]");
                        for (int i = 0; i < split.length; i++) {
                            list.add(size.getString((i + 1) + ""));
                        }
                    }else{
                        JSONObject type = data.getJSONObject("type");
                        String[] split = type.toString().split("[,]");
                        for (int i = 0; i < split.length; i++) {
                            list.add(type.getString((i + 1) + ""));
                        }
                    }
                    mList.addAll(list);
                    typeAdapter.notifyDataSetChanged();
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
