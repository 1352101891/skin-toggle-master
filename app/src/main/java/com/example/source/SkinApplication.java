package com.example.source;

import android.app.Application;
import com.skinlibrary.skinManager.SkinResManager;

public class SkinApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SkinResManager.Init(this);
    }
}
