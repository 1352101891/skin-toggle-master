package com.skinlibrary.skinManager;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.VectorEnabledTintResources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import com.skinlibrary.attr.AttrFactory;
import com.skinlibrary.attr.DynamicAttr;
import com.skinlibrary.attr.SkinAttr;
import com.skinlibrary.entity.SkinItem;
import com.skinlibrary.util.L;
import com.skinlibrary.util.ListUtils;
import java.util.ArrayList;
import java.util.List;


public class SkinViewInflaterFactory implements LayoutInflaterFactory {
    private final static String TAG="SkinViewInflaterFactory";
    private ArrayList<SkinItem> mSkinItems=new ArrayList<>();
    private AppCompatViewInflater mAppCompatViewInflater;


    /**
     * From {@link android.support.v4.view.LayoutInflaterFactory}
     */
    public  View onCreateView(View parent, String name,
                                   Context context, AttributeSet attrs) {
        if (name.contains("Relative")||name.contains("text")){
            Log.e(TAG,""+name);
        }
        View view=createViewByInflater(parent, name, context, attrs);
        if (view==null){
            view=createViewByName(context,name,attrs);
        }
        parseSkinAttr(context, attrs,view);
        return view;
    }

    /**
     * 通过name去实例化一个View
     *
     * @param context
     * @param name    要被实例化View的全名.
     * @param attrs   View在布局文件中的XML的属性
     * @return View
     */
    private View createViewByName(Context context, String name, AttributeSet attrs) {
        Log.i(TAG, "createView:" + name);
        View view = null;
        try {
            if (-1 == name.indexOf('.')) {
                if ("View".equals(name)) {
                    view = LayoutInflater.from(context).createView(name, "android.view.", attrs);
                }
                if (view == null) {
                    view = LayoutInflater.from(context).createView(name, "android.widget.", attrs);
                }
                if (view == null) {
                    view = LayoutInflater.from(context).createView(name, "android.webkit.", attrs);
                }
            } else {
                view = LayoutInflater.from(context).createView(name, null, attrs);
            }

            L.i(TAG, "about to create " + name);

        } catch (Exception e) {
            L.e(TAG, "error while create 【" + name + "】 : " + e.getMessage());
            view = null;
        }
        return view;
    }

    public View createViewByInflater(View parent, final String name, @NonNull Context context,
                           @NonNull AttributeSet attrs) {
        final boolean isPre21 = Build.VERSION.SDK_INT < 21;

        if (mAppCompatViewInflater == null) {
            mAppCompatViewInflater = new AppCompatViewInflater();
        }

        // We only want the View to inherit its context if we're running pre-v21
        final boolean inheritContext = isPre21 && shouldInheritContext((ViewParent) parent,context);

        return mAppCompatViewInflater.createView(parent, name, context, attrs, inheritContext,
                isPre21, /* Only read android:theme pre-L (L+ handles this anyway) */
                true, /* Read read app:theme as a fallback at all times for legacy reasons */
                VectorEnabledTintResources.shouldBeUsed() /* Only tint wrap the context if enabled */
        );
    }


    private boolean shouldInheritContext(ViewParent parent,Context context) {
        if (parent == null) {
            // The initial parent is null so just return false
            return false;
        }
        final View windowDecor = ((Activity)context).getWindow().getDecorView();
        while (true) {
            if (parent == null) {
                return true;
            } else if (parent == windowDecor || !(parent instanceof View)
                    || ViewCompat.isAttachedToWindow((View) parent)) {
                return false;
            }
            parent = parent.getParent();
        }
    }


