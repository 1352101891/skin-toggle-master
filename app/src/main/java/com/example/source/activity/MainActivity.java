package com.example.source.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.example.source.R;
import com.skinlibrary.entity.SourceBo;
import com.skinlibrary.skinManager.SkinResManager;

import org.greenrobot.eventbus.EventBus;

import base.SkinBaseActivity;
import base.fragment.SkinChooseFragment;

public class MainActivity extends SkinBaseActivity {
    SkinChooseFragment firstFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=new Intent(this,GuideActiivity.class);
        this.startActivity(intent);
        overridePendingTransition(0, 0);
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
        SourceBo bo=new SourceBo("skin_black.apk","/assets/skin_black.apk", SourceBo.TYPE.ASSETS);
        EventBus.getDefault().post(bo);
    }

    public void jumpTo(View view) {
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        firstFragment = new SkinChooseFragment();
        transaction.add(R.id.skinFragment, firstFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
