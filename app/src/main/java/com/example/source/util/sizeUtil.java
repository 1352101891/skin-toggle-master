package com.example.source.util;

import android.util.DisplayMetrics;
import com.example.source.SkinApplication;
import com.skinlibrary.util.L;

/**
 * Created by lvqiu on 2018/8/12.
 */

public class sizeUtil {
    private final static String TAG="sizeUtil";

    private static int convertDpToPixel(int dp) {
        DisplayMetrics displayMetrics = SkinApplication.get().getResources().getDisplayMetrics();
        return (int)(dp*displayMetrics.density);
    }

    private static int convertPixelToDp(int pixel) {
        DisplayMetrics displayMetrics = SkinApplication.get().getResources().getDisplayMetrics();
        return (int)(pixel/displayMetrics.density);
    }


    private static size getSize(){
        DisplayMetrics dm2 = SkinApplication.get().getResources().getDisplayMetrics();
        L.e(TAG,"width-display :" + dm2.widthPixels);
        L.e(TAG,"heigth-display :" + dm2.heightPixels);
        return new size(dm2.widthPixels,dm2.heightPixels);
    }

    static class size{
        private int screenWidth;
        private int screenHeight;

        public size(int screenWidth, int screenHeight) {
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
        }
    }
}
