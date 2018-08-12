package com.skinlibrary.util;

import android.os.AsyncTask;
import android.util.Log;

import com.skinlibrary.callback.AsyTaskCallback;
import com.skinlibrary.entity.SourceBo;

/**
 * Created by lvqiu on 2018/8/12.
 */

public class AsyTaskUtil<T> {
    private static final String TAG="AsyTaskUtil";
    private AsyTaskCallback<T> callback;

    public AsyTaskUtil(AsyTaskCallback<T> callback) {
        this.callback = callback;
    }

    public void load(T t){
        new ResAsyncTask(callback).execute(t);
    }

    class ResAsyncTask extends AsyncTask<T,Integer,T> {
        AsyTaskCallback callback;

        public ResAsyncTask(AsyTaskCallback callback) {
            this.callback = callback;
        }

        //onPreExecute用于异步处理前的操作
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (callback!=null){
                callback.pre();
            }
            Log.e(TAG,"onPreExecute");
        }

        //在doInBackground方法中进行异步任务的处理.
        @Override
        protected T doInBackground(T... params) {
            if (callback!=null){
                callback.doInBackground(params[0]);
            }
            return params[0];
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        //onPostExecute用于UI的更新.此方法的参数为doInBackground方法返回的值.
        @Override
        protected void onPostExecute(T t) {
            if (callback!=null){
                callback.end(t);
            }
            super.onPostExecute(t);

        }
    }
}

