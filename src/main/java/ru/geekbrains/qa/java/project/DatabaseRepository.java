package ru.geekbrains.qa.java.project;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

// Репозиторий для работы
public interface DatabaseRepository {

    boolean saveWeatherData(WeatherResponse weatherResponse) throws SQLException;

    List<WeatherResponse> getAllSavedData() throws IOException;
}
