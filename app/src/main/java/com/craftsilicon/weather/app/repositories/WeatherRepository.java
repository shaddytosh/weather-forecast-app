package com.craftsilicon.weather.app.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.craftsilicon.weather.app.api.APIResponse;
import com.craftsilicon.weather.app.api.ApiManager;
import com.craftsilicon.weather.app.api.responses.CurrentWeatherResponse;
import com.craftsilicon.weather.app.api.responses.FiveDayResponse;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class WeatherRepository {
    private final ApiManager apiManager;

    @Inject
    public WeatherRepository(ApiManager apiManager) {
        this.apiManager = apiManager;
    }

    public LiveData<APIResponse<CurrentWeatherResponse>> getCurrentWeather(String q, String units, String lang,
                                                                           String appId) {
        MutableLiveData<APIResponse<CurrentWeatherResponse>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<CurrentWeatherResponse> response = apiManager.getCurrentWeather(q, units, lang, appId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<FiveDayResponse>> getFiveDaysWeather(String q, String units, String lang,
                                                              String appId) {
        MutableLiveData<APIResponse<FiveDayResponse>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<FiveDayResponse> response = apiManager.getFiveDaysWeather(q, units, lang, appId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }


}

