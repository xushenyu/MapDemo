package com.chengmao.mapdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cysion.baselib.Box;

public class Alert {

    private static volatile Alert instance;
    private AlertDialog mLoadDialog;

    private Alert() {

    }

    public static synchronized Alert obj() {
        if (instance == null) {
            instance = new Alert();
        }
        return instance;
    }


    public void loading(final Activity src) {
        if (mLoadDialog != null && mLoadDialog.isShowing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(src);
        LayoutInflater inflater = LayoutInflater.from(src);
        View view = inflater.inflate(R.layout.dialog_loading, null);
        mLoadDialog = builder.create();
        Window window = mLoadDialog.getWindow();
        mLoadDialog.show();
        mLoadDialog.setCanceledOnTouchOutside(true);
        //摆脱token的限制，注意清单文件alert权限
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        window.getDecorView().setBackgroundColor(0X00000000);
        p.width = (int) (Box.w() * 1f);
        window.setAttributes(p);
        window.setDimAmount(0);
        window.setBackgroundDrawable(null);
        mLoadDialog.getWindow().setContentView(view);//自定义布局应该在这里添加，要在dialog.show()的后面
        mLoadDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mLoadDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mLoadDialog.setCancelable(true);
    }

    public void loaded() {
        if (mLoadDialog != null) {
            mLoadDialog.dismiss();
        }
    }
}
