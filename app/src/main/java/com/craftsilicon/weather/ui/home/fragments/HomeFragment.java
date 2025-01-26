package com.craftsilicon.weather.ui.home.fragments;


import android.Manifest;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.craftsilicon.weather.R;
import com.craftsilicon.weather.app.MainApplication;
import com.craftsilicon.weather.app.api.responses.CurrentWeatherResponse;
import com.craftsilicon.weather.app.api.responses.FiveDayResponse;
import com.craftsilicon.weather.app.models.CityInfo;
import com.craftsilicon.weather.app.models.db.CurrentWeather;
import com.craftsilicon.weather.app.models.db.FiveDayWeather;
import com.craftsilicon.weather.app.models.db.ItemHourlyDB;
import com.craftsilicon.weather.app.models.fivedayweather.ItemHourly;
import com.craftsilicon.weather.app.navigation.BaseFragment;
import com.craftsilicon.weather.app.utils.AppUtil;
import com.craftsilicon.weather.app.utils.Constants;
import com.craftsilicon.weather.app.utils.DbUtil;
import com.craftsilicon.weather.app.utils.SnackbarUtil;
import com.craftsilicon.weather.app.utils.TextViewFactory;
import com.craftsilicon.weather.databinding.FragmentHomeBinding;
import com.craftsilicon.weather.ui.home.activity.HourlyActivity;
import com.craftsilicon.weather.ui.home.viewModel.WeatherViewModel;
import com.github.pwittchen.prefser.library.rx2.Prefser;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.query.Query;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscriptionList;
import io.reactivex.disposables.CompositeDisposable;


public class HomeFragment extends BaseFragment {

    private FragmentHomeBinding binding;
    private WeatherViewModel weatherViewModel;

