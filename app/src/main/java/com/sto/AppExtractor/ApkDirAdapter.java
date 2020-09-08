package com.sto.AppExtractor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;

public class ApkDirAdapter extends BaseAdapter {

    private ArrayList<AppData> Applist;
    private Context context;


    public ApkDirAdapter(ArrayList<AppData> Applist, Context context) {
        this.Applist = Applist;
        this.context = context;
    }

    @Override
    public int getCount() {
        return Applist.size();
    }

    @Override
    public Object getItem(int position) {
        return Applist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_list, parent, false);
        }

        TextView list_name = (TextView)convertView.findViewById(R.id.list_name);
        TextView list_package = (TextView)convertView.findViewById(R.id.list_package);
        ImageView list_icon = (ImageView) convertView.findViewById(R.id.list_icon);
        final Button delete_apk = (Button) convertView.findViewById(R.id.delete_apk);
        Button shared_aok = (Button) convertView.findViewById(R.id.share_apk);

        final AppData appData = Applist.get(position);

        list_name.setText(appData.getAppName());
        list_package.setText(appData.getPackageName());
        list_icon.setImageDrawable(appData.getIcon());

        delete_apk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExtractUtil.deleteAPk(appData.getSrc(), context);

                MainActivity.refreshlistview();
            }
        });

        shared_aok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File fl = new File(appData.getSrc());
                if(fl.exists()) {
                    Uri uri = FileProvider.getUriForFile(context, "com.sto.AppExtractor.fileprovider", new File(appData.getSrc()));
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.setType("application/vnd.android.package-archive");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(Intent.createChooser(intent, null));
                }
                else {
                    Toast.makeText(context, "존재 하지 않는 파일입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }


}
