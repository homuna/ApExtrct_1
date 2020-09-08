package com.sto.AppExtractor;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;

public class AppData {

    private Drawable icon = null;
    private String packageName = null;
    private ComponentName componentName = null;
    private String appName = null;
    private String appId = null;
    private String src = null;

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public ComponentName getComponentName() {
        return componentName;
    }

    public void setComponentName(ComponentName componentName) {
        this.componentName = componentName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public AppData(String packageName, String appName, String src, Drawable icon){
        this.packageName = packageName;
        this.appName = appName;
        this.src = src;
        this.icon = icon;
    }
}
