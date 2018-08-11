package com.skinlibrary.util;

import android.widget.Toast;

import com.skinlibrary.skinManager.SkinResManager;

public class ToastUtil {

    public static void show(String msg){
        Toast.makeText(SkinResManager.getInstance().getApplication(),""+msg,Toast.LENGTH_SHORT).show();
    }

    public  static void showL(String msg){
        Toast.makeText(SkinResManager.getInstance().getApplication(),""+msg,Toast.LENGTH_LONG).show();
    }
}
