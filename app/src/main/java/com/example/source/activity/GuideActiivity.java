package com.example.source.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.example.source.R;
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
        cheackStatus();
    }

    public void cheackStatus(){
        int code= PreferencesUtils.getInt(getApplication(), constants.STATUS);
        INITCODE=INITCODE|code;
        if (INITCODE==FINISHCODE){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissLoadingDialog();
                    GuideActiivity.this.finish();
                    overridePendingTransition(0,0);
                }
            },2000);
        }
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


}
