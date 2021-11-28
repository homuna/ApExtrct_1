package com.sto.AppExtractor;

import  android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.text.Html;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.codekidlabs.storagechooser.StorageChooser;

import java.security.Permission;


public class SettingActivity extends PreferenceActivity  {

    private Preference perfDirPath;
    private Preference perfAllDelete;
    private PreferenceData preferenceData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pre_setting);

        perfAllDelete = findPreference("preDeleteAll");
        perfDirPath = findPreference("preSetDir");

        preferenceData = MainActivity.getPreferenceData();

        perfDirPath.setSummary(preferenceData.getDirPath());
        perfDirPath.setDefaultValue(ExtractUtil.defalutFileDir(this).getPath());

        perfDirPath.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final StorageChooser chooser = new StorageChooser.Builder()
                        .withActivity(SettingActivity.this)
                        .withFragmentManager(getFragmentManager())
                        .withMemoryBar(true)
                        .allowCustomPath(true)
                        .setType(StorageChooser.DIRECTORY_CHOOSER)
                        .build();

                chooser.show();

                chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
                    @Override
                    public void onSelect(String path) {
                        preferenceData.setDirPath(path);
                        perfDirPath.setSummary(path);
                    }
                });
                return false;
            }
        });

        perfAllDelete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                allDeleteDialog();
                return false;
            }
        });
    }


    private void allDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("APK 파일 전체 삭제")
                .setMessage("사용자 지정 폴더안의 APK 파일을 전체 삭제 하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ExtractUtil.deleteAllApk(getApplicationContext());
            }
        });
        builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
