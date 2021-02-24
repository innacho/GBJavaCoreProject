package ru.geekbrains.qa.java.project;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Controller {

    WeatherProvider weatherProvider = new AccuWeatherProvider();

    public Controller() {
    }

    public void onUserInput(String input) throws IOException {
        int command = Integer.parseInt(input);
        switch (command) {
            case 1:
                getWeatherForecast();
                break;
            case 2:
                readDB();
                break;
        }
    }

    public void getWeatherForecast() throws IOException {
        List<WeatherResponse> weatherResponses = weatherProvider.getWeather();
        if ( weatherResponses == null ) {throw new IOException("Не удалось получить результат запроса погоды");}
        DatabaseRepositorySQLiteImpl myDB = ApplicationGlobalState.getInstance().getDB();
        if(myDB == null){
            myDB = new DatabaseRepositorySQLiteImpl();
            ApplicationGlobalState.getInstance().setDB(myDB);
        }

        for (WeatherResponse weatherResponse : weatherResponses){
            try {
               myDB.saveWeatherData(weatherResponse);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public void readDB() throws IOException {
        DatabaseRepositorySQLiteImpl myDB = ApplicationGlobalState.getInstance().getDB();
        if(myDB == null){ throw new IOException("БД пуста"); }
        List<WeatherResponse> DBData = myDB.getAllSavedData();
        for (WeatherResponse weatherResponse : DBData) {
            System.out.println(weatherResponse.toString());
        }
    }
}
