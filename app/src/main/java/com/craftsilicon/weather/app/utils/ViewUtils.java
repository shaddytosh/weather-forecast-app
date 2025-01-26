package com.craftsilicon.weather.app.utils;

import android.content.Context;
import android.location.LocationManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.location.LocationManagerCompat;

public class ViewUtils {


    public static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
    }

    public static<T extends TextView> String getText(@NonNull T textView){
        return textView.getText()==null?"":textView.getText().toString().trim();
    }


}
