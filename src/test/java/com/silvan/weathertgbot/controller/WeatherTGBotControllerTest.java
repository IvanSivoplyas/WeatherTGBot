package com.silvan.weathertgbot.controller;

import com.silvan.weathertgbot.models.Main;
import com.silvan.weathertgbot.models.Weather;
import com.silvan.weathertgbot.models.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = WeatherTGBotController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class WeatherTGBotControllerTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherTGBotController weatherTGBotController;

    @Value("${telegram.bot.token}")
    private String botToken = "test_bot_token";

    @Value("${weather.api.key}")
    private String weatherApiKey = "test_weather_api_key";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        weatherTGBotController = new WeatherTGBotController(botToken, weatherApiKey, restTemplate);
    }

    @Test
    void testReceiveUpdate_Success() {
        Update update = createUpdate("Moscow");

        WeatherResponse weatherResponse = createWeatherResponse(20.5, 60, "clear sky");

        when(restTemplate.getForObject(anyString(), eq(WeatherResponse.class))).thenReturn(weatherResponse);

        weatherTGBotController.receiveUpdate(update);

        ArgumentCaptor<String> chatIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
        verify(restTemplate, times(1)).getForObject(contains("Moscow"), eq(WeatherResponse.class));
    }

    @Test
    void testReceiveUpdate_CityNotFound() {
        Update update = createUpdate("UnknownCity");

        when(restTemplate.getForObject(anyString(), eq(WeatherResponse.class))).thenReturn(null);

        weatherTGBotController.receiveUpdate(update);

        verify(restTemplate, times(1)).getForObject(anyString(), eq(WeatherResponse.class));
    }

    private Update createUpdate(String cityName) {
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(12345L);
        message.setChat(chat);
        message.setText(cityName);
        update.setMessage(message);
        return update;
    }

    private WeatherResponse createWeatherResponse(double temperature, int humidity, String description) {
        WeatherResponse weatherResponse = new WeatherResponse();
        Main main = new Main();
        main.setTemperature(temperature);
        main.setHumidity(humidity);
        weatherResponse.setMain(main);
        weatherResponse.setWeathers(new Weather[]{new Weather()});
        weatherResponse.getWeathers()[0].setDescription(description);
        return weatherResponse;
    }
}
