package com.craftsilicon.weather.app.api;

import android.app.Application;

import com.craftsilicon.weather.app.api.responses.CurrentWeatherResponse;
import com.craftsilicon.weather.app.api.responses.FiveDayResponse;
import com.craftsilicon.weather.app.sharedprefs.SecurePrefs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Singleton
public class ApiManager {
    private static final int READ_TIMEOUT = 30;
    private static final int CONNECT_TIMEOUT = 30;
    private static final int WRITE_TIMEOUT = 30;
    public static final String SERVER_URL = "https://api.openweathermap.org/data/2.5/";

    private static final int NUMBER_OF_THREADS = 6;
    private static final ExecutorService apiExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private final RestApi api;
    private final Application application;

    @Inject
    public ApiManager(SecurePrefs securePrefs, Application application) {
        this.application = application;

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(logging);


        builder.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .method(original.method(), original.body());

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });


        OkHttpClient client = builder.build();


        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .create();


        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(SERVER_URL)
                .client(client)
                .build();

        api = retrofit.create(RestApi.class);
    }

    public static void execute(Runnable runnable) {
        apiExecutor.execute(runnable);
    }

    public Application getApplication() {
        return application;
    }


    public APIResponse<CurrentWeatherResponse> getCurrentWeather(String q, String units, String lang,
                                                                 String appId) throws IOException {

        return new APIResponse<>(api.getCurrentWeather(q, units, lang, appId).execute());
    }

    public APIResponse<FiveDayResponse> getFiveDaysWeather(String q, String units, String lang,
                                                           String appId) throws IOException {
        return new APIResponse<>(api.getFiveDaysWeather(q, units, lang, appId).execute());
    }


}
