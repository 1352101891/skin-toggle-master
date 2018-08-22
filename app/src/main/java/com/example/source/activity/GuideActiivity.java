package com.example.source.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.example.source.R;
import com.skinlibrary.skinManager.SkinResManager;
import com.skinlibrary.util.PreferencesUtils;
import com.skinlibrary.util.constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import static com.skinlibrary.util.constants.SKINLOADCONFIGFINISH;


/**
 * Created by lvqiu on 2018/8/12.
 */

public class GuideActiivity extends AppCompatActivity{
    private int INITCODE=0;
    private int FINISHCODE=16;
    private AlertDialog alertDialog;
    private int SKINFINISH=16;
    private int DBFINISH=1;
    private Handler mHandler=new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        showLoadingDialog();
        checkBlePermission();
    }


    public void Init(){
        SkinResManager.Init(getApplication());
        cheackStatus();
    }

    public void cheackStatus(){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int code= PreferencesUtils.getInt(getApplication(), constants.STATUS);
                    INITCODE=INITCODE|code;
                    if (INITCODE==FINISHCODE){
                        dismissLoadingDialog();
                        GuideActiivity.this.finish();
                        overridePendingTransition(0,0);
                    }
                }
            },1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void showLoadingDialog() {
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bg)));
        alertDialog.setCancelable(false);
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return false;
            }
        });
        alertDialog.show();
        alertDialog.setContentView(R.layout.alertdialog_loading);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    public void dismissLoadingDialog() {
        if (null != alertDialog && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // 如果请求被取消，则结果数组为空。
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("tag","同意申请");
                    Init();
                } else {
                    Log.i("tag","拒绝申请");
                    checkBlePermission();
                }
                return;
            }
        }
    }


    /**
     * 检查蓝牙权限
     */
    public void checkBlePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions((Activity) this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        } else {
            Log.i("tag","已申请权限");
            Init();
        }
    }

}
