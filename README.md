# Weather Forecast App 🌤️

A modern, dynamic weather application built with Android Studio, leveraging OpenWeatherMap API, and designed with Material Design 2 principles. This app provides accurate and visually appealing weather updates.

## Features ✨
- **Real-Time Weather Updates**: Displays current conditions and a 5-day forecast.
- **Dynamic Visuals**: Backgrounds and icons change based on weather conditions.
- **Material Design 2 Components**: Uses MaterialCardView, Bottom App Bars, and ExtendedFloatingActionButton.
- **Search Functionality**: Find weather updates for any city using a MaterialSearchView.
- **Data Persistence**: Stores weather data locally with ObjectBox.
- **Charts**: Visualize weather trends with MPAndroidChart.
- **Animations**: Beautiful Lottie animations for a delightful user experience.

## Requirements 🛠️
- Android Studio: Latest version
- Java Development Kit (JDK): Version 17
- Android SDK: 34
- Minimum Supported API Level: 26
- Material Components Library: Version 1.12.0

## Libraries & Dependencies 📚
- **FastAdapter**: For efficient RecyclerView handling.
- **Retrofit and OkHttp**: For API integration.
- **ObjectBox**: Lightweight database for local storage.
- **RxAndroid**: Reactive programming tools.
- **Glide**: For smooth image loading and caching.
- **Lottie-Android**: For After Effects animations.
- **MaterialSearchView**: Modern search interface.
- **MPAndroidChart**: For data visualization.

## Installation & Setup 🚀
1. Clone the Repository:
   ```bash
   git clone https://github.com/shaddytosh/weather-forecast-app.git
   cd weather-forecast-app
## Open in Android Studio:

1. Launch Android Studio.
2. Click on **File > Open** and select the project folder.

## Set Up API Key:

1. Sign up at [OpenWeatherMap](https://openweathermap.org) to get an API key.
2. Add the API key in the `res/values/strings.xml` file:
    ```xml
    <string name="open_weather_map_api">YOUR_API_KEY_HERE</string>

# **Build and Run**

1. Sync the project with Gradle files.
2. Run the app on an emulator or connected device.

# **Approach & Challenges 💡**

### **Architecture**
- Follows MVVM (Model-View-ViewModel) for clear separation of concerns.

### **Design**
- Inspired by **Dribble**

### **Responsiveness**
- Ensures a seamless experience across devices.

### **Challenges:**
- **Dynamic Visuals**: Creating seamless transitions for weather-based themes.
  - **Solution**: Utilized Lottie animations and Glide for dynamic backgrounds.
  
- **API Integration**: Handling errors and caching data for offline usage.
  - **Solution**: Combined Retrofit with ObjectBox for smooth data flow.

# **Demo 📸**
👉  [Download APK](https://github.com/shaddytosh/weather-forecast/blob/main/weather-forecast-1.0.1-release.apk)

# **License 📜**
This app is licensed under the [Apache License 2.0](LICENSE). See the LICENSE file for details.

# **Credits 🙌**
- **Weather Data**: OpenWeatherMap.
