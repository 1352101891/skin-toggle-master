package com.skinlibrary.util;

import com.skinlibrary.entity.SourceBo;
import com.skinlibrary.skinManager.SkinResManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import static com.skinlibrary.util.constants.DEFAULTKEY;
import static com.skinlibrary.util.constants.SKINCONFIG;

/**
 * Created by lvqiu on 2018/8/12.
 */

public class ConfigAccess {

    private static final String TAG="ConfigAccess";
    /**
     * 获取皮肤配置
     */
    public static ArrayList<SourceBo> getAssetsSkinSourceBo()   {

        try {
            String str =  PreferencesUtils.getString(SkinResManager.getInstance().getApplication(),SKINCONFIG);
            JSONArray array=new JSONArray(str);
            if (array==null){
                return null;
            }
            ArrayList<SourceBo> list=new ArrayList<>();
            for (int i=0;i<array.length();i++){
                SourceBo bo=SourceBo.parseJson(array.getJSONObject(i));
                list.add(bo);
            }

            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            L.e(TAG,"getPropertiese读取失败！");
        }
        return null;
    }


    /**
     * 获取皮肤配置
     */
    public static SourceBo getDefaultSkin() {

        try {

            String str = PreferencesUtils.getString(SkinResManager.getInstance().getApplication(),DEFAULTKEY);
            SourceBo bo=SourceBo.parseJson(new JSONObject(str));
            return bo;
        }  catch (JSONException e) {
            e.printStackTrace();
            L.e(TAG,"getPropertiese读取失败！");
        }
        return null;
    }



    /**
     * 设置皮肤配置
     */
    public static boolean setDefaultSKinConfig(SourceBo bo) {
         return PreferencesUtils.putString(SkinResManager.getInstance().getApplication(),DEFAULTKEY,bo.toString());
    }


}
