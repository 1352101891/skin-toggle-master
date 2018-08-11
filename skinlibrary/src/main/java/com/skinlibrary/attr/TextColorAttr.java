package com.skinlibrary.attr;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.skinlibrary.skinManager.SkinResManager;

/**
 * Created by _SOLID
 * Date:2016/4/13
 * Time:22:53
 */
public class TextColorAttr extends SkinAttr {
    @Override
    public void apply(View view) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            if (RES_TYPE_NAME_COLOR.equals(attrValueTypeName)) {
                Log.i("applyAttr", "TextColorAttr");
                tv.setTextColor(SkinResManager.getInstance().convertToColorStateList(attrValueRefId));
            }
        }
    }
}
