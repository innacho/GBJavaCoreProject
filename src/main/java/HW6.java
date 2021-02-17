// С помощью http запроса получить в виде json строки погоду в Санкт-Петербурге на период времени,
// пересекающийся со следующим занятием (например, выборка погода на следующие 5 дней - подойдет)
// Подобрать источник самостоятельно. Можно использовать api accuweather, порядок следующий:
// зарегистрироваться, зарегистрировать тестовое приложение для получения api ключа, найдите
// нужный endpoint и изучите документацию. Бесплатный тарифный план предполагает получение погоды
// не более чем на 5 дней вперед (этого достаточно для выполнения д/з).

import okhttp3.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class HW6 {

    private static final String BASE_HOST = "dataservice.accuweather.com";
    private static final String FORECASTS = "forecasts";
    private static final String LOCATIONS = "locations";
    private static final String API_VERSION = "v1";
    private static final String FORECAST_TYPE = "daily";
    private static final String FORECAST_PERIOD = "5day";
    private static final String API_KEY = "ZnKd4kTYc68bc36GD8GEqWoC70D6IGMB";
    private static final String CITY = "Москва";
    private static final String FILE = "forecast.json";

    public static void main(String args[]) throws IOException {

        System.out.printf("Хотите узнать погоду в городе %s?\n", CITY);

        //чтение из консоли
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine();

        System.out.printf("Вы ответили: %s, а прогноз в городе %s смотрите в файле %s \n", answer, CITY, FILE);

        // Экземпляр класса OkHttpClient выполняет всю работу по созданию и отправке запросов
        // Построим экземпляр класса с помощью паттерна проектирования builder
        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .followRedirects(true)
                .retryOnConnectionFailure(true)
                .build();

        //с помощью GET запроса определим LocationKey для Москвы

        //сначала построим URL запроса через билдер
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(BASE_HOST)
                .addPathSegment(LOCATIONS)
                .addPathSegment(API_VERSION)
                .addPathSegment("cities")
                .addPathSegment("search")
                .addQueryParameter("apikey", API_KEY)
                .addQueryParameter("q", CITY)
                .build();

        //System.out.println(url.toString());


        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response responseGetLocationKey = client.newCall(request).execute();

        if(responseGetLocationKey.code() != 200){
            System.out.println("Не получится узнать погоду, извините.");
            return;
        }

        String responseBody = responseGetLocationKey.body().string();
        //System.out.println(responseBody);

        // Распарсим строку чтобы взять locationKey
        String locationKey = responseBody.split(":")[2];

        locationKey = locationKey.split(",")[0];
        locationKey = locationKey.replaceAll("[^\\p{L}\\p{Nd}]+", "");
        //System.out.println(locationKey);


    //делаем запрос на ежедневный прогноз на 5 дней

        url = new HttpUrl.Builder()
                .scheme("http")
                .host(BASE_HOST)
                .addPathSegment(FORECASTS)
                .addPathSegment(API_VERSION)
                .addPathSegment(FORECAST_TYPE)
                .addPathSegment(FORECAST_PERIOD)
                .addPathSegment(locationKey)
                .addQueryParameter("apikey", API_KEY)
                .addQueryParameter("language", "ru-ru")
                .addQueryParameter("metric", "true")
                .build();

        //System.out.println(url.toString());

        request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response responseGetForecast = client.newCall(request).execute();

        if(responseGetForecast.code() != 200){
            System.out.println("Не получится узнать погоду, извините.");
            return;
        }

        responseBody = responseGetForecast.body().string();
        //System.out.println(responseBody);

        //выведем результат запроса на погоду в json файл
        try (PrintWriter out = new PrintWriter(FILE)) {
            out.println(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
