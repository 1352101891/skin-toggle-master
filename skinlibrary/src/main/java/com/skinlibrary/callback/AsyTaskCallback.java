package com.skinlibrary.callback;

/**
 * Created by lvqiu on 2018/8/12.
 */

public interface AsyTaskCallback<T> {
    void pre();
    void doInBackground(T t);
    void end(T t);
}
