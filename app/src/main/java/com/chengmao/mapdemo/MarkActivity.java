package com.chengmao.mapdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chengmao.mapdemo.luban.Luban;
import com.chengmao.mapdemo.luban.OnCompressListener;
import com.cysion.baselib.cache.ACache;
import com.cysion.baselib.listener.PureListener;
import com.cysion.baselib.net.Caller;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xsy on 2019/2/21 0021.
 */

public class MarkActivity extends AppCompatActivity {

    private final String UPLOAD = "http://trade.5dev.cn/upload/upload/?save.start";
    private int REQUEST_CODE_DOOR = 1001;
    private int REQUEST_CODE_ROOM = 1002;
    private int REQUEST_CODE_GOOD = 1003;
    private RecyclerView mDoorImage;
    private RecyclerView mRoomImage;
    private RecyclerView mGoodImage;
    private List<String> mUrls = new ArrayList<>();
    private List<String> doorList = new ArrayList<>();
    private List<String> rommList = new ArrayList<>();
    private List<String> goodList = new ArrayList<>();
    private ImageAdapter doorAdapter;
    private ImageAdapter roomAdapter;
    private ImageAdapter goodAdapter;
    private EditText et_store;
    private EditText et_phone;
    private String coords;
    private View loading;
    private TextView tv_type;
    private TextView tv_size;
    private int type_position;
    private int size_position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark);
        double longitude = getIntent().getDoubleExtra("longitude", 116.4417);
        double latitude = getIntent().getDoubleExtra("latitude", 39.92318);
        coords = longitude + "," + latitude;
        mDoorImage = findViewById(R.id.grid_door);
        mRoomImage = findViewById(R.id.grid_room);
        mGoodImage = findViewById(R.id.grid_good);
        et_store = findViewById(R.id.et_store);
        et_phone = findViewById(R.id.et_phone);
        loading = findViewById(R.id.ll_content);
        mDoorImage.setNestedScrollingEnabled(false);
        mRoomImage.setNestedScrollingEnabled(false);
        mGoodImage.setNestedScrollingEnabled(false);
        mDoorImage.setLayoutManager(new GridLayoutManager(this, 3));
        mRoomImage.setLayoutManager(new GridLayoutManager(this, 3));
        mGoodImage.setLayoutManager(new GridLayoutManager(this, 3));
        doorAdapter = new ImageAdapter(this);
        roomAdapter = new ImageAdapter(this);
        goodAdapter = new ImageAdapter(this);
        mDoorImage.setAdapter(doorAdapter);
        mRoomImage.setAdapter(roomAdapter);
        mGoodImage.setAdapter(goodAdapter);
        findViewById(R.id.fl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAfterTransition();
            }
        });
        et_store.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.fl_reset).setVisibility(View.VISIBLE);
            }
        }, 1000);
        findViewById(R.id.fl_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_phone.setText("");
                et_store.setText("");
                mUrls.clear();
                doorList.clear();
                rommList.clear();
                goodList.clear();
                doorAdapter.setData(new ArrayList<String>());
                roomAdapter.setData(new ArrayList<String>());
                goodAdapter.setData(new ArrayList<String>());
            }
        });
        tv_type = findViewById(R.id.tv_type);
        tv_size = findViewById(R.id.tv_size);
        findViewById(R.id.ll_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MarkActivity.this, StoreTypeActivity.class);
                intent.putExtra("page", 1);
                intent.putExtra("item",tv_type.getText().toString().trim());
                startActivityForResult(intent, 101);
            }
        });
        findViewById(R.id.ll_size).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MarkActivity.this, StoreTypeActivity.class);
                intent.putExtra("page", 2);
                intent.putExtra("item",tv_size.getText().toString().trim());
                startActivityForResult(intent, 102);
            }
        });
        doorAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick() {
                MultiImageSelector.create()
                        .showCamera(true) // show camera or not. true by default
                        .count(1 - doorList.size()) // max select image size, 9 by default. used width #.multi()
                        .single() // single mode
                        .multi() // multi mode, default mode;
                        .start(MarkActivity.this, REQUEST_CODE_DOOR);
            }
        });
        roomAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick() {
                MultiImageSelector.create()
                        .showCamera(true) // show camera or not. true by default
                        .count(1 - rommList.size()) // max select image size, 9 by default. used width #.multi()
                        .single() // single mode
                        .multi() // multi mode, default mode;
                        .start(MarkActivity.this, REQUEST_CODE_ROOM);
            }
        });
        goodAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick() {
                MultiImageSelector.create()
                        .showCamera(true) // show camera or not. true by default
                        .count(1 - goodList.size()) // max select image size, 9 by default. used width #.multi()
                        .single() // single mode
                        .multi() // multi mode, default mode;
                        .start(MarkActivity.this, REQUEST_CODE_GOOD);
            }
        });
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                StringBuffer sb = new StringBuffer(s);
                char[] chars = s.toString().toCharArray();
                if (s.length() > 3) {
                    if (chars[3] != ' ') {
                        sb.insert(3, "  ");
                        setContent(sb);
                    }
                }
                if (s.length() > 9) {
                    if (chars[9] != ' ') {
                        sb.insert(9, "  ");
                        setContent(sb);
                    }
                }
            }
        });
    }

    /**
     * 添加或删除空格EditText的设置
     */
    private void setContent(StringBuffer sb) {
        et_phone.setText(sb.toString());
        //移动光标到最后面
        et_phone.setSelection(sb.length());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 101) {
                tv_type.setText(data.getStringExtra("name"));
                type_position = data.getIntExtra("position", 1);
            }else if (requestCode == 102){
                tv_size.setText(data.getStringExtra("name"));
                size_position = data.getIntExtra("position", 1);
            }else {
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                compressWithLs(path, requestCode);
            }
        }
    }

    private void compressWithLs(final List<String> paths, final int code) {
        loading.setVisibility(View.VISIBLE);
        final List<String> temps = new ArrayList<>();
        Luban.with(this).load(paths).ignoreBy(100).setTargetDir(getPath())
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        temps.add(file.getPath());
                        if (code == REQUEST_CODE_GOOD) goodAdapter.setData(temps);
                        else if (code == REQUEST_CODE_ROOM) roomAdapter.setData(temps);
                        else doorAdapter.setData(temps);
                        upload(file, code);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }).launch();
    }

    private void upload(File file, final int code) {
        final Map<String, String> map = new HashMap<>();
        map.put("key", "6");
        FileUpUtil.obj().postFile(UPLOAD, map, file, new PureListener<String>() {
            @Override
            public void done(String result) {
                Log.e("flag--", "done(MarkActivity.java:90)-->>" + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String url = jsonObject.getString("url");
                    if (code == REQUEST_CODE_DOOR) {
                        doorList.clear();
                        doorList.add(url);
                        doorAdapter.setData(doorList);
                    } else if (code == REQUEST_CODE_ROOM) {
                        rommList.clear();
                        rommList.add(url);
                        roomAdapter.setData(rommList);
                    } else {
                        goodList.clear();
                        goodList.add(url);
                        goodAdapter.setData(goodList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loading.setVisibility(View.GONE);
                Toast.makeText(MarkActivity.this, "图片上传成功！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void dont(int flag, String msg) {
                mUrls.clear();
                loading.setVisibility(View.GONE);
                Toast.makeText(MarkActivity.this, "图片上传失败，请检查网络后重新选择图片！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getPath() {
        String path = Environment.getExternalStorageDirectory() + "/Luban/image/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }

    public void submit(View view) {
        String signature = ACache.get(this).getAsString("signature");
        if (TextUtils.isEmpty(signature)) {
            wxLogin();
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("signature", signature);
        map.put("coords", coords);
        if (TextUtils.isEmpty(et_store.getText().toString().trim())) {
            Toast.makeText(MarkActivity.this, "请填写店铺名称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(et_phone.getText().toString().trim())) {
            Toast.makeText(MarkActivity.this, "请填写联系电话", Toast.LENGTH_SHORT).show();
            return;
        }
        mUrls.clear();
        mUrls.addAll(doorList);
        mUrls.addAll(rommList);
        mUrls.addAll(goodList);
        if (doorList.size() < 1) {
            Toast.makeText(MarkActivity.this, "门牌照为必传，请选择图片", Toast.LENGTH_SHORT).show();
            return;
        }
        map.put("mobile", et_phone.getText().toString().trim());
        map.put("name", et_store.getText().toString().trim());
        map.put("pic", mUrls.toString().substring(1, mUrls.toString().length() - 1));
        map.put("type", (type_position + 1) + "");
        map.put("size", (size_position + 1) + "");
        map.put("source", "3");
        map.put("json", "1");
        loading.setVisibility(View.VISIBLE);
        Log.e("flag--", "submit(MarkActivity.java:224)-->>" + new JSONObject(map).toString());
        Caller.obj().load(MapApi.class).setMark(map).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("flag--", "onResponse(MarkActivity.java:244)-->>" + response.body());
                loading.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        Toast.makeText(MarkActivity.this, "信息提交成功", Toast.LENGTH_SHORT).show();
                        finishAfterTransition();
                    } else {
                        Toast.makeText(MarkActivity.this, response.body(), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(MarkActivity.this, "数据解析错误", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(MarkActivity.this, "提交失败，请检查网络后重新提交", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void wxLogin() {
        if (!MainActivity.mWxApi.isWXAppInstalled()) {
            Toast.makeText(this, "你还未安装微信客户端", Toast.LENGTH_SHORT).show();
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        MainActivity.mWxApi.sendReq(req);
    }
}
