package com.silvan.weathertgbot.models;

public class WeatherResponse {
    private Main main;
    private Weather[] weathers;

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Weather[] getWeathers() {
        return weathers;
    }

    public void setWeathers(Weather[] weathers) {
        this.weathers = weathers;
    }
}