    /**
     * 搜集可更换皮肤的属性
     *
     * @param context
     * @param attrs
     * @param view
     */
    private void parseSkinAttr(Context context, AttributeSet attrs, View view) {
        if (view==null){
            return;
        }
        List<SkinAttr> viewAttrs = new ArrayList<SkinAttr>();//存储View可更换皮肤属性的集合
        for (int i = 0; i < attrs.getAttributeCount(); i++) {//遍历当前View的属性
            String attrName = attrs.getAttributeName(i);//属性名
            String attrValue = attrs.getAttributeValue(i);//属性值
            if (!AttrFactory.isSupportedAttr(attrName)) {
                continue;
            }
            if (attrValue.startsWith("@")) {//也就是引用类型，形如@color/red
                try {
                    int id = Integer.parseInt(attrValue.substring(1));//资源的id
                    String entryName = context.getResources().getResourceEntryName(id);//入口名，例如text_color_selector
                    String typeName = context.getResources().getResourceTypeName(id);//类型名，例如color、background
                    SkinAttr mSkinAttr = AttrFactory.get(attrName, id, entryName, typeName);
                    L.i("parseSkinAttr", "view:" + view.getClass().getSimpleName());
                    L.i("parseSkinAttr", "attrName:" + attrName + " | attrValue:" + attrValue);
                    L.i("parseSkinAttr", "id:" + id);
                    L.i("parseSkinAttr", "entryName:" + entryName);
                    L.i("parseSkinAttr", "typeName:" + typeName);
                    if (mSkinAttr != null) {
                        viewAttrs.add(mSkinAttr);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!ListUtils.isEmpty(viewAttrs)) {
            SkinItem skinItem = new SkinItem();
            skinItem.view = view;
            skinItem.attrs = viewAttrs;
            mSkinItems.add(skinItem);
            if (SkinResManager.getInstance()!=null) {
                skinItem.apply();
            }
        }
    }

    /**
     * 应用皮肤
     */
    public void applySkin() {
        if (ListUtils.isEmpty(mSkinItems)) {
            return;
        }

        for (SkinItem si : mSkinItems) {
            if (si.view == null) {
                continue;
            }
            si.apply();
        }
    }

    /**
     * 清除有皮肤更改需求的View及其对应的属性的集合
     */
    public void clean() {
        if (ListUtils.isEmpty(mSkinItems)) {
            return;
        }
        for (SkinItem si : mSkinItems) {
            if (si.view == null) {
                continue;
            }
            si.clean();
        }
    }

    public void addSkinView(SkinItem item) {
        mSkinItems.add(item);
    }

    /**
     * 动态添加那些有皮肤更改需求的View，及其对应的属性
     *
     * @param context
     * @param view
     * @param attrName       属性名
     * @param attrValueResId 属性资源id
     */
    public void dynamicAddSkinEnableView(Context context, View view, String attrName, int attrValueResId) {
        int id = attrValueResId;
        String entryName = context.getResources().getResourceEntryName(id);
        String typeName = context.getResources().getResourceTypeName(id);
        SkinAttr mSkinAttr = AttrFactory.get(attrName, id, entryName, typeName);
        SkinItem skinItem = new SkinItem();
        skinItem.view = view;
        List<SkinAttr> viewAttrs = new ArrayList<SkinAttr>();
        viewAttrs.add(mSkinAttr);
        skinItem.attrs = viewAttrs;
        skinItem.apply();
        addSkinView(skinItem);
    }

    /**
     * 动态添加那些有皮肤更改需求的View，及其对应的属性集合
     *
     * @param context
     * @param view
     * @param pDAttrs
     */
    public void dynamicAddSkinEnableView(Context context, View view, List<DynamicAttr> pDAttrs) {
        List<SkinAttr> viewAttrs = new ArrayList<SkinAttr>();
        SkinItem skinItem = new SkinItem();
        skinItem.view = view;

        for (DynamicAttr dAttr : pDAttrs) {
            int id = dAttr.refResId;
            String entryName = context.getResources().getResourceEntryName(id);
            String typeName = context.getResources().getResourceTypeName(id);
            SkinAttr mSkinAttr = AttrFactory.get(dAttr.attrName, id, entryName, typeName);
            viewAttrs.add(mSkinAttr);
        }

        skinItem.attrs = viewAttrs;
        skinItem.apply();
        addSkinView(skinItem);
    }
}