    private FastAdapter<FiveDayWeather> mFastAdapter;
    private ItemAdapter<FiveDayWeather> mItemAdapter;
    private CompositeDisposable disposable = new CompositeDisposable();
    private String defaultLang = "en";
    private List<FiveDayWeather> fiveDayWeathers;
    private FiveDayWeather todayFiveDayWeather;
    private Prefser prefser;
    private Box<CurrentWeather> currentWeatherBox;
    private Box<FiveDayWeather> fiveDayWeatherBox;
    private Box<ItemHourlyDB> itemHourlyDBBox;
    private DataSubscriptionList subscriptions = new DataSubscriptionList();
    private boolean isLoad = false;
    private CityInfo cityInfo;
    private String apiKey;
    private Typeface typeface;
    private int[] colors;
    private int[] colorsAlpha;
    private int PERMISSION_REQUEST_CODE = 10001;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbarLayout.toolbar);


        // Initialize other components
        initSearchView();
        initValues();
        setupTextSwitchers();
        initRecyclerView();
        showStoredCurrentWeather();
        showStoredFiveDayWeather();
        checkLastUpdate();

        // Request permissions if needed
        if (Build.VERSION.SDK_INT > 32) {
            if (!shouldShowRequestPermissionRationale("112")) {
                getNotificationPermission();
            }
        }

        return binding.getRoot();

    }

    private void initSearchView() {
        binding.toolbarLayout.searchView.setVoiceSearch(false);
        binding.toolbarLayout.searchView.setHint(getString(R.string.search_label));
        binding.toolbarLayout.searchView.setCursorDrawable(R.drawable.custom_curosr);
        binding.toolbarLayout.searchView.setEllipsize(true);
        binding.toolbarLayout.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                requestWeather(query, true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        binding.toolbarLayout.cvSearchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.toolbarLayout.searchView.showSearch();
            }
        });

    }

    private void initValues() {
        colors = getResources().getIntArray(R.array.mdcolor_500);
        colorsAlpha = getResources().getIntArray(R.array.mdcolor_500_alpha);
        prefser = new Prefser(requireContext());
        BoxStore boxStore = MainApplication.getBoxStore();
        currentWeatherBox = boxStore.boxFor(CurrentWeather.class);
        fiveDayWeatherBox = boxStore.boxFor(FiveDayWeather.class);
        itemHourlyDBBox = boxStore.boxFor(ItemHourlyDB.class);
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        long lastStored = prefser.get(Constants.LAST_STORED_CURRENT, Long.class, 0L);


        binding.swipeContainer.setOnRefreshListener(() -> {
            cityInfo = prefser.get(Constants.CITY_INFO, CityInfo.class, null);
            if (cityInfo != null) {
                displayLastUpdated();
                if (AppUtil.isTimePass(lastStored)) {
                    requestWeather(cityInfo.getName(), false);
                } else {
                    binding.swipeContainer.setRefreshing(false);
                }
            } else {
                binding.swipeContainer.setRefreshing(false);
            }
        });

        typeface = ResourcesCompat.getFont(requireContext(), R.font.open_sans);


        binding.contentMainLayout.todayMaterialCard.setOnClickListener(v -> {
            if (todayFiveDayWeather != null) {
                Intent intent = new Intent(requireContext(), HourlyActivity.class);
                intent.putExtra(Constants.FIVE_DAY_WEATHER_ITEM, todayFiveDayWeather);
                startActivity(intent);
            }
        });
    }

    private void displayLastUpdated() {
        long lastStored = prefser.get(Constants.LAST_STORED_CURRENT, Long.class, 0L);

        Date date = new Date(lastStored);

        Calendar currentCalendar = Calendar.getInstance();
        Calendar storedCalendar = Calendar.getInstance();
        storedCalendar.setTime(date);

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String time = timeFormat.format(date);

        String formattedDate;
        if (currentCalendar.get(Calendar.YEAR) == storedCalendar.get(Calendar.YEAR) &&
                currentCalendar.get(Calendar.DAY_OF_YEAR) == storedCalendar.get(Calendar.DAY_OF_YEAR)) {
            // It's today
            formattedDate = String.format("Today at %s", time);
        } else {
            // Format as "Saturday, Jan 25 at 11:45 PM"
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d 'at' hh:mm a", Locale.getDefault());
            formattedDate = dateFormat.format(date);
        }

        binding.contentMainLayout.tvLastStored.setText(String.format("Last updated on %s", formattedDate));

    }
        private void setupTextSwitchers() {
        binding.contentMainLayout.tempTextView.setFactory(new TextViewFactory(requireContext(), R.style.TempTextView, true, typeface));
        binding.contentMainLayout.tempTextView.setInAnimation(requireContext(), R.anim.slide_in_right);
        binding.contentMainLayout.tempTextView.setOutAnimation(requireContext(), R.anim.slide_out_left);
        binding.contentMainLayout.descriptionTextView.setFactory(new TextViewFactory(requireContext(), R.style.DescriptionTextView, true, typeface));
        binding.contentMainLayout.descriptionTextView.setInAnimation(requireContext(), R.anim.slide_in_right);
        binding.contentMainLayout.descriptionTextView.setOutAnimation(requireContext(), R.anim.slide_out_left);
        binding.contentMainLayout.humidityTextView.setFactory(new TextViewFactory(requireContext(), R.style.HumidityTextView, false, typeface));
        binding.contentMainLayout.humidityTextView.setInAnimation(requireContext(), R.anim.slide_in_bottom);
        binding.contentMainLayout.humidityTextView.setOutAnimation(requireContext(), R.anim.slide_out_top);
        binding.contentMainLayout.windTextView.setFactory(new TextViewFactory(requireContext(), R.style.WindSpeedTextView, false, typeface));
        binding.contentMainLayout.windTextView.setInAnimation(requireContext(), R.anim.slide_in_bottom);
        binding.contentMainLayout.windTextView.setOutAnimation(requireContext(), R.anim.slide_out_top);
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.contentMainLayout.recyclerView.setLayoutManager(layoutManager);
        mItemAdapter = new ItemAdapter<>();
        mFastAdapter = FastAdapter.with(mItemAdapter);
        binding.contentMainLayout.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.contentMainLayout.recyclerView.setAdapter(mFastAdapter);
        binding.contentMainLayout.recyclerView.setFocusable(false);
        mFastAdapter.withOnClickListener(new OnClickListener<FiveDayWeather>() {
            @Override
            public boolean onClick(@Nullable View v, @NonNull IAdapter<FiveDayWeather> adapter, @NonNull FiveDayWeather item, int position) {
                Intent intent = new Intent(requireContext(), HourlyActivity.class);
                intent.putExtra(Constants.FIVE_DAY_WEATHER_ITEM, item);
                startActivity(intent);
                return true;
            }
        });
    }

    private void showStoredCurrentWeather() {
        Query<CurrentWeather> query = DbUtil.getCurrentWeatherQuery(currentWeatherBox);
        query.subscribe(subscriptions).on(AndroidScheduler.mainThread())
                .observer(new DataObserver<List<CurrentWeather>>() {
                    @Override
                    public void onData(@NonNull List<CurrentWeather> data) {
                        if (!isAdded() || getContext() == null) {
                            // Fragment is not attached; skip updates
                            return;
                        }
                        if (data.size() > 0) {
                            hideEmptyLayout();
                            CurrentWeather currentWeather = data.get(0);
                            if (isLoad) {
                                binding.contentMainLayout.tempTextView.setText(String.format(Locale.getDefault(),
                                        "%.0f°", currentWeather.getTemp()));
                                binding.contentMainLayout.descriptionTextView.setText(AppUtil.getWeatherStatus(currentWeather.getWeatherId()));
                                binding.contentMainLayout.humidityTextView.setText(String.format(Locale.getDefault(),
                                        "%d%%", currentWeather.getHumidity()));
                                binding.contentMainLayout.windTextView.setText(String.format(Locale.getDefault(),
                                        getResources().getString(R.string.wind_unit_label), currentWeather.getWindSpeed()));
                            } else {
                                binding.contentMainLayout.tempTextView.setCurrentText(String.format(Locale.getDefault(),
                                        "%.0f°", currentWeather.getTemp()));
                                binding.contentMainLayout.descriptionTextView.setCurrentText(AppUtil.getWeatherStatus(currentWeather.getWeatherId()));
                                binding.contentMainLayout.humidityTextView.setCurrentText(String.format(Locale.getDefault(),
                                        "%d%%", currentWeather.getHumidity()));
                                binding.contentMainLayout.windTextView.setCurrentText(String.format(Locale.getDefault(),
                                        getResources().getString(R.string.wind_unit_label), currentWeather.getWindSpeed()));
                            }
                            binding.contentMainLayout.animationView.setAnimation(AppUtil.getWeatherAnimation(currentWeather.getWeatherId()));
                            binding.contentMainLayout.animationView.playAnimation();
                        }
                    }
                });
    }

    private void showStoredFiveDayWeather() {
        Query<FiveDayWeather> query = DbUtil.getFiveDayWeatherQuery(fiveDayWeatherBox);
        query.subscribe(subscriptions).on(AndroidScheduler.mainThread())
                .observer(new DataObserver<List<FiveDayWeather>>() {
                    @Override
                    public void onData(@NonNull List<FiveDayWeather> data) {
                        if (data.size() > 0) {
                            todayFiveDayWeather = data.remove(0);
                            mItemAdapter.clear();
                            mItemAdapter.add(data);
                        }
                    }
                });
    }

    private void checkLastUpdate() {
        cityInfo = prefser.get(Constants.CITY_INFO, CityInfo.class, null);
        if (cityInfo != null) {
            binding.toolbarLayout.cardText.setText(String.format("%s, %s", cityInfo.getName(), cityInfo.getCountry()));
            if (prefser.contains(Constants.LAST_STORED_CURRENT)) {
                long lastStored = prefser.get(Constants.LAST_STORED_CURRENT, Long.class, 0L);
                if (AppUtil.isTimePass(lastStored)) {
                    requestWeather(cityInfo.getName().trim(), false);
                }
            } else {
                requestWeather(cityInfo.getName().trim(), false);
            }
        } else {
            showEmptyLayout();
        }

    }


    private void requestWeather(String cityName, boolean isSearch) {
        if (AppUtil.isNetworkConnected()) {
            getCurrentWeather(cityName, isSearch);
            getFiveDaysWeather(cityName);

        } else {
            SnackbarUtil
                    .with(binding.swipeContainer)
                    .setMessage(getString(R.string.no_internet_message))
                    .setDuration(SnackbarUtil.LENGTH_LONG)
                    .showError();
            binding.swipeContainer.setRefreshing(false);
        }
    }

    private void getCurrentWeather(String cityName, boolean isSearch) {
        apiKey = getResources().getString(R.string.open_weather_map_api);

        binding.swipeContainer.setRefreshing(true);
        weatherViewModel.getCurrentWeather(cityName, Constants.UNITS, defaultLang, apiKey).
                observe(getViewLifecycleOwner(), apiResponse -> {
                    binding.swipeContainer.setRefreshing(false);

                    if (apiResponse != null) {
                        if (apiResponse.isSuccessful()) {
                            CurrentWeatherResponse weatherResponse = apiResponse.body();
                            isLoad = true;
                            storeCurrentWeather(weatherResponse);
                            storeCityInfo(weatherResponse);
                            binding.swipeContainer.setRefreshing(false);
                            if (isSearch) {
                                prefser.remove(Constants.LAST_STORED_MULTIPLE_DAYS);
                            }
                        } else {
                            binding.swipeContainer.setRefreshing(false);
                            handleErrorCode(apiResponse.code(), apiResponse.errorBody());
                        }
                    }

                });

    }

    private void handleErrorCode(int code, String message) {
        if (code == 404) {
            SnackbarUtil
                    .with(binding.swipeContainer)
                    .setMessage(getString(R.string.no_city_found_message))
                    .setDuration(SnackbarUtil.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.search_label), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            binding.toolbarLayout.searchView.showSearch();
                        }
                    })
                    .showWarning();

        } else if (code == 401) {
            SnackbarUtil
                    .with(binding.swipeContainer)
                    .setMessage(getString(R.string.invalid_api_key_message))
                    .setDuration(SnackbarUtil.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.ok_label), v -> {

                    })
                    .showError();

        } else {
            SnackbarUtil
                    .with(binding.swipeContainer)
                    .setMessage(message)
                    .setDuration(SnackbarUtil.LENGTH_LONG)
                    .setAction(getResources().getString(R.string.retry_label), v -> {
                        if (cityInfo != null) {
                            requestWeather(cityInfo.getName(), false);
                        } else {
                            binding.toolbarLayout.searchView.showSearch();
                        }
                    })
                    .showWarning();
        }
    }

    private void showEmptyLayout() {
        Glide.with(requireContext()).load(R.drawable.no_city).into(binding.contentEmptyLayout.noCityImageView);
        binding.contentEmptyLayout.emptyLayout.setVisibility(View.VISIBLE);
        binding.contentMainLayout.nestedScrollView.setVisibility(View.GONE);
    }

    private void hideEmptyLayout() {
        binding.contentEmptyLayout.emptyLayout.setVisibility(View.GONE);
        binding.contentMainLayout.nestedScrollView.setVisibility(View.VISIBLE);
    }


    private void storeCurrentWeather(CurrentWeatherResponse response) {
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setTemp(response.getMain().getTemp());
        currentWeather.setHumidity(response.getMain().getHumidity());
        currentWeather.setDescription(response.getWeather().get(0).getDescription());
        currentWeather.setMain(response.getWeather().get(0).getMain());
        currentWeather.setWeatherId(response.getWeather().get(0).getId());
        currentWeather.setWindDeg(response.getWind().getDeg());
        currentWeather.setWindSpeed(response.getWind().getSpeed());
        currentWeather.setStoreTimestamp(System.currentTimeMillis());
        prefser.put(Constants.LAST_STORED_CURRENT, System.currentTimeMillis());
        if (!currentWeatherBox.isEmpty()) {
            currentWeatherBox.removeAll();
            currentWeatherBox.put(currentWeather);
        } else {
            currentWeatherBox.put(currentWeather);
        }

        displayLastUpdated();
    }

    private void storeCityInfo(CurrentWeatherResponse response) {
        CityInfo cityInfo = new CityInfo();
        cityInfo.setCountry(response.getSys().getCountry());
        cityInfo.setId(response.getId());
        cityInfo.setName(response.getName());
        prefser.put(Constants.CITY_INFO, cityInfo);
        binding.toolbarLayout.cardText.setText(String.format("%s, %s", cityInfo.getName(), cityInfo.getCountry()));
    }


    private void getFiveDaysWeather(String cityName) {
        weatherViewModel.getFiveDaysWeather(cityName, Constants.UNITS, defaultLang, apiKey)
                .observe(getViewLifecycleOwner(), apiResponse -> {
                    if (apiResponse != null) {
                        if (apiResponse.isSuccessful()) {
                            // Handle successful response
                            FiveDayResponse response = apiResponse.body();
                            handleFiveDayHourlyResponse(response);

                        } else {
                            // Handle error response
                            handleErrorCode(apiResponse.code(), apiResponse.errorBody());
                        }
                    }
                });
    }


    private void handleFiveDayHourlyResponse(FiveDayResponse response) {
        fiveDayWeathers = new ArrayList<>();
        List<ItemHourly> list = response.getList();
        Calendar today = Calendar.getInstance(TimeZone.getDefault());

        for (int day = 0; day < 5; day++) {
            Calendar dayStart = AppUtil.getStartOfDay(today);
            Calendar dayEnd = AppUtil.getEndOfDay(today);

            List<ItemHourly> dailyHourlies = new ArrayList<>();
            for (ItemHourly item : list) {
                long itemTimestamp = item.getDt() * 1000L; // Convert to milliseconds
                if (itemTimestamp >= dayStart.getTimeInMillis() && itemTimestamp < dayEnd.getTimeInMillis()) {
                    dailyHourlies.add(item);
                }
            }

            if (!dailyHourlies.isEmpty()) {
                int color = colors[day];
                int colorAlpha = colorsAlpha[day];

                // Aggregate data for the day
                double maxTemp = Double.MIN_VALUE;
                double minTemp = Double.MAX_VALUE;
                double avgTemp = 0;
                int weatherId = dailyHourlies.get(0).getWeather().get(0).getId();

                for (ItemHourly item : dailyHourlies) {
                    double temp = item.getMain().getTemp();
                    avgTemp += temp;
                    maxTemp = Math.max(maxTemp, item.getMain().getTempMax());
                    minTemp = Math.min(minTemp, item.getMain().getTempMin());
                    weatherId = item.getWeather().get(0).getId();
                }
                avgTemp /= dailyHourlies.size();

                FiveDayWeather fiveDayWeather = new FiveDayWeather();
                fiveDayWeather.setWeatherId(weatherId);
                fiveDayWeather.setDt((int) (dayStart.getTimeInMillis() / 1000)); // Start timestamp
                fiveDayWeather.setMaxTemp(maxTemp);
                fiveDayWeather.setMinTemp(minTemp);
                fiveDayWeather.setTemp(avgTemp);
                fiveDayWeather.setColor(color);
                fiveDayWeather.setColorAlpha(colorAlpha);
                fiveDayWeather.setTimestampStart(dayStart.getTimeInMillis());
                fiveDayWeather.setTimestampEnd(dayEnd.getTimeInMillis());
                fiveDayWeathers.add(fiveDayWeather);
            }

            today.add(Calendar.DAY_OF_YEAR, 1); // Move to the next day
        }

        // Save the results
        if (!fiveDayWeatherBox.isEmpty()) {
            fiveDayWeatherBox.removeAll();
        }
        if (!itemHourlyDBBox.isEmpty()) {
            itemHourlyDBBox.removeAll();
        }
        for (FiveDayWeather fiveDayWeather : fiveDayWeathers) {
            long fiveDayWeatherId = fiveDayWeatherBox.put(fiveDayWeather);

            // Save hourly data for the day
            for (ItemHourly item : list) {
                long itemTimestamp = item.getDt() * 1000L; // Convert to milliseconds
                if (itemTimestamp >= fiveDayWeather.getTimestampStart() && itemTimestamp < fiveDayWeather.getTimestampEnd()) {
                    ItemHourlyDB itemHourlyDB = new ItemHourlyDB();
                    itemHourlyDB.setDt(item.getDt());
                    itemHourlyDB.setFiveDayWeatherId(fiveDayWeatherId);
                    itemHourlyDB.setTemp(item.getMain().getTemp());
                    itemHourlyDB.setWeatherCode(item.getWeather().get(0).getId());
                    itemHourlyDBBox.put(itemHourlyDB);
                }
            }
        }
    }

    public void getNotificationPermission() {
        try {
            if (Build.VERSION.SDK_INT > 32) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        binding.toolbarLayout.searchView.setMenuItem(item);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            // Handle search action if necessary
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.toolbarLayout.searchView.isSearchOpen()) {
                    binding.toolbarLayout.searchView.closeSearch();
                } else {
                    setEnabled(false);
                    requireActivity().onBackPressed();
                }
            }
        });
    }

}