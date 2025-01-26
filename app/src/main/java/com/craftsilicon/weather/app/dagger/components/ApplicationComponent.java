package com.craftsilicon.weather.app.dagger.components;


import com.craftsilicon.weather.app.dagger.modules.ApplicationModule;
import com.craftsilicon.weather.ui.home.viewModel.WeatherViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {


    void inject(WeatherViewModel weatherViewModel);

}