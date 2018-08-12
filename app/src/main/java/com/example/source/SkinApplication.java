package com.example.source;

import android.app.Application;
import android.os.Environment;

import com.skinlibrary.entity.SourceBo;
import com.skinlibrary.skinManager.SkinResManager;

import java.io.File;
import java.util.ArrayList;

public class SkinApplication extends Application {
    private static ArrayList<SourceBo> sourceBos;
    private static final String TAG="SkinApplication";
    private static Application app;
    private static String CacheDir="";
    private static int width=0,height=0;

    @Override
    public void onCreate() {
        super.onCreate();
        SkinResManager.Init(this);
        app=this;
        CacheDir= Environment.getExternalStorageDirectory()+ File.separator+getApplicationInfo().packageName;
    }


    public static Application get() {
        return app;
    }

    public static String CacheDir() {
        return CacheDir;
    }
}
