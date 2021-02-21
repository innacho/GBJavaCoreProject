package ru.geekbrains.qa.java.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class AccuWeatherProvider{

    private static final String BASE_HOST = "dataservice.accuweather.com";
    private static final String FORECAST_ENDPOINT = "forecasts";
    private static final String LOCATIONS = "locations";
    private static final String API_VERSION = "v1";
    private static final String FORECAST_TYPE = "daily";
    private static final String FORECAST_PERIOD = "5day";
    private static final String API_KEY = ApplicationGlobalState.getInstance().getApiKey();

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String selectedCity;

    public void getWeather() throws IOException {
        selectedCity = ApplicationGlobalState.getInstance().getSelectedCity();
        String cityKey = detectCityKey();
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(BASE_HOST)
                .addPathSegment(FORECAST_ENDPOINT)
                .addPathSegment(API_VERSION)
                .addPathSegment(FORECAST_TYPE)
                .addPathSegment(FORECAST_PERIOD)
                .addPathSegment(cityKey)
                .addQueryParameter("apikey", API_KEY)
                .addQueryParameter("language", "ru-ru")
                .addQueryParameter("metric", "true")
                .build();

        Request request = new Request.Builder()
                .addHeader("accept", "application/json")
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        //System.out.println(response.body().string());

        ArrayList<WeatherResponse> weatherResponses = new ArrayList<>();
        String jsonResponse = response.body().string();
        //System.out.println(jsonResponse);


        for (int i = 0; i < 5; i++) {
            WeatherResponse weatherResponse = new WeatherResponse();
            String date = objectMapper.readTree(jsonResponse).at("/DailyForecasts").get(i).at("/Date").asText();
            date = date.split("T")[0];
            weatherResponse.setDate(date);
            String weatherText = objectMapper.readTree(jsonResponse).at("/DailyForecasts").get(i).at("/Day/IconPhrase").asText();
            weatherResponse.setWeatherText(weatherText);
            float temperature = Float.parseFloat(objectMapper.readTree(jsonResponse).at("/DailyForecasts").get(i).at("/Temperature/Maximum/Value").asText());
            weatherResponse.setTemperature(temperature);
            weatherResponses.add(weatherResponse);
            System.out.println(weatherResponses.get(i).toString());
        }


            for (WeatherResponse weatherResp : weatherResponses){
                System.out.println("В городе "+ selectedCity + weatherResp.toString());
            }

    }


    public String detectCityKey() throws IOException {

        HttpUrl detectLocationURL = new HttpUrl.Builder()
                .scheme("http")
                .host(BASE_HOST)
                .addPathSegment(LOCATIONS)
                .addPathSegment(API_VERSION)
                .addPathSegment("cities")
                .addPathSegment("search")
                .addQueryParameter("apikey", API_KEY)
                .addQueryParameter("language", "ru-ru")
                .addQueryParameter("q", selectedCity)
                .build();

        Request request = new Request.Builder()
                .addHeader("accept", "application/json")
                .url(detectLocationURL)
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Невозможно прочесть информацию о городе. " +
                    "Код ответа сервера = " + response.code() + " тело ответа = " + response.body().string());
        }
        String jsonResponse = response.body().string();
        System.out.println("Произвожу поиск города " + selectedCity);

        if (objectMapper.readTree(jsonResponse).size() > 0) {
            String cityName = objectMapper.readTree(jsonResponse).get(0).at("/LocalizedName").asText();
            String countryName = objectMapper.readTree(jsonResponse).get(0).at("/Country/LocalizedName").asText();
            System.out.println("Найден город " + cityName + " в стране " + countryName);
        } else throw new IOException("Server returns 0 cities");

        return objectMapper.readTree(jsonResponse).get(0).at("/Key").asText();
    }
}
