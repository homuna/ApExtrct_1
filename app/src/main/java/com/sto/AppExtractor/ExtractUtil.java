package com.sto.AppExtractor;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class ExtractUtil {

    public static File defalutFileDir(Context context) {
        return new File(String.valueOf(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)));
    }

    public static void makeDefalutFileDir(Context context){
        File file = defalutFileDir(context);
        if(!file.exists()) {
            Log.e("file", "folder is not existed");
            if(!file.mkdirs());
            Log.e("file", "cannot make folder");
        }
        else{
            Log.e("file", "folder is already existed");
        }
    }

    private static void copyfile(File src, File cat) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(cat);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    public static String extractAPk(AppData appData) throws Exception {
        File src = new File(appData.getSrc());
        PreferenceData preferenceData = MainActivity.getPreferenceData();
        File cat = new File(preferenceData.getDirPath() + "/" + apkFilename(appData) + ".apk");
        try {
            copyfile(src, cat);
            Log.e("extrack", "source file : " + src.toString());
            Log.e("extrack", "created file : " + cat.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!cat.exists()){
            throw new Exception("추출 할 수 없습니다.");
        }
        return getCopedApkFilename(cat);
    }

    private static String getCopedApkFilename(File cat){
        return cat.toString();
    }

    private static String apkFilename(AppData appData){
        return appData.getAppName();
    }


    public static Boolean deleteAllApk(Context context){
        PreferenceData preferenceData = MainActivity.getPreferenceData();
        File pfi = new File(preferenceData.getDirPath());
        if (pfi.exists() && pfi.isDirectory()){
            File[] files= pfi.listFiles();
            for (File file : files){
                if(file.getPath().endsWith(".apk")) {
                    file.delete();
                }
            }
            Toast.makeText(context, "폴더안 APK가 전부 삭제되었습니다", Toast.LENGTH_LONG).show();
            if(files.length == 0){
                return false;
            }
        }
        return true;
    }

    public static Boolean deleteAPk(String path, Context context){
        File file = new File(path);
        if(file.exists()){
            file.delete();
            Toast.makeText(context, "삭제 하였습니다.", Toast.LENGTH_SHORT).show();
            return true;
        }
        Toast.makeText(context, "존재 하지 않는 파일입니다.", Toast.LENGTH_SHORT).show();
        return false;
    }
    public static void apaterRefresh(ApkDirAdapter apkDirAdapter){
        apkDirAdapter.notifyDataSetChanged();
    }
}
