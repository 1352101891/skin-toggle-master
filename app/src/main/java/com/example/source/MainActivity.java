package com.example.source;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.skinlibrary.entity.SourceBo;
import com.skinlibrary.skinManager.SkinResManager;

import base.SkinBaseActivity;

public class MainActivity extends SkinBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected void InitViews() {

    }

    @Override
    protected void InitMenus() {

    }

    public void setSkin(View view){
        Log.e(TAG(),"正在设置皮肤！");
        SkinResManager.getInstance().applySKinPlugin(new SourceBo("黑色主题.apk","/assets/skin_black.apk", SourceBo.TYPE.ASSETS));
    }

}
