package com.craftsilicon.weather.app.sharedprefs;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class SharedPrefs {


    private final Context mContext;
    private final SharedPreferences mSharedPreferences;

    @Inject
    public SharedPrefs(Application context){
        mContext = context;
        mSharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void saveString(String key, String value){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value).apply();
    }

    public String getString(String key, String defaultValue){
        return mSharedPreferences.getString(key, defaultValue);
    }

    public void registerOnSharedPreferencesListener(SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

}
