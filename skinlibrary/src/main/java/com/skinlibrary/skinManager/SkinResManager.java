package com.skinlibrary.skinManager;

import android.app.Application;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import android.util.Log;

import com.skinlibrary.callback.AsyTaskCallback;
import com.skinlibrary.callback.SkinLoadCallback;
import com.skinlibrary.entity.ActionBo;
import com.skinlibrary.util.PreferencesUtils;
import com.skinlibrary.util.constants;
import com.skinlibrary.entity.ResourceBo;
import com.skinlibrary.entity.SourceBo;
import com.skinlibrary.util.AccessUtil;
import com.skinlibrary.util.AsyTaskUtil;
import com.skinlibrary.util.ConfigAccess;
import com.skinlibrary.util.L;
import com.skinlibrary.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SkinResManager {
    private String TAG="SkinResManager";
    private String CacheDir="";
    private final static String AssetsConfigPath="/assets/skin.properties";
    private static String ScardConfigPath="";
    private WeakReference<Application> mApplication;
    private Map<String,ResourceBo> mapSources;
    private static SkinResManager Instance;
    private static final String DEFAULTKEY= constants.DEFAULTSKIN;
    private String currentKey="";
    private String configName="skin.properties";
    public static String getAssetsConfigPath() {
        return AssetsConfigPath;
    }

    private ResourceBo currentResource=null;
    private static ArrayList<SourceBo> sourceBos=null;

    public static SkinResManager Init(Application application){
        if (Instance==null) {
            synchronized (SkinResManager.class) {
                Instance=new SkinResManager(application);
                return Instance;
            }
        }else {
            return Instance;
        }
    }


    private SkinResManager(Application application){
        create(application);
    }

    private synchronized void create(Application application){
        mapSources=new HashMap<>();
        mApplication=new WeakReference<>(application);
        Resources resources=mApplication.get().getResources();
        mapSources.put(DEFAULTKEY,new ResourceBo(resources,null,null));
        currentResource=new ResourceBo(resources,application.getApplicationInfo().packageName,null);
        currentKey=DEFAULTKEY;
        CacheDir= Environment.getExternalStorageDirectory()+ File.separator+application.getApplicationInfo().packageName;
        ScardConfigPath=CacheDir+ File.separator+configName;
        createFolder();
        SkinResLoader.setCallback(new SkinLoadCallback() {
            @Override
            public void preDo() {
                L.e(TAG,"加载皮肤之前");
            }

            @Override
            public void progressDo(int percent) {
                L.e(TAG,"加载皮肤进度："+percent);
            }

            @Override
            public void endDo(SourceBo bo) {
                L.e(TAG,"皮肤加载完毕！："+bo.getName());
                apply(bo.getName());
            }
        });


        AsyTaskUtil<SourceBo> taskUtil = new AsyTaskUtil<>(new AsyTaskCallback<SourceBo>() {
            @Override
            public void pre() {
                L.e(TAG, "开始复制皮肤配置到sdcard");
            }

            @Override
            public void doInBackground(SourceBo bo) {
                if (PreferencesUtils.getInt(getApplication(),constants.STATUS,constants.FAILE)!=constants.OK) {
                    AccessUtil.CopyAssetToPreference();
                }
                sourceBos = ConfigAccess.getAssetsSkinSourceBo();
            }

            @Override
            public void end(SourceBo bo) {
                PreferencesUtils.putInt(getApplication(), constants.STATUS, constants.OK);
                L.e(TAG, "复制皮肤配置完成！,路径是：" + bo.getPath());
            }
        });
        taskUtil.load(new SourceBo("skin.properties", AssetsConfigPath, null));

    }


    public static ArrayList<SourceBo> getSourceBos() {
        return sourceBos;
    }

    public static SourceBo getSource(String name){
        if (sourceBos!=null){
            for (SourceBo bo:sourceBos) {
                if (bo.getName().equals(name)){
                    return bo;
                }
            }
        }
        return null;
    }


    private void createFolder() {
        String strCpSdPath = this.CacheDir + File.separator;
        File file = new File(strCpSdPath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("SkinResManager", "cannot create directory.");
            }
        }
    }


    public Application getApplication() {
        return mApplication.get();
    }

    public String getCacheDir() {
        return CacheDir;
    }

    public synchronized void addResource(String key,ResourceBo resources){
        if (mapSources==null){
            mapSources=new HashMap<>();
        }
        mapSources.put(key,resources);
        currentKey=key;
        currentResource=resources;
    }

    public boolean IsExistRes(String key){
        if (mapSources!=null){
            return mapSources.containsKey(key);
        }
        return false;
    }

    public ResourceBo getResource(){
        if (currentResource!=null){
            return currentResource;
        }
        return null;
    }

    public ResourceBo getResource(String key){
        if (mapSources==null){
            return null;
        }
        if (key==null){
            return mapSources.get(DEFAULTKEY);
        }else {
            return mapSources.get(key);
        }
    }

    public void Clear(){
        mApplication.clear();
        mapSources.clear();
        CacheDir=null;
    }


    /**
     * 加载皮肤的入口
     * @param bo
     */
    public void applySKinPlugin(SourceBo bo){
        if (bo!=null && SourceBo.TYPE.contains(bo.getType())){
            if (IsExistRes(bo.getName())){
                apply(bo.getName());
            }else {
                if(SkinResLoader.load(bo)){
                    ToastUtil.show("正在加载皮肤，请稍后！");
                }
            }
            ConfigAccess.setDefaultSKinConfig(bo);
        }else {
            ToastUtil.show("不存在该类型皮肤！");
        }
    }




    private synchronized void apply(String key){
        if (key!=null) {
            if (mapSources.get(currentKey) != null) {
                currentKey = key;
                currentResource = mapSources.get(currentKey);
            }else {
                mapSources.remove(key);
                return;
            }
        }
        EventBus.getDefault().post(new ActionBo());
        Log.e(TAG,"应用皮肤资源的广播:"+key);
    }

    public int getColor(int resId) {
        int originColor = getApplication().getResources().getColor(resId);
        if (currentResource == null ) {
            return originColor;
        }

        String resName = getApplication().getResources().getResourceEntryName(resId);

        int trueResId = currentResource.getResources().getIdentifier(resName, "color", currentResource.getPackName());
        int trueColor = 0;

        try {
            trueColor = currentResource.getResources().getColor(trueResId);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            trueColor = originColor;
        }

        return trueColor;
    }



    public int getResourceID(String name) {
        String[] resName =name.split("\\.");
        int ID = getApplication().getResources().getIdentifier(resName[0], "drawable",  getApplication().getPackageName());
        return ID;
    }

    public Drawable getMipmap(int resId) {

        Drawable originDrawable = getApplication().getResources().getDrawable(resId);
        if (currentResource == null) {
            return originDrawable;
        }
        String resName = getApplication().getResources().getResourceEntryName(resId);

        int trueResId = currentResource.getResources().getIdentifier(resName, "mipmap", currentResource.getPackName());

        Drawable trueDrawable = null;
        try {
            Log.i("SkinManager getDrawable", "SDK_INT = " + android.os.Build.VERSION.SDK_INT);
            if (android.os.Build.VERSION.SDK_INT < 22) {
                trueDrawable = currentResource.getResources().getDrawable(trueResId);
            } else {
                trueDrawable = currentResource.getResources().getDrawable(trueResId, null);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            trueDrawable = originDrawable;
        }

        return trueDrawable;
    }

    public Drawable getDrawable(int resId) {

        Drawable originDrawable = getApplication().getResources().getDrawable(resId);
        if (currentResource == null) {
            return originDrawable;
        }
        String resName = getApplication().getResources().getResourceEntryName(resId);

        int trueResId = currentResource.getResources().getIdentifier(resName, "drawable", currentResource.getPackName());

        Drawable trueDrawable = null;
        try {
            Log.i("SkinManager getDrawable", "SDK_INT = " + android.os.Build.VERSION.SDK_INT);
            if (android.os.Build.VERSION.SDK_INT < 22) {
                trueDrawable = currentResource.getResources().getDrawable(trueResId);
            } else {
                trueDrawable = currentResource.getResources().getDrawable(trueResId, null);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            trueDrawable = originDrawable;
        }

        return trueDrawable;
    }

    public ColorStateList convertToColorStateList(int resId) {
        boolean isExtendSkin = true;
        if (currentResource.getResources() == null  ) {
            isExtendSkin = false;
        }

        String resName = getApplication().getResources().getResourceEntryName(resId);
        if (isExtendSkin) {
            int trueResId = currentResource.getResources().getIdentifier(resName, "color", currentResource.getPackName());
            ColorStateList trueColorList = null;
            if (trueResId == 0) { // 如果皮肤包没有复写该资源，但是需要判断是否是ColorStateList
                try {
                    ColorStateList originColorList = getApplication().getResources().getColorStateList(resId);
                    return originColorList;
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                    Log.e(TAG,"resName = " + resName + " NotFoundException : " + e.getMessage());
                }
            } else {
                try {
                    trueColorList = currentResource.getResources().getColorStateList(trueResId);
                    return trueColorList;
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                    Log.e(TAG,"resName = " + resName + " NotFoundException :" + e.getMessage());
                }
            }
        } else {
            try {
                ColorStateList originColorList = getApplication().getResources().getColorStateList(resId);
                return originColorList;
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
                Log.e(TAG,"resName = " + resName + " NotFoundException :" + e.getMessage());
            }

        }

        int[][] states = new int[1][1];
        return new ColorStateList(states, new int[]{getApplication().getResources().getColor(resId)});
    }

    public static SkinResManager getInstance() {
        return Instance;
    }

    public static String getScardConfigPath() {
        return ScardConfigPath;
    }
}
