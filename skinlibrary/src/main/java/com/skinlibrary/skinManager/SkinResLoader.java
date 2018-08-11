package com.skinlibrary.skinManager;

import android.os.AsyncTask;
import android.util.Log;
import com.skinlibrary.callback.SkinLoadCallback;
import com.skinlibrary.entity.ResourceBo;
import com.skinlibrary.entity.SourceBo;
import com.skinlibrary.util.AccessUtil;
import com.skinlibrary.util.L;


public class SkinResLoader  {
    private static String TAG="SkinResLoader";
    private static SkinLoadCallback callback;
    private static boolean isLoading=false;

    public static boolean load(SourceBo bo){
        if (isLoading){
            return false;
        }else {
            new ResAsyncTask().execute(bo);
            return true;
        }
    }

    static class ResAsyncTask extends AsyncTask<SourceBo,Integer,SourceBo> {

        //onPreExecute用于异步处理前的操作
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isLoading=true;
            if (callback!=null)
                callback.preDo();
            Log.e(TAG,"onPreExecute");
        }

        //在doInBackground方法中进行异步任务的处理.
        @Override
        protected SourceBo doInBackground(SourceBo... params) {
            isLoading=true;
            categChoose(params[0]);
            return params[0];
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (callback!=null)
                callback.progressDo(values[0]);
        }

        //onPostExecute用于UI的更新.此方法的参数为doInBackground方法返回的值.
        @Override
        protected void onPostExecute(SourceBo sourceBo) {
            super.onPostExecute(sourceBo);
            if (sourceBo.getType()== SourceBo.TYPE.NET || sourceBo.getType()== SourceBo.TYPE.ASSETS){
                sourceBo.setType(SourceBo.TYPE.LOCAL);
                ResAsyncTask asyncTask=new ResAsyncTask();
                asyncTask.execute(sourceBo);
            }else {
                isLoading=false;
                sourceBo.setType(SourceBo.TYPE.LOCAL);
                if (callback!=null)
                    callback.endDo(sourceBo);
            }
        }
    }

    public static void setCallback(SkinLoadCallback cb) {
         callback = cb;
    }


    public static void categChoose(SourceBo bo){
        if (bo.getType()== SourceBo.TYPE.LOCAL){
            if (!SkinResManager.getInstance().IsExistRes(bo.getName())) {
                ResourceBo res = AccessUtil.getResByAssertManager(bo);
                SkinResManager.getInstance().addResource(bo.getName(), res);
                L.e(TAG,"从本地加载皮肤！"+bo.getPath());
            }
            L.e(TAG,"皮肤已经被加载过了！");
        }else if (bo.getType()== SourceBo.TYPE.NET){
            AccessUtil.download(bo);
            L.e(TAG,"网络下载皮肤！"+bo.getUrl());
        }else if (bo.getType()==SourceBo.TYPE.ASSETS){
            AccessUtil.CopyAssetFile(bo);
            L.e(TAG,"Assert皮肤！"+bo.getPath());
        }
    }
}
