package com.craftsilicon.weather.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatDelegate;

import com.craftsilicon.weather.BuildConfig;
import com.craftsilicon.weather.app.dagger.components.ApplicationComponent;
import com.craftsilicon.weather.app.dagger.components.DaggerApplicationComponent;
import com.craftsilicon.weather.app.dagger.modules.ApplicationModule;
import com.craftsilicon.weather.app.models.db.MyObjectBox;
import com.craftsilicon.weather.app.utils.LocaleManager;
import com.craftsilicon.weather.app.utils.SharedPreferencesUtil;

import java.util.Locale;

import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;


public class MainApplication extends Application {
    private ApplicationComponent applicationComponent;
    public static LocaleManager localeManager;

    private static BoxStore boxStore;

    public static BoxStore getBoxStore() {
        return boxStore;
    }

    @Override
    public void onCreate() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate();

        createBoxStore();
        applicationComponent =  DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        setDefaultLocale();

        if (SharedPreferencesUtil.getInstance(this).isDarkThemeEnabled())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


    }

    private void createBoxStore() {
        boxStore = MyObjectBox.builder().androidContext(MainApplication.this).build();
        if (BuildConfig.DEBUG) {
            new AndroidObjectBrowser(boxStore).start(this);
        }
    }

    public static MainApplication getInstance(Application application) {
        return (MainApplication) application;
    }

    @Override
    protected void attachBaseContext(Context base) {
        localeManager = new LocaleManager(base);

        super.attachBaseContext(localeManager.setLocale(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        localeManager.setLocale(this);
    }
    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    private void setDefaultLocale() {
        String lang = getSharedPreferences("Settings", MODE_PRIVATE).getString("Language", "");
        if (!TextUtils.isEmpty(lang)) {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
    }


}
