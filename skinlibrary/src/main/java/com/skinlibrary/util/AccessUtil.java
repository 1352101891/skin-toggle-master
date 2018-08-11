package com.skinlibrary.util;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import com.skinlibrary.entity.ResourceBo;
import com.skinlibrary.entity.SourceBo;
import com.skinlibrary.skinManager.SkinResManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccessUtil {

    public static void CopyAssetFile(SourceBo bo ) {
        String strCpSdPath = SkinResManager.getInstance().getCacheDir()+File.separator + bo.getName();
        //安全检查文件
        safeCheck(strCpSdPath);
        try {

            File file = new File(strCpSdPath);
            InputStream myInput = SkinResManager.getInstance().getClass().getResourceAsStream(bo.getPath());
            file.createNewFile();
            OutputStream myOutput = new FileOutputStream(file, true);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        bo.setPath(strCpSdPath);
    }


    public static void download(SourceBo sourceBo ) {
        URL url = null;
        try {
            url = new URL(sourceBo.getUrl());

            HttpURLConnection conn = null;

            conn = (HttpURLConnection) url.openConnection();

            conn.connect();

            // 打印HTTP header
            Map headers = conn.getHeaderFields();
            Set<String> keys = headers.keySet();
            for(String key : keys) {
                System.out.println(key + " ----------------------------- " + conn.getHeaderField(key));
            }
            // 转换编码
            String contentDisposition = null;
            try {
                contentDisposition = URLDecoder.decode(conn.getHeaderField("content-Disposition"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            System.out.println(contentDisposition);
            // 匹配文件名
            Pattern pattern = Pattern.compile(".*fileName=(.*)");
            Matcher matcher = pattern.matcher(contentDisposition);
            System.out.println(matcher.groupCount());
            System.out.println(matcher.matches());
            System.out.println(matcher.group(1));
            String filename = matcher.group(1);

            String filepath=SkinResManager.getInstance().getCacheDir()+ File.separator+ sourceBo.getName();

            //安全检查文件
            safeCheck(filepath);

            // 写盘
            RandomAccessFile file = null;
            file = new RandomAccessFile(filepath, "rw");

            InputStream stream = conn.getInputStream();
            byte buffer[] = new byte[1024];

            while (true) {
                int len = stream.read(buffer);
                if (len == -1) {
                    break;
                }
                file.write(buffer, 0, len);
            }
            if (stream != null) {
                stream.close();
            }
            if (file != null) {
                file.close();
            }
            sourceBo.setPath(filepath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static ResourceBo getResByAssertManager(SourceBo bo){
        try {

                String skinPkgPath = bo.getPath();
                Log.i("loadSkin", skinPkgPath);
                File file = new File(skinPkgPath);
                if (file == null || !file.exists()) {
                    return null;
                }

                PackageManager mPm = SkinResManager.getInstance().getApplication().getPackageManager();
                PackageInfo mInfo = mPm.getPackageArchiveInfo(skinPkgPath, PackageManager.GET_ACTIVITIES);
                String skinPackageName = mInfo.packageName;

                AssetManager assetManager = AssetManager.class.newInstance();
                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                addAssetPath.invoke(assetManager, skinPkgPath);


                Resources superRes = SkinResManager.getInstance().getApplication().getResources();
                Resources skinResource = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());

                ResourceBo bo1=new ResourceBo(skinResource,skinPackageName,skinPkgPath);

                return bo1;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static boolean safeCheck(String filepath){
        File tempfile=new File(filepath);
        if (!tempfile.getParentFile().exists()) {
            if (!tempfile.getParentFile().mkdirs()) {
                Log.e("CopyAssetFile", "cannot create directory.");
                return false;
            }
        }
        if (tempfile.exists()){
            if (!tempfile.delete()){
                Log.e("CopyAssetFile", "cannot delete file.");
                return false;
            }
        }
        return true;
    }

}
