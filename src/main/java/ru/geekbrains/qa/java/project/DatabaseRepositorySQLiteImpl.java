package ru.geekbrains.qa.java.project;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseRepositorySQLiteImpl implements DatabaseRepository {

    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    String filename = null;
    String createTableQuery = "CREATE TABLE IF NOT EXISTS weather (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
        "city TEXT NOT NULL," +
        "date_time TEXT NOT NULL," +
        "weather_text TEXT NOT NULL," +
        "temperature REAL NOT NULL);";
    String insertWeatherQuery = "INSERT INTO weather (city, date_time, weather_text, temperature) VALUES (?,?,?,?)";

    // конструктор класса для работы с БД, будет инициализировать БД и пустую таблицу
    public DatabaseRepositorySQLiteImpl(){
        filename = ApplicationGlobalState.getInstance().getDBFilename();
        try {
            connection = this.getConnection();
            statement = connection.createStatement();
            // очистим таблицу, если она существует
            performDropTable();
            // создадим таблицу заново
            createTableIfNotExists();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + filename);
        return connection;
    }

    private void createTableIfNotExists() {
        try {
            statement.executeUpdate(createTableQuery);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public boolean saveWeatherData(WeatherResponse weatherResponse) throws SQLException {
        try {
            preparedStatement = connection.prepareStatement(insertWeatherQuery);
            preparedStatement.setString(1, weatherResponse.getCity());
            preparedStatement.setString(2, weatherResponse.getDate());
            preparedStatement.setString(3, weatherResponse.getWeatherText());
            preparedStatement.setDouble(4, weatherResponse.getTemperature());
            return preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        throw new SQLException("Failure on saving weather object");
    }

    @Override
    public List<WeatherResponse> getAllSavedData() throws IOException {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM weather");
            List<WeatherResponse> resultList = new ArrayList<WeatherResponse>();
            while(resultSet.next()){
                WeatherResponse tableLine = new WeatherResponse();
                String city = resultSet.getString("city");
                tableLine.setCity(city);
                String date_time = resultSet.getString("date_time");
                tableLine.setDate(date_time);
                String weather_text = resultSet.getString("weather_text");
                tableLine.setWeatherText(weather_text);
                double temperature = resultSet.getDouble("temperature");
                tableLine.setTemperature(temperature);
                resultList.add(tableLine);
            }
            return resultList;
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        throw new IOException("Could not read table from DB");
    }

    public void finalizeDB() {
        try {
            preparedStatement.close();
            statement.close();
            connection.close();
            } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void performDropTable() throws SQLException {
        statement.executeUpdate("DROP TABLE IF EXISTS weather");
    }
}
