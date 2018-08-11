package com.skinlibrary.skinManager;

import android.app.Application;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import android.util.Log;

import com.skinlibrary.callback.SkinLoadCallback;
import com.skinlibrary.entity.ResourceBo;
import com.skinlibrary.entity.SourceBo;
import com.skinlibrary.util.L;
import com.skinlibrary.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SkinResManager {
    private String TAG="SkinResManager";
    private String CacheDir="";
    private WeakReference<Application> mApplication;
    private Map<String,ResourceBo> mapSources;
    private static SkinResManager Instance;
    private static final String DEFAULTKEY="DEFAULT_SKIN";
    private String currentKey="";
    private ResourceBo currentResource=null;

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
                return;
            }
        }
        SourceBo bo=new SourceBo(currentKey,currentResource.getSourcePath(), SourceBo.TYPE.LOCAL);
        EventBus.getDefault().post(bo);
        Log.e(TAG,"发送皮肤广播:"+key);
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

}
