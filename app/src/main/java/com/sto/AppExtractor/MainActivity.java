package com.sto.AppExtractor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragAndDropPermissions;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    SearchView searchView;

    private ArrayList<AppData> appDatalist;
    private ArrayList<AppData> appDialoglist;
    private RecyclerView main_recyclerview;
    private AppAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private static PreferenceData mPreferenceData;
    private PreferenceData preferenceData;

    final static int STORAGE_PERMISSION = 1001;
    final static String AD_ID = "ca-app-pub-3432383016939902/7366186335";

    private static ApkDirAdapter apkDirAdapter;

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new  AdRequest.Builder().build();
        adView.loadAd(adRequest);

        main_recyclerview = (RecyclerView) findViewById(R.id.main_recycler);
        main_recyclerview.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        main_recyclerview.setLayoutManager(layoutManager);

        mPreferenceData = new PreferenceData(this);
        preferenceData = getPreferenceData();

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        appDatalist = new ArrayList<AppData>();
        appDialoglist =  new ArrayList<AppData>();

        String ads = ExtractUtil.defalutFileDir(this).getPath();
        Log.e("test", ads);

        checkPermission();
        //ExtractUtil.makeDefalutFileDir(this);

        try {
            appDatalist.clear();
            installedApp();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        adapter = new AppAdapter(this, appDatalist);
        main_recyclerview.setAdapter(adapter);


        String path = ExtractUtil.defalutFileDir(this).getPath();
        Log.e("File","path : " + path);
        String preferpath = preferenceData.getDirPath();
        Log.e("File", "prepath : " + preferpath);

        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_toolbar,menu);

        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();


        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
        searchView.setQueryHint("검색어를 입력하세요");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.getFilter().filter(newText);
                    Log.e("serchview", "serchview Text : " + newText);
                    return false;
                }
            });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case  R.id.search:
                return true;

            case R.id.dialog_menu:
                appDialoglist.clear();
                appDialoglist = extractedAppList(this);
                dialogCall(this, appDialoglist);
                return true;

            case R.id.Setting_menu:
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void installedApp() throws PackageManager.NameNotFoundException {

        PackageManager pkgMgr = this.getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> mApps = pkgMgr.queryIntentActivities(mainIntent, 0);

        Collections.sort(mApps, new ResolveInfo.DisplayNameComparator(pkgMgr));

        for (int i = 0; i < mApps.size(); i++)
        {
            String app_packagename = mApps.get(i).activityInfo.packageName;
            String app_name = mApps.get(i).activityInfo.loadLabel(pkgMgr).toString();
            Drawable app_icon = pkgMgr.getApplicationIcon(app_packagename);
            String app_src = pkgMgr.getApplicationInfo(app_packagename,0).sourceDir;

            AppData appData = new AppData(app_packagename, app_name, app_src, app_icon);
            appDatalist.add(appData);
        }

    }


    private ArrayList<AppData> extractedAppList(Context context){

        ArrayList<AppData> result = new ArrayList<>();
        File file = new File(preferenceData.getDirPath());

        int i = 0;

        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if(files != null){
                for (File infile : files){
                    String filename = infile.getName();
                    Log.e("filename", filename);
                    String filepath = infile.getPath();
                    Log.e("filepath", filepath);
                    if(infile.getPath().endsWith(".apk")) {
                      String filePath = infile.getPath();
                      PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
                      if (packageInfo != null) {
                          i++;
                         ApplicationInfo appinfo = packageInfo.applicationInfo;

                         appinfo.sourceDir = filePath;
                         appinfo.publicSourceDir = filePath;
                         String app_packagename = appinfo.packageName;
                         String app_name = appinfo.loadLabel(getPackageManager()).toString();
                         Drawable app_icon = appinfo.loadIcon(this.getPackageManager());

                          Log.e("filename", "file name : " + app_name);

                         AppData appData = new AppData(app_packagename, app_name, filePath, app_icon);
                         result.add(appData);
                        }
                    }
                }
            }
        }
        Log.e("file", "file i : " + Integer.toString(i));
        Log.e("result", "result size : " + Integer.toString(result.size()));
        return result;
    }

    private void dialogCall(Context context, ArrayList<AppData> ArrayAppData){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = getLayoutInflater().inflate(R.layout.dialog_list, null);
        builder.setView(view);
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog dialog = builder.create();
        ListView listView = (ListView)view.findViewById(R.id.listview);
        apkDirAdapter = new ApkDirAdapter(ArrayAppData, context);
        listView.setAdapter(apkDirAdapter);

        dialog.show();
    }

    public static void refreshlistview(){
        apkDirAdapter.notifyDataSetChanged();
    }

    private void nullDialogCall(Context context){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("ApkExtractor")
                .setMessage("폴더에 APK파일이 존재하지 않습니다.")
                .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }


    public void checkPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_PERMISSION :
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                }
                else{
                    Toast.makeText(this, "사용자 지정 폴더를 사용하기 위해서는 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                }
        }
    }

    public static PreferenceData getPreferenceData() {
        return mPreferenceData;
    }


}
