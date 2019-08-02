package com.edgar.sample;

import android.app.Activity;

import androidx.annotation.NonNull;

/**
 * Created by Edgar on 2019/8/2.
 */
public class IndexItem {

    private String title;
    private Class<? extends Activity> activityClass;

    public IndexItem(String title, Class<? extends Activity> activityClass) {
        this.title = title;
        this.activityClass = activityClass;
    }

    public String getTitle() {
        return title;
    }

    public Class<? extends Activity> getActivityClass() {
        return activityClass;
    }

    @NonNull
    @Override
    public String toString() {
        return title;
    }
}
