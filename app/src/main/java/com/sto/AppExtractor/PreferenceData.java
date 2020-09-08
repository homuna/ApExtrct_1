package com.sto.AppExtractor;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class PreferenceData {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public static final String KeyDirPath = "perfDirPath";


    public PreferenceData(Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = sharedPreferences.edit();
    }

    public String getDirPath(){
        return sharedPreferences.getString(KeyDirPath, ExtractUtil.defalutFileDir(context).getPath());
    }

    public void setDirPath(String path){
        editor.putString(KeyDirPath, path);
        editor.commit();
    }
}
