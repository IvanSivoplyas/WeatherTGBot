package com.silvan.weathertgbot.controller;

import com.silvan.weathertgbot.models.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequestMapping("/bot")
public class WeatherTGBotController {
    private final String botToken;
    private final String weatherApiKey;
    private final RestTemplate restTemplate;

    public WeatherTGBotController(@Value("${telegram.bot.token}") String botToken,
                                  @Value("${weather.api.key}") String weatherApiKey,
                                  RestTemplate restTemplate) {
        this.botToken = botToken;
        this.weatherApiKey = weatherApiKey;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/webhook")
    public void receiveUpdate(@RequestBody Update update) {
        String chatId = update.getMessage().getChat().getId().toString();
        String cityName = update.getMessage().getText();

        String weatherInfo = getWeather(cityName);
        sendMessage(chatId, weatherInfo);
    }

    private String getWeather(String cityName) {
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric", cityName, weatherApiKey);
        WeatherResponse weatherResponse = restTemplate.getForObject(url, WeatherResponse.class);

        if (weatherResponse != null && weatherResponse.getMain() != null) {
            return String.format("Температура в %s: %.1f°C, Влажность: %d%%, Погода: %s",
                    cityName,
                    weatherResponse.getMain().getTemperature(),
                    weatherResponse.getMain().getHumidity(),
                    weatherResponse.getWeathers()[0].getDescription());
        } else {
            return "Город не найден. Проверьте название.";
        }
    }

    private void sendMessage(String chatId, String text) {
        String url = String.format("https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s", botToken, chatId, text);
        restTemplate.getForObject(url, String.class);
    }

}
