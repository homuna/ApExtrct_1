package com.sto.AppExtractor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.CustomViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<AppData> baseappDatalist;
    private ArrayList<AppData> appDatalist;


    public AppAdapter(Context mContext,ArrayList<AppData> appDatalist) {
        this.mContext = mContext;
        this.baseappDatalist = appDatalist;
        this.appDatalist = appDatalist;
    }

    @NonNull
    @Override
    public AppAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);


        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppAdapter.CustomViewHolder holder, int position) {

        final AppData appData = appDatalist.get(position);
        holder.appImage.setImageDrawable(appData.getIcon());
        holder.appName.setText(appData.getAppName());
        holder.appPackage.setText(appData.getPackageName());

        holder.appExtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ExtractUtil.extractAPk(appData);
                    Toast.makeText(mContext, "APK파일 추출이 성공하였습니다.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(mContext, "APK파일 추출실패.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return appDatalist.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView appImage;
        TextView appName;
        TextView appPackage;
        Button appExtract;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            appImage = itemView.findViewById(R.id.appImage);
            appName = itemView.findViewById(R.id.appName);
            appPackage = itemView.findViewById(R.id.appPackage);
            appExtract = itemView.findViewById(R.id.appExtract);
            
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            ArrayList<AppData> filteredList;
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()) {
                    filteredList = baseappDatalist;
                } else {
                    ArrayList<AppData> filteringList = new ArrayList<>();
                    for(AppData appData : appDatalist) {
                        if(appData.getAppName().toLowerCase().contains(charString.toLowerCase().trim())) {
                            filteringList.add(appData);
                            Log.e("Filter", "Filtering Name : " + appData.getAppName());
                            Log.e("Filter", "Filtering Name : " + filteringList.size());
                        }
                    }
                    filteredList = filteringList;
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                appDatalist = (ArrayList<AppData>)results.values;
                notifyDataSetChanged();
            }
        };
    }

}
