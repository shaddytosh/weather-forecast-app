package com.craftsilicon.weather.app.models.enums;

public enum WeatherCode {
    CLEAR(0),
    CLOUDY(1),
    RAINY(2),
    SNOWY(3),
    THUNDERSTORM(4);

    private final int value;

    WeatherCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static WeatherCode fromInt(int i) {
        for (WeatherCode code : values()) {
            if (code.getValue() == i) {
                return code;
            }
        }
        return CLEAR; // Default or fallback value
    }
}
