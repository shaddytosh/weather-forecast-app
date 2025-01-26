package com.craftsilicon.weather.ui.home.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.craftsilicon.weather.app.MainApplication;
import com.craftsilicon.weather.app.api.APIResponse;
import com.craftsilicon.weather.app.api.responses.CurrentWeatherResponse;
import com.craftsilicon.weather.app.api.responses.FiveDayResponse;
import com.craftsilicon.weather.app.repositories.WeatherRepository;

import javax.inject.Inject;


public class WeatherViewModel extends AndroidViewModel {

    @Inject
    WeatherRepository weatherRepository;

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        ((MainApplication) application).getApplicationComponent().inject(this);
    }

    public LiveData<APIResponse<CurrentWeatherResponse>> getCurrentWeather(String q, String units, String lang,
                                                                           String appId) {
        return weatherRepository.getCurrentWeather(q, units, lang, appId);
    }

    public LiveData<APIResponse<FiveDayResponse>> getFiveDaysWeather(String q, String units, String lang,
                                                              String appId) {
        return weatherRepository.getFiveDaysWeather(q, units, lang, appId);
    }

}
